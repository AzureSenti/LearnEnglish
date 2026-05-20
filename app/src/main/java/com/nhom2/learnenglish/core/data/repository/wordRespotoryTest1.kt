package com.nhom2.learnenglish.core.data.repository;



import com.nhom2.learnenglish.core.data.local.dao.UserWordSetDao
import com.nhom2.learnenglish.core.data.local.dao.WordDao
import com.nhom2.learnenglish.core.data.local.dao.WordSetDao
import com.nhom2.learnenglish.core.data.local.dao.WordSrsDao
import com.nhom2.learnenglish.core.data.local.entity.UserWordSetCrossRef
import com.nhom2.learnenglish.core.data.local.entity.WordEntity
import com.nhom2.learnenglish.core.data.local.entity.WordSetEntity
import com.nhom2.learnenglish.core.data.local.entity.WordSrsEntity
import com.nhom2.learnenglish.core.data.local.model.WordWithProgress
import com.nhom2.learnenglish.core.util.AppExecutors
import kotlinx.coroutines.runBlocking

class wordRespotoryTest1(
    private val wordDao: WordDao,
    private val wordSetDao: WordSetDao,
    private val wordSrsDao: WordSrsDao,
    private val userWordSetDao: UserWordSetDao,
    executors: AppExecutors = AppExecutors.getInstance()
) : BaseRepository(executors) {

    suspend fun getAllSets(): List<WordSetEntity> {
        return wordSetDao.getAllSets()
    }

    suspend fun getWordsInSet(setId: Long): List<WordEntity> {
        return wordDao.getWordsBySetId(setId)
    }

    suspend fun getWordsForReview(userId: Long): List<WordEntity> {
        val currentTimeMillis = System.currentTimeMillis()
        return wordDao.getWordsDueForReview(userId, currentTimeMillis)
    }

    suspend fun getUnlockedWordSets(userId: Long): List<WordSetEntity> {
        return if (userId == -1L) {
            wordSetDao.getAllSets()
        } else {
            wordSetDao.getUnlockedWordSets(userId)
        }
    }

    suspend fun getWordListWithProgress(setId: Long, userId: Long): List<WordWithProgress> {
        val rawList = wordDao.getWordsWithProgressBySet(setId, userId)
        return rawList.map { item ->
            if (item.level == null) {
                item.copy(level = 0)
            } else {
                item
            }
        }
    }

    suspend fun unlockWordSet(userId: Long, setId: Long): Boolean {
        return try {
            val crossRef = UserWordSetCrossRef(userId = userId, setId = setId)
            userWordSetDao.unlockSet(crossRef)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getNewWordsToLearn(userId: Long, setId: Long): List<WordEntity> {
        return wordDao.getUnlearnedWords(setId, userId, limit = 10)
    }

    suspend fun processWordLearning(userId: Long, wordId: Long, isCorrect: Boolean) {
        val existingSrs = wordSrsDao.getWordSrs(userId, wordId)
        val currentTime = System.currentTimeMillis()

        if (existingSrs == null) {
            val initialLevel = if (isCorrect) 1 else 0
            val interval = calculateInterval(initialLevel)

            val newSrs = WordSrsEntity(
                userId = userId,
                wordId = wordId,
                level = initialLevel,
                lastReviewDate = currentTime,
                nextReviewDate = currentTime + interval,
                isSynced = false
            )
            wordSrsDao.insertOrUpdate(newSrs)

        } else {
            val newLevel = if (isCorrect) existingSrs.level + 1 else maxOf(0, existingSrs.level - 1)
            val interval = calculateInterval(newLevel)

            val updatedSrs = existingSrs.copy(
                level = newLevel,
                lastReviewDate = currentTime,
                nextReviewDate = currentTime + interval,
                isSynced = false
            )
            wordSrsDao.insertOrUpdate(updatedSrs)
        }
    }

    private fun calculateInterval(level: Int): Long {
        val minutes: Long = when (level) {
            0 -> 10
            1 -> 60 * 12
            2 -> 60 * 24
            3 -> 60 * 24 * 3
            4 -> 60 * 24 * 7
            5 -> 60 * 24 * 14
            else -> 60 * 24 * 30
        }
        return minutes * 60 * 1000L
    }

    // =========================================================================
    // CÁC HÀM CẦU NỐI ĐỂ BÊN JAVA GỌI (Tránh bị lỗi cannot find symbol / suspend)
    // =========================================================================

    fun getWordsForReviewForJava(userId: Long): List<WordEntity> = runBlocking {
        getWordsForReview(userId)
    }

    fun getNewWordsToLearnForJava(userId: Long, setId: Long): List<WordEntity> = runBlocking {
        getNewWordsToLearn(userId, setId)
    }

    fun getWordsInSetForJava(setId: Long): List<WordEntity> = runBlocking {
        getWordsInSet(setId)
    }

    fun processWordLearningForJava(userId: Long, wordId: Long, isCorrect: Boolean) = runBlocking {
        processWordLearning(userId, wordId, isCorrect)
    }

//    fun getAllWordsForJava(): List<WordEntity> = runBlocking {
//        wordDao.getAllWords()
//    }
}