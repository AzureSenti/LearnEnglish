package com.nhom2.learnenglish.core.data.repository

import com.nhom2.learnenglish.core.data.local.dao.word.UserWordSetDao
import com.nhom2.learnenglish.core.data.local.dao.word.WordDao
import com.nhom2.learnenglish.core.data.local.dao.word.WordSetDao
import com.nhom2.learnenglish.core.data.local.dao.word.WordSrsDao
import com.nhom2.learnenglish.core.data.local.entity.word.UserWordSetCrossRef
import com.nhom2.learnenglish.core.data.local.entity.word.WordEntity
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity
import com.nhom2.learnenglish.core.data.local.entity.word.WordSrsEntity
import com.nhom2.learnenglish.core.data.local.model.WordWithProgress
import com.nhom2.learnenglish.core.util.AppExecutors

class WordRepository(
    private val wordDao: WordDao,
    private val wordSetDao: WordSetDao,
    private val wordSrsDao: WordSrsDao,
    private val userWordSetDao: UserWordSetDao,
    executors: AppExecutors = AppExecutors.getInstance()
) : BaseRepository(executors) {

    fun getAllSets(): List<WordSetEntity> {
        return wordSetDao.getAllSets()
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

    fun getAllWords(): List<WordEntity> {
        return wordDao.getAll()
    }

    fun getWordById(wordId: Long): WordEntity? {
        return wordDao.getById(wordId)
    }



}