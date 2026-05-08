package com.nhom2.learnenglish.core.data.local.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey


@Entity(
    tableName = "users",
    indices = [Index(value = ["user_id"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "coins")
    val coins: Int,

    @ColumnInfo(name = "is_synced", defaultValue = "0")
    val isSynced: Boolean = false

)