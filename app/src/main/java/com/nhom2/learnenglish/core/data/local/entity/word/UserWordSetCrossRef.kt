package com.nhom2.learnenglish.core.data.local.entity.word

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import com.nhom2.learnenglish.core.data.local.entity.UserEntity

@Entity(
    tableName = "user_word_set_cross_ref",
    primaryKeys = ["user_id", "set_id"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WordSetEntity::class,
            parentColumns = ["id"],
            childColumns = ["set_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["set_id"])
    ]
)
data class UserWordSetCrossRef(
    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "set_id")
    val setId: Long,

    @ColumnInfo(name = "unlocked_at")
    val unlockedAt: Long? = System.currentTimeMillis()
)