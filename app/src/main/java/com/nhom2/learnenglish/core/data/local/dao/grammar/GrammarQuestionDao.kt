package com.nhom2.learnenglish.core.data.local.dao.grammar

import androidx.room3.Dao
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.dao.BaseDao
import com.nhom2.learnenglish.core.data.local.entity.grammar.GrammarQuestionEntity

@Dao
interface GrammarQuestionDao : BaseDao<GrammarQuestionEntity> {

    @Query("SELECT * FROM grammar_questions WHERE lesson_id = :lessonId")
    suspend fun getQuestionsByLessonId(lessonId: Long): List<GrammarQuestionEntity>

    @Query("DELETE FROM grammar_questions")
    suspend fun deleteAll()

}