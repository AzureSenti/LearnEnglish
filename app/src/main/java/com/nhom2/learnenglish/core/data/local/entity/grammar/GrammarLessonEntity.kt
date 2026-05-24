package com.nhom2.learnenglish.core.data.local.entity.grammar

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "grammar_lessons")
data class GrammarLessonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "theory_basics")
    val theoryBasics: String,

    @ColumnInfo(name = "usage_rules")
    val usageRules: String,

    @ColumnInfo(name = "examples")
    val examples: String,

    @ColumnInfo(name = "order_index")
    val orderIndex: Int
)