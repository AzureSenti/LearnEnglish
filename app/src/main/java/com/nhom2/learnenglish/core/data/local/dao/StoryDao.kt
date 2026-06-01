package com.nhom2.learnenglish.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.entity.StoryEntity

@Dao
interface StoryDao : BaseDao<StoryEntity> {

    @Query("SELECT * FROM story WHERE id = :id")
    fun getById(id: Long): StoryEntity?

    @Query("Select * from story")
    fun getAll(): List<StoryEntity>

    @Query("DELETE FROM story")
    fun deleteAll()
}
