package com.nhom2.learnenglish.core.data.local.dao


import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Update
import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity


interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<T>): List<Long>

    @Update
    suspend fun update(entity: T): Int

    @Delete
    suspend fun delete(entity: T): Int

    abstract suspend fun getAll(): List<T>

    abstract suspend fun getById(id: Long): T?

    abstract suspend fun deleteAll()
}
