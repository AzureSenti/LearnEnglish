package com.nhom2.learnenglish.core.data.local.entity.word

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "word_sets")
data class WordSetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "unlock_cost", defaultValue = "0")
    val unlockCost: Int

)

