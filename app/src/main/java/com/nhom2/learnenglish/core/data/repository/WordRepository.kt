package com.nhom2.learnenglish.core.data.repository

import com.nhom2.learnenglish.core.data.local.dao.UserWordSetDao
import com.nhom2.learnenglish.core.data.local.dao.WordDao
import com.nhom2.learnenglish.core.data.local.dao.WordSetCrossDao
import com.nhom2.learnenglish.core.data.local.dao.WordSetDao
import com.nhom2.learnenglish.core.data.local.dao.WordSrsDao
import com.nhom2.learnenglish.core.data.local.entity.UserWordSetCrossRef
import com.nhom2.learnenglish.core.data.local.entity.WordEntity
import com.nhom2.learnenglish.core.data.local.entity.WordSetCrossRef
import com.nhom2.learnenglish.core.data.local.entity.WordSetEntity
import com.nhom2.learnenglish.core.data.local.entity.WordSrsEntity
import com.nhom2.learnenglish.core.data.local.model.WordWithProgress
import com.nhom2.learnenglish.core.util.AppExecutors

class WordRepository(
    private val wordDao: WordDao,
    private val wordSetDao: WordSetDao,
    private val wordSrsDao: WordSrsDao,
    private val userWordSetDao: UserWordSetDao,
    private val wordSetCrossDao: WordSetCrossDao,
    executors: AppExecutors = AppExecutors.getInstance()
) : BaseRepository(executors) {

    suspend fun getAllSets(): List<WordSetEntity> {
        return wordSetDao.getAllSets()
    }

    suspend fun getSetById(id: Long): WordSetEntity? {
        return wordSetDao.getSetById(id)
    }

    suspend fun getWordsInSet(setId: Long): List<WordEntity> {
        return wordDao.getWordsBySetId(setId)
    }

    suspend fun getAllWords(): List<WordEntity> {
        return wordDao.getAllWords()
    }

    suspend fun findWordByEnglish(word: String): WordEntity? {
        return wordDao.findByEnglishWord(word.trim())
    }

    suspend fun buildVocabularyLookupMap(): Map<String, WordEntity> {
        return wordDao.getAllWords().associateBy { it.englishWord.lowercase() }
    }

    suspend fun createWordSet(name: String, description: String?, iconCategory: String = "folder"): Long {
        val entity = WordSetEntity(
            name = name.trim(),
            description = description?.trim()?.takeIf { it.isNotEmpty() },
            unlockCost = 0,
            iconCategory = iconCategory
        )
        return wordSetDao.insert(entity)
    }

    suspend fun getWordCountsBySet(): Map<Long, Int> {
        val sets = wordSetDao.getAllSets()
        return sets.associate { it.id to wordSetDao.countWordsInSet(it.id) }
    }

    suspend fun getAllSetsForUi(): List<WordSetEntity> = wordSetDao.getAllSets()

    suspend fun updateWordSet(set: WordSetEntity) {
        wordSetDao.update(set)
    }

    suspend fun deleteWordSet(setId: Long) {
        wordSetDao.getSetById(setId)?.let { wordSetDao.delete(it) }
    }

    suspend fun addWordToSet(
        setId: Long,
        englishWord: String,
        vietnameseMeaning: String,
        audio: String? = null
    ): Long {
        val normalized = englishWord.trim()
        val existing = wordDao.findByEnglishWord(normalized)
        val wordId = if (existing != null) {
            val updated = existing.copy(
                vietnameseMeaning = vietnameseMeaning.trim(),
                audio = audio ?: existing.audio
            )
            wordDao.update(updated)
            existing.id
        } else {
            wordDao.insert(
                WordEntity(
                    englishWord = normalized,
                    vietnameseMeaning = vietnameseMeaning.trim(),
                    audio = audio
                )
            )
        }
        val wordsInSet = wordDao.getWordsBySetId(setId)
        if (wordsInSet.none { it.id == wordId }) {
            wordSetCrossDao.insert(WordSetCrossRef(wordId = wordId, setId = setId))
        }
        return wordId
    }

    suspend fun updateWord(word: WordEntity) {
        wordDao.update(word)
    }

    suspend fun removeWordFromSet(setId: Long, wordId: Long) {
        wordSetCrossDao.removeWordFromSet(wordId, setId)
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
        try {
            val crossRef = UserWordSetCrossRef(userId = userId, setId = setId)
            userWordSetDao.unlockSet(crossRef)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
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
}
