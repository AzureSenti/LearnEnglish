package com.nhom2.learnenglish.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import com.nhom2.learnenglish.core.data.local.entity.WordSrsEntity

@Dao
interface WordSrsDao : BaseDao<WordSrsEntity> {

    @Query("SELECT * FROM word_srs WHERE user_id = :userId AND word_id = :wordId")
    suspend fun getWordSrs(userId: Long, wordId: Long): WordSrsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(srs: WordSrsEntity)
}