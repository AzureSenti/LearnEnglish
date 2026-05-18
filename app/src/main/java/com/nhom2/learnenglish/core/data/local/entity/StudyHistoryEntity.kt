package com.nhom2.learnenglish.core.data.local.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "study_history",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["timestamp"])
    ]
)

data class StudyHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "activity_type")
    val activityType: String,

    @ColumnInfo(name = "xp_earned", defaultValue = "0")
    val xpEarned: Int = 0,

    @ColumnInfo(name = "streak_day", defaultValue = "0")
    val streakDay: Int = 0,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "session_duration_seconds", defaultValue = "0")
    val sessionDurationSeconds: Int = 0,

    @ColumnInfo(name = "notes")
    val notes: String? = null
)
