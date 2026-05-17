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
    suspend fun getProgress(userId: Long, lessonId: Long): UserGrammarProgress?

    @Query("SELECT * FROM user_grammar_progress WHERE user_id = :userId")
    suspend fun getAllProgressForUser(userId: Long): List<UserGrammarProgress>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: UserGrammarProgress)
}