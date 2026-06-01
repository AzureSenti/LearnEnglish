package com.nhom2.learnenglish.core.data.repository


import com.nhom2.learnenglish.core.data.local.dao.*
import com.nhom2.learnenglish.core.data.local.entity.*

import com.nhom2.learnenglish.core.util.AppExecutors

class ArticleRepository (
    executors: AppExecutors = AppExecutors.getInstance(),
    private val articleDao: ArticleDao
) : BaseRepository(executors) {

    fun getAll(): List<ArticleEntity> {
        return articleDao.getAll()
    }

    fun getById(id: Long): ArticleEntity? = articleDao.getById(id)

}