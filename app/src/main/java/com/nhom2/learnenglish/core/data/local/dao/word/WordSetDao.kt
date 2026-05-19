package com.nhom2.learnenglish.core.data.local.dao.word

import androidx.room3.Dao
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.dao.BaseDao
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity

@Dao
interface WordSetDao : BaseDao<WordSetEntity> {

    @Query("SELECT * FROM word_sets")
    suspend fun getAllSets(): List<WordSetEntity>

    @Query("SELECT * FROM word_sets WHERE id = :id")
    suspend fun getSetById(id: Long): WordSetEntity?

    @Query("""
        SELECT ws.* FROM word_sets ws
        LEFT JOIN user_word_set_cross_ref ref ON ws.id = ref.set_id AND ref.user_id = :userId
        WHERE ws.unlock_cost <= 0 OR ref.set_id IS NOT NULL
    """)
    suspend fun getUnlockedWordSets(userId: Long): List<WordSetEntity>

    @Query("DELETE FROM word_sets")
    suspend fun deleteAll()
}