package com.nhom2.learnenglish.core.data.local.entity.grammar

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "grammar_questions",
    foreignKeys = [
        ForeignKey(
            entity = GrammarLessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lesson_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["lesson_id"])
    ]
)
data class GrammarQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "lesson_id")
    val lessonId: Long,

    @ColumnInfo(name = "question_type")
    val questionType: String,

    @ColumnInfo(name = "question_text")
    val questionText: String,


    @ColumnInfo(name = "options")
    val options: String?,

    @ColumnInfo(name = "correct_answer")
    val correctAnswer: String,

    @ColumnInfo(name = "explanation")
    val explanation: String?
)