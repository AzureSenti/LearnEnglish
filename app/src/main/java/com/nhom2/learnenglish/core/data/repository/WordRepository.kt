package com.nhom2.learnenglish.core.data.repository

import com.nhom2.learnenglish.core.data.local.dao.word.UserWordSetDao
import com.nhom2.learnenglish.core.data.local.dao.word.WordDao
import com.nhom2.learnenglish.core.data.local.dao.word.WordSetCrossDao
import com.nhom2.learnenglish.core.data.local.dao.word.WordSetDao
import com.nhom2.learnenglish.core.data.local.dao.word.WordSrsDao
import com.nhom2.learnenglish.core.data.local.entity.word.UserWordSetCrossRef
import com.nhom2.learnenglish.core.data.local.entity.word.WordEntity
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetCrossRef
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity
import com.nhom2.learnenglish.core.data.local.entity.word.WordSrsEntity
import com.nhom2.learnenglish.core.data.model.WordWithProgress
import com.nhom2.learnenglish.core.util.AppExecutors

class WordRepository(
    private val wordDao: WordDao,
    private val wordSrsDao: WordSrsDao,
    private val userWordSetDao: UserWordSetDao,
    private val wordSetDao: WordSetDao,
    private val wordSetCrossDao: WordSetCrossDao,
    executors: AppExecutors = AppExecutors.getInstance()
) : BaseRepository(executors) {

    fun getAllSets(): List<WordSetEntity> {
        return wordSetDao.getAllSets()
    }

    fun getSetById(setId: Long): WordSetEntity? {
        return wordSetDao.getSetById(setId)
    }

    fun createSet(set: WordSetEntity) {
        wordSetDao.insert(set)
    }

    fun updateSet(set: WordSetEntity) {
        wordSetDao.update(set)
    }
    fun addWordToSet(wordId: Long, setId: Long) {
        wordSetCrossDao.insert(WordSetCrossRef(wordId = wordId, setId = setId))
    }

    fun  removeWordFromSet(wordId: Long, setId: Long) {
        wordSetCrossDao.removeWordFromSet(wordId, setId)
    }

    fun getWordsInSet(setId: Long): List<WordEntity> {
        return wordDao.getWordsBySetId(setId)
    }

    fun getWordsForReview(userId: Long): List<WordEntity> {
        val currentTimeMillis = System.currentTimeMillis()
        return wordDao.getWordsDueForReview(userId, currentTimeMillis)
    }

    fun getUnlockedWordSets(userId: Long): List<WordSetEntity> {
        return if (userId == -1L) {
            wordSetDao.getAllSets()
        } else {
            wordSetDao.getUnlockedWordSets(userId)
        }
    }


    fun getWordListWithProgress(setId: Long, userId: Long): List<WordWithProgress> {
        val rawList = wordDao.getWordsWithProgressBySet(setId, userId)

        return rawList.map { item ->
            if (item.level == null) {
                item.copy(level = 0)
            } else {
                item
            }
        }
    }

    fun unlockWordSet(userId: Long, setId: Long): Boolean {
        try {
            val crossRef = UserWordSetCrossRef(userId = userId, setId = setId)
            userWordSetDao.unlockSet(crossRef)
            return true
        } catch (e: Exception) {
            e.printStackTrace();
            return false
        }
    }

    fun getNewWordsToLearn(userId: Long, setId: Long): List<WordEntity> {
        return wordDao.getUnlearnedWords(setId, userId, limit = 10)
    }

    fun processWordLearning(userId: Long, wordId: Long, isCorrect: Boolean) {
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
        // THỜI GIAN TEST (Tính bằng phút)
        val minutes: Long = when (level) {
            0 -> 1  // Sai hoặc chưa thuộc: Chờ 1 phút để ôn lại
            1 -> 1  // Thuộc level 1: Chờ 1 phút (thay vì 12 tiếng)
            2 -> 2  // Thuộc level 2: Chờ 2 phút (thay vì 1 ngày)
            3 -> 3  // Thuộc level 3: Chờ 3 phút (thay vì 3 ngày)
            4 -> 4  // Thuộc level 4: Chờ 4 phút (thay vì 7 ngày)
            5 -> 5  // Thuộc level 5: Chờ 5 phút (thay vì 14 ngày)
            else -> 6 // Từ level 6 trở lên: Chờ 6 phút (thay vì 30 ngày)
        }
        return minutes * 60 * 1000L // Nhân với 60 * 1000 để đổi từ phút sang milliseconds
    }

    fun getAllWords(): List<WordEntity> {
        return wordDao.getAll()
    }

    fun getWordById(wordId: Long): WordEntity? {
        return wordDao.getById(wordId)
    }



}