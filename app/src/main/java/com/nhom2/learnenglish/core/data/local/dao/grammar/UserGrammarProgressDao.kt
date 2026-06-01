package com.nhom2.learnenglish.core.data.local.dao.grammar

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.dao.BaseDao
import com.nhom2.learnenglish.core.data.local.entity.grammar.UserGrammarProgress

@Dao
interface UserGrammarProgressDao : BaseDao<UserGrammarProgress> {

    @Query("SELECT * FROM user_grammar_progress WHERE user_id = :userId AND lesson_id = :lessonId")
    fun getProgress(userId: String, lessonId: Long): UserGrammarProgress?

    @Query("SELECT * FROM user_grammar_progress WHERE user_id = :userId")
    fun getAllProgressForUser(userId: String): List<UserGrammarProgress>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(progress: UserGrammarProgress)

    // --- Sync queries ---

    @Query("SELECT * FROM user_grammar_progress WHERE user_id = :userId AND is_synced = 0")
    fun getUnsyncedProgress(userId: String): List<UserGrammarProgress>

    @Query("UPDATE user_grammar_progress SET is_synced = 1 WHERE user_id = :userId")
    fun markAllAsSynced(userId: String)

    @Query("DELETE FROM user_grammar_progress WHERE user_id = :userId")
    fun deleteAllForUser(userId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateAll(list: List<UserGrammarProgress>)

    @Query("UPDATE user_grammar_progress SET user_id = :newUserId WHERE user_id = :oldUserId")
    fun migrateUserId(oldUserId: String, newUserId: String)
}