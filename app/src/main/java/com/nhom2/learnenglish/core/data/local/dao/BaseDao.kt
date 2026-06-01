package com.nhom2.learnenglish.core.data.local.dao


import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Update


interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entities: List<T>): List<Long>

    @Update
    fun update(entity: T): Int

    @Delete
    fun delete(entity: T): Int

}
