package com.nhom2.learnenglish.core.data.local.dao.word

import androidx.room3.Dao
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.dao.BaseDao
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetCrossRef

@Dao
interface WordSetCrossDao : BaseDao<WordSetCrossRef> {
    @Query("DELETE FROM word_set_cross_ref")
    suspend fun deleteAll()
}