package com.nhom2.learnenglish.core.data.local.dao.grammar

import androidx.room3.Dao
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.dao.BaseDao
import com.nhom2.learnenglish.core.data.local.entity.grammar.GrammarLessonEntity

@Dao
interface GrammarLessonDao : BaseDao<GrammarLessonEntity> {

    // Lấy tất cả bài học, sắp xếp theo Lộ trình (orderIndex)
    @Query("SELECT * FROM grammar_lessons ORDER BY order_index ASC")
    suspend fun getAllLessons(): List<GrammarLessonEntity>

    @Query("SELECT * FROM grammar_lessons WHERE id = :id")
    suspend fun getLessonById(id: Long): GrammarLessonEntity?

    @Query("DELETE FROM grammar_lessons")
    suspend fun deleteAll()

}