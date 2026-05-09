package com.nhom2.learnenglish.core.data.local.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index

@Entity(
    tableName = "word_set_cross_ref",
    primaryKeys = ["word_id", "set_id"],
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["word_id"],
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
        Index(value = ["word_id"]),
        Index(value = ["set_id"])
    ]
)
data class WordSetCrossRef(
    @ColumnInfo(name = "word_id")
    val wordId: Long,

    @ColumnInfo(name = "set_id")
    val setId: Long
)

