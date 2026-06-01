package com.nhom2.learnenglish.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.entity.UserEntity

@Dao
interface UserDao : BaseDao<UserEntity> {
    @Query("SELECT * FROM users LIMIT 1")
    fun getActiveUser(): UserEntity?

    @Query("SELECT * FROM users WHERE user_id = :userId")
    fun getByUserId(userId: String): UserEntity?

    @Query("DELETE FROM users")
    fun deleteAll()
}