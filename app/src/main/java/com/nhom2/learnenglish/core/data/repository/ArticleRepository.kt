package com.nhom2.learnenglish.core.data.repository


import com.nhom2.learnenglish.core.data.local.dao.*
import com.nhom2.learnenglish.core.data.local.entity.*

import com.nhom2.learnenglish.core.util.AppExecutors

class ArticleRepository (
    private val articleDao: ArticleDao,

    executors: AppExecutors = AppExecutors.getInstance()
) : BaseRepository(executors) {

    suspend fun getAllArticles(): List<ArticleEntity> {
        return articleDao.getAll()
    }

    suspend fun getWordById(id: Long): WordEntity? = articleDao.getById(id)

}