package com.nhom2.learnenglish.core.data.repository

import com.nhom2.learnenglish.core.data.local.dao.UserDao
import com.nhom2.learnenglish.core.data.local.entity.UserEntity
import com.nhom2.learnenglish.core.network.auth.AuthApi
import com.nhom2.learnenglish.core.network.auth.LoginRequest
import com.nhom2.learnenglish.core.util.AppExecutors
import com.nhom2.learnenglish.core.util.SessionManager

class UserRepository(
    executors: AppExecutors = AppExecutors.getInstance(),
    private val userDao: UserDao,
    private val authApi: AuthApi,
    private val sessionManager: SessionManager
) : BaseRepository(executors) {

    companion object {
        const val LOCAL_USER_ID = "-1"
    }

    suspend fun login(username: String, password: String) : UserEntity {
        val response = authApi.login(LoginRequest(username, password))

        // CẬP NHẬT 1: Lấy đúng vị trí token và check null
        val validToken = response.tokens?.accessToken
        if (validToken.isNullOrEmpty()) {
            throw Exception("Đăng nhập thất bại: Không lấy được token từ server!")
        }

        // CẬP NHẬT 2: Đảm bảo userId không bị null
        val validUserId = response.userId ?: "-1"

        // Lưu session an toàn
        sessionManager.createLoginSession(validToken, validUserId)

        val userEntity = UserEntity(
            userId = validUserId, // Lưu String
            fullName = response.fullName ?: "",
            avatarUrl = response.avatarUrl ?: "",
            email = response.email ?: "",
            coins = response.coins ?: 0
        )

        userDao.deleteAll()
        userDao.insert(userEntity)

        return userEntity
    }

    fun ensureLocalUserExists(): UserEntity {
        var localUser = userDao.getById(LOCAL_USER_ID)

        if (localUser == null) {
            localUser = UserEntity(
                id = LOCAL_USER_ID.toLong(),
                userId = LOCAL_USER_ID, // Chuyển -1 thành chuỗi "-1"
                fullName = "Local User",
                coins = 0,
                // Bổ sung các trường bắt buộc khác (nếu có) để tránh lỗi null entity
                avatarUrl = "",
                email = ""
            )
            userDao.insert(localUser)
        }
        return localUser
    }
}