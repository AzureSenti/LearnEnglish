
package com.nhom2.learnenglish.core.data.local.entity.word

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import com.nhom2.learnenglish.core.data.local.entity.UserEntity


@Entity(
    tableName = "word_srs",
    primaryKeys = ["word_id", "user_id"],

    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["word_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["word_id"]),
        Index(value = ["next_review_date"])
    ]
)

data class WordSrsEntity(

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "word_id")
    val wordId: Long,

    @ColumnInfo(name = "level", defaultValue = "0")
    val level: Int,

    @ColumnInfo(name = "next_review_date",defaultValue = "0")
    val nextReviewDate: Long,

    @ColumnInfo(name = "last_review_date")
    val lastReviewDate: Long? = null,

    @ColumnInfo(name = "is_synced", defaultValue = "0")
    val isSynced: Boolean = false

)