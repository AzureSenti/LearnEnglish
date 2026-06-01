package com.nhom2.learnenglish.core.data.local.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "article",
)
data class ArticleEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "category")
    val category: String? = null,

    @ColumnInfo(name = "level")
    val level: String? = null,

    @ColumnInfo(name = "read_time")
    val readTime: String? = null,

    @ColumnInfo(name = "image")
    val image: String? = null,

    @ColumnInfo(name = "author")
    val author: String? = null,

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
)