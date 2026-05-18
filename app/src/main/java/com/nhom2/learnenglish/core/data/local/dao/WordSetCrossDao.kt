package com.nhom2.learnenglish.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.entity.WordSetCrossRef

@Dao
interface WordSetCrossDao : BaseDao<WordSetCrossRef> {

    @Query("DELETE FROM word_set_cross_ref WHERE word_id = :wordId AND set_id = :setId")
    suspend fun removeWordFromSet(wordId: Long, setId: Long)
}