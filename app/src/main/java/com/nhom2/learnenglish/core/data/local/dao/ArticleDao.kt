package com.nhom2.learnenglish.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity
import com.nhom2.learnenglish.core.data.local.entity.WordEntity


@Dao
interface ArticleDao : BaseDao<ArticleEntity> {

    @Query("SELECT * FROM article WHERE id = :id")
    suspend fun getById(id: Long): WordEntity?

    @Query("Select * from article")
    suspend fun getAll(): List<ArticleEntity>

}