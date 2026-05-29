package com.nhom2.learnenglish.core.data.local.dao.word

import androidx.room3.Dao
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.dao.BaseDao
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity

@Dao
interface WordSetDao : BaseDao<WordSetEntity> {

    @Query("SELECT * FROM word_sets")
    fun getAllSets(): List<WordSetEntity>

    @Query("SELECT * FROM word_sets WHERE id = :id")
    fun getSetById(id: Long): WordSetEntity?

    @Query("""
        SELECT ws.* FROM word_sets ws
        LEFT JOIN user_word_set_cross_ref ref ON ws.id = ref.set_id AND ref.user_id = :userId
        WHERE ws.unlock_cost <= 0 OR ref.set_id IS NOT NULL
    """)
    fun getUnlockedWordSets(userId: Long): List<WordSetEntity>

    @Query("DELETE FROM word_sets")
    fun deleteAll()

    @Query("SELECT * FROM word_sets WHERE is_synced = 0")
    fun getUnsyncedSets(): List<WordSetEntity>

    @Query("UPDATE word_sets SET is_synced = 1")
    fun markAllAsSynced()

    @androidx.room3.Insert(onConflict = androidx.room3.OnConflictStrategy.REPLACE)
    fun insertOrUpdateAll(wordSets: List<WordSetEntity>)
}