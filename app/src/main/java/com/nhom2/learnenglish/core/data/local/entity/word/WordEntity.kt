package com.nhom2.learnenglish.core.data.local.entity.word

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "words",
    indices = [
        Index(value = ["english_word"], unique = true),
    ]
)
data class WordEntity(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),

    @ColumnInfo(name = "english_word")
    val englishWord: String,

    @ColumnInfo(name = "vietnamese_meaning")
    val vietnameseMeaning: String,

    @ColumnInfo(name = "audio")
    val audio: String? = null,

    @ColumnInfo(name = "is_synced", defaultValue = "0")
    val isSynced: Boolean = false
)
