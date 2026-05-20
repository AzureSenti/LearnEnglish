package com.nhom2.learnenglish.core.data.local.dao.word

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.dao.BaseDao
import com.nhom2.learnenglish.core.data.local.entity.word.WordSrsEntity

@Dao
interface WordSrsDao : BaseDao<WordSrsEntity> {

    @Query("SELECT * FROM word_srs WHERE user_id = :userId AND word_id = :wordId")
    fun getWordSrs(userId: Long, wordId: Long): WordSrsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(srs: WordSrsEntity)
}