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
    fun getWordSrs(userId: String, wordId: String): WordSrsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(srs: WordSrsEntity)

    // --- Sync queries ---

    @Query("SELECT * FROM word_srs WHERE user_id = :userId AND is_synced = 0")
    fun getUnsyncedProgress(userId: String): List<WordSrsEntity>

    @Query("UPDATE word_srs SET is_synced = 1 WHERE user_id = :userId")
    fun markAllAsSynced(userId: String)

    @Query("SELECT * FROM word_srs WHERE user_id = :userId")
    fun getAllForUser(userId: String): List<WordSrsEntity>

    @Query("DELETE FROM word_srs WHERE user_id = :userId")
    fun deleteAllForUser(userId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateAll(list: List<WordSrsEntity>)

    @Query("UPDATE word_srs SET user_id = :newUserId WHERE user_id = :oldUserId")
    fun migrateUserId(oldUserId: String, newUserId: String)
}