package com.nhom2.learnenglish.core.data.repository

import com.nhom2.learnenglish.core.data.local.dao.UserWordSetDao
import com.nhom2.learnenglish.core.data.local.dao.WordDao
import com.nhom2.learnenglish.core.data.local.dao.WordSetDao
import com.nhom2.learnenglish.core.data.local.dao.WordSrsDao
import com.nhom2.learnenglish.core.data.local.entity.WordEntity
import com.nhom2.learnenglish.core.data.local.entity.WordSetEntity
import com.nhom2.learnenglish.core.data.local.entity.WordSrsEntity
import com.nhom2.learnenglish.core.data.local.model.WordWithProgress
import com.nhom2.learnenglish.core.util.AppExecutors

class WordRepository(
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
}