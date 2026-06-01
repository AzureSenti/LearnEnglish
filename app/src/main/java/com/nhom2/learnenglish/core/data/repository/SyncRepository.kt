package com.nhom2.learnenglish.core.data.repository

import android.util.Log
import com.nhom2.learnenglish.core.data.local.dao.UserDao
import com.nhom2.learnenglish.core.data.local.dao.grammar.UserGrammarProgressDao
import com.nhom2.learnenglish.core.data.local.dao.word.UserWordSetDao
import com.nhom2.learnenglish.core.data.local.dao.word.WordSrsDao
import com.nhom2.learnenglish.core.data.local.entity.UserEntity
import com.nhom2.learnenglish.core.data.local.entity.grammar.UserGrammarProgress
import com.nhom2.learnenglish.core.data.local.entity.word.UserWordSetCrossRef
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetCrossRef
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity
import com.nhom2.learnenglish.core.data.local.entity.word.WordSrsEntity
import com.nhom2.learnenglish.core.network.sync.GrammarProgressUploadItem
import com.nhom2.learnenglish.core.network.sync.SyncApi
import com.nhom2.learnenglish.core.network.sync.SyncUploadRequest
import com.nhom2.learnenglish.core.network.sync.UserProfileUploadItem
import com.nhom2.learnenglish.core.network.sync.UserWordSetUploadItem
import com.nhom2.learnenglish.core.network.sync.WordSetCrossRefSyncItem
import com.nhom2.learnenglish.core.network.sync.WordSetSyncItem
import com.nhom2.learnenglish.core.network.sync.WordSrsUploadItem
import com.nhom2.learnenglish.core.network.sync.WordSyncItem
import com.nhom2.learnenglish.core.util.AppExecutors
import com.nhom2.learnenglish.core.util.SessionManager

/**
 * Handles two-way data synchronization between local Room DB and the remote server.
 *
 * Sync flow:
 * 1. Collect unsynced local data
 * 2. Upload to server (POST /sync/upload)
 * 3. Mark local data as synced
 * 4. Download latest from server (GET /sync/download)
 * 5. Merge server data into local DB
 */
class SyncRepository(
    private val syncApi: SyncApi,
    private val wordDao: com.nhom2.learnenglish.core.data.local.dao.word.WordDao,
    private val wordSetDao: com.nhom2.learnenglish.core.data.local.dao.word.WordSetDao,
    private val wordSetCrossDao: com.nhom2.learnenglish.core.data.local.dao.word.WordSetCrossDao,
    private val wordSrsDao: WordSrsDao,
    private val grammarProgressDao: UserGrammarProgressDao,
    private val userWordSetDao: UserWordSetDao,
    private val userDao: UserDao,
    private val deletedSyncItemDao: com.nhom2.learnenglish.core.data.local.dao.sync.DeletedSyncItemDao,
    private val sessionManager: SessionManager,
    executors: AppExecutors = AppExecutors.getInstance()
) : BaseRepository(executors) {

    companion object {
        private const val TAG = "SyncRepository"
        private const val GUEST_USER_ID = "-1"
    }

    /**
     * Perform a full two-way sync for the currently logged-in user.
     *
     * @return true if sync completed successfully, false otherwise.
     */
    suspend fun performFullSync(): Boolean {
        val token = sessionManager.fetchAuthToken() ?: run {
            Log.w(TAG, "No auth token, skipping sync")
            return false
        }
        val userId = sessionManager.getCurrentUserId()
        if (userId == GUEST_USER_ID) {
            Log.w(TAG, "Guest user, skipping sync")
            return false
        }

        val bearerToken = "Bearer $token"

        return try {
            // --- STEP 1: Upload unsynced local data ---
            uploadUnsyncedData(userId, bearerToken)

            // --- STEP 2: Download & merge server data ---
            downloadAndMerge(userId, bearerToken)

            Log.i(TAG, "Full sync completed successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed", e)
            false
        }
    }

    /**
     * Collect all unsynced records from Room and push them to the server.
     */
    private suspend fun uploadUnsyncedData(userId: String, bearerToken: String) {
        // 1. Collect unsynced word SRS progress
        val unsyncedWordSrs = wordSrsDao.getUnsyncedProgress(userId)
        val wordSrsUpload = unsyncedWordSrs.map { srs ->
            WordSrsUploadItem(
                wordId = srs.wordId,
                level = srs.level,
                nextReviewDate = srs.nextReviewDate,
                lastReviewDate = srs.lastReviewDate
            )
        }

        // 2. Collect unsynced grammar progress
        val unsyncedGrammar = grammarProgressDao.getUnsyncedProgress(userId)
        val grammarUpload = unsyncedGrammar.map { gp ->
            GrammarProgressUploadItem(
                lessonId = gp.lessonId.toInt(),
                isTheoryCompleted = gp.isTheoryCompleted,
                isQuizPassed = gp.isQuizPassed,
                score = gp.score
            )
        }

        // 3. Collect unlocked word sets
        val activeUser = userDao.getActiveUser()
        val userLocalId = activeUser?.id ?: 0L
        val unlockedSetIds = userWordSetDao.getUnlockedSetIds(userLocalId)
        val wordSetUpload = unlockedSetIds.map { setId ->
            UserWordSetUploadItem(setId = setId)
        }

        // 4. Collect user profile
        val profileUpload = activeUser?.let {
            UserProfileUploadItem(
                coins = it.coins,
                currentStreak = it.currentStreak,
                longestStreak = it.longestStreak
            )
        }

        // 5. Custom words
        val unsyncedWords = wordDao.getUnsyncedWords()
        val wordsUpload = unsyncedWords.map { w ->
            WordSyncItem(
                wordId = w.id,
                englishWord = w.englishWord,
                vietnameseMeaning = w.vietnameseMeaning,
                audio = w.audio
            )
        }

        // 6. Custom word sets
        val unsyncedWordSets = wordSetDao.getUnsyncedSets()
        val wordSetsUpload = unsyncedWordSets.map { ws ->
            WordSetSyncItem(
                setId = ws.id,
                name = ws.name,
                unlockCost = ws.unlockCost
            )
        }

        // 7. Custom word set cross refs
        val unsyncedCrossRefs = wordSetCrossDao.getUnsyncedCrossRefs()
        val crossRefsUpload = unsyncedCrossRefs.map { cr ->
            WordSetCrossRefSyncItem(
                wordId = cr.wordId,
                setId = cr.setId
            )
        }

        // 8. Deleted items
        val unsyncedDeletedItems = deletedSyncItemDao.getAll()
        val deletedWordSetIds = unsyncedDeletedItems.filter { it.itemType == "WORD_SET" }.map { it.primaryId }
        val deletedCrossRefs = unsyncedDeletedItems.filter { it.itemType == "CROSS_REF" }.map { 
            WordSetCrossRefSyncItem(wordId = it.primaryId, setId = it.secondaryId ?: "")
        }

        // Skip upload if nothing to sync
        if (wordSrsUpload.isEmpty() && grammarUpload.isEmpty() &&
            wordSetUpload.isEmpty() && profileUpload == null &&
            wordsUpload.isEmpty() && wordSetsUpload.isEmpty() && crossRefsUpload.isEmpty() &&
            deletedWordSetIds.isEmpty() && deletedCrossRefs.isEmpty()
        ) {
            Log.d(TAG, "Nothing to upload, skipping")
            return
        }

        // Upload
        val request = SyncUploadRequest(
            wordsList = wordsUpload,
            wordSetsList = wordSetsUpload,
            wordSetCrossRefsList = crossRefsUpload,
            wordSrsList = wordSrsUpload,
            grammarProgressList = grammarUpload,
            unlockedWordSets = wordSetUpload,
            userProfile = profileUpload,
            deletedWordSetIds = deletedWordSetIds,
            deletedWordSetCrossRefs = deletedCrossRefs
        )

        val response = syncApi.uploadProgress(bearerToken, request)
        if (response.isSuccessful) {
            // Mark all local data as synced
            wordDao.markAllAsSynced()
            wordSetDao.markAllAsSynced()
            wordSetCrossDao.markAllAsSynced()
            wordSrsDao.markAllAsSynced(userId)
            grammarProgressDao.markAllAsSynced(userId)
            deletedSyncItemDao.deleteAll()
            Log.i(TAG, "Upload successful: ${wordSrsUpload.size} SRS, ${grammarUpload.size} grammar, ${wordSetUpload.size} word sets, ${unsyncedDeletedItems.size} deleted items")
        } else {
            Log.e(TAG, "Upload failed: ${response.code()} ${response.message()}")
            throw Exception("Upload failed: ${response.code()}")
        }
    }

    /**
     * Download the latest data from the server and merge into local Room DB.
     */
    private suspend fun downloadAndMerge(userId: String, bearerToken: String) {
        val response = syncApi.downloadProgress(bearerToken)
        if (!response.isSuccessful) {
            Log.e(TAG, "Download failed: ${response.code()} ${response.message()}")
            throw Exception("Download failed: ${response.code()}")
        }

        val data = response.body() ?: throw Exception("Download returned null body")

        // 1. Update user profile from server
        val activeUser = userDao.getActiveUser()
        if (activeUser != null) {
            val updatedUser = activeUser.copy(
                coins = data.userProfile.coins,
                currentStreak = data.userProfile.currentStreak,
                longestStreak = data.userProfile.longestStreak,
                fullName = data.userProfile.fullName,
                avatarUrl = data.userProfile.avatarUrl,
                isSynced = true
            )
            userDao.update(updatedUser)
        }

        // 2. Merge custom words
        val serverWords = data.wordsList.map { item ->
            com.nhom2.learnenglish.core.data.local.entity.word.WordEntity(
                id = item.wordId,
                englishWord = item.englishWord,
                vietnameseMeaning = item.vietnameseMeaning,
                audio = item.audio,
                isSynced = true
            )
        }
        if (serverWords.isNotEmpty()) {
            wordDao.insertOrUpdateAll(serverWords)
        }

        // 3. Merge custom word sets
        val serverWordSets = data.wordSetsList.map { item ->
            WordSetEntity(
                id = item.setId,
                name = item.name,
                unlockCost = item.unlockCost,
                isSynced = true
            )
        }
        if (serverWordSets.isNotEmpty()) {
            wordSetDao.insertOrUpdateAll(serverWordSets)
        }

        // 4. Merge custom word set cross refs
        val serverCrossRefs = data.wordSetCrossRefsList.map { item ->
            WordSetCrossRef(
                wordId = item.wordId,
                setId = item.setId,
                isSynced = true
            )
        }
        if (serverCrossRefs.isNotEmpty()) {
            wordSetCrossDao.insertOrUpdateAll(serverCrossRefs)
        }

        // 5. Merge unlocked word sets
        val userLocalId = activeUser?.id ?: 0L
        val serverUnlockedSets = data.unlockedWordSetIds.map { setId ->
            UserWordSetCrossRef(
                userId = userLocalId,
                setId = setId
            )
        }
        if (serverUnlockedSets.isNotEmpty()) {
            userWordSetDao.insertAll(serverUnlockedSets)
        }

        // 6. Merge word SRS data (MUST happen after words are merged to prevent CASCADE deletion)
        val serverWordSrs = data.wordSrsList.map { item ->
            WordSrsEntity(
                userId = userId,
                wordId = item.wordId,
                level = item.level,
                nextReviewDate = item.nextReviewDate,
                lastReviewDate = item.lastReviewDate,
                isSynced = true
            )
        }
        if (serverWordSrs.isNotEmpty()) {
            wordSrsDao.insertOrUpdateAll(serverWordSrs)
        }

        // 7. Merge grammar progress
        val serverGrammar = data.grammarProgressList.map { item ->
            UserGrammarProgress(
                userId = userId,
                lessonId = item.lessonId.toLong(),
                isTheoryCompleted = item.isTheoryCompleted,
                isQuizPassed = item.isQuizPassed,
                score = item.score,
                isSynced = true
            )
        }
        if (serverGrammar.isNotEmpty()) {
            grammarProgressDao.insertOrUpdateAll(serverGrammar)
        }

        Log.i(TAG, "Download & merge complete: ${serverWordSrs.size} SRS, " +
                "${serverGrammar.size} grammar, ${serverUnlockedSets.size} word sets")
    }

    /**
     * Migrate guest (userId="-1") learning data to the real user ID.
     * This should be called right after login, before the full sync.
     */
    fun mergeGuestData(realUserId: String) {
        try {
            // Migrate word SRS records from guest to real user
            wordSrsDao.migrateUserId(GUEST_USER_ID, realUserId)

            // Migrate grammar progress records from guest to real user
            grammarProgressDao.migrateUserId(GUEST_USER_ID, realUserId)

            Log.i(TAG, "Guest data migrated to user: $realUserId")
        } catch (e: Exception) {
            Log.e(TAG, "Guest data migration failed", e)
        }
    }
}
