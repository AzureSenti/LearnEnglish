package com.nhom2.learnenglish.core.data.local.entity.grammar

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import com.nhom2.learnenglish.core.data.local.entity.UserEntity

@Entity(
    tableName = "user_grammar_progress",
    primaryKeys = ["user_id", "lesson_id"],
    foreignKeys = [
        //ForeignKey(
        // entity = UserEntity::class,
        // parentColumns = ["id"],
        //    childColumns = ["user_id"],
        //    onDelete = ForeignKey.Companion.CASCADE
        //),
        ForeignKey(
            entity = GrammarLessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lesson_id"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["lesson_id"])
    ]
)
data class UserGrammarProgress(
    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "lesson_id")
    val lessonId: Long,

    @ColumnInfo(name = "is_theory_completed")
    val isTheoryCompleted: Boolean = false,

    @ColumnInfo(name = "is_quiz_passed")
    val isQuizPassed: Boolean = false,

    @ColumnInfo(name = "score")
    val score: Int = 0
)