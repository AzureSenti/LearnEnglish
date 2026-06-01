package com.nhom2.learnenglish.core.data.repository

import com.nhom2.learnenglish.core.data.local.dao.*
import com.nhom2.learnenglish.core.data.local.entity.*

import com.nhom2.learnenglish.core.util.AppExecutors

class StoryRepository (
    executors: AppExecutors = AppExecutors.getInstance(),
    private val storyDao: StoryDao
) : BaseRepository(executors) {

    fun getAll(): List<StoryEntity> {
        return storyDao.getAll()
    }

    fun getById(id: Long): StoryEntity? = storyDao.getById(id)

}
