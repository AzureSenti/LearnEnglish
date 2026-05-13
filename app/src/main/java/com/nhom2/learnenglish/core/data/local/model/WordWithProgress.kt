package com.nhom2.learnenglish.core.data.local.model

import androidx.room3.ColumnInfo

data class WordWithProgress(
    @ColumnInfo(name = "word_id")
    val wordId: Long,

    @ColumnInfo(name = "english_word")
    val englishWord: String,

    @ColumnInfo(name = "vietnamese_meaning")
    val vietnameseMeaning: String,

    @ColumnInfo(name = "audio")
    val audio: String?,

    @ColumnInfo(name = "level")
    val level: Int?
)