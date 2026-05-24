package com.nhom2.learnenglish.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity


@Dao
interface ArticleDao : BaseDao<ArticleEntity> {

    @Query("SELECT * FROM article WHERE id = :id")
    fun getById(id: Long): ArticleEntity?

    @Query("Select * from article")
    fun getAll(): List<ArticleEntity>

    @Query("DELETE FROM article")
    fun deleteAll()
}


