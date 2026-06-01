package com.nhom2.learnenglish.core.data.repository

import android.util.Log
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
    private val sessionManager: SessionManager,
    private val syncRepository: SyncRepository? = null
) : BaseRepository(executors) {

    companion object {
        const val LOCAL_USER_ID = "-1"
        private const val TAG = "UserRepository"
    }

    suspend fun login(identifier: String, password: String) : UserEntity {
        val response = authApi.login(LoginRequest(identifier, password))

        // CẬP NHẬT 1: Lấy đúng vị trí token và check null
        val validToken = response.tokens?.accessToken
        if (validToken.isNullOrEmpty()) {
            throw Exception("Đăng nhập thất bại: Không lấy được token từ server!")
        }

        // CẬP NHẬT 2: Đảm bảo userId không bị null
        val validUserId = response.userId ?: "-1"

        // Lưu session an toàn (bao gồm cả refresh token)
        val refreshToken = response.tokens?.refreshToken ?: ""
        sessionManager.createLoginSession(validToken, validUserId, refreshToken)

        val userEntity = UserEntity(
            userId = validUserId, // Lưu String
            fullName = response.fullName ?: "",
            avatarUrl = response.avatarUrl ?: "",
            email = response.email ?: "",
            coins = response.coins ?: 0,
            currentStreak = response.currentStreak ?: 0,
            longestStreak = response.longestStreak ?: 0
        )

        userDao.deleteAll()
        userDao.insert(userEntity)

        // --- SYNC: Merge guest data và đồng bộ với server ---
        try {
            syncRepository?.let { sync ->
                // Bước 1: Chuyển dữ liệu guest sang user thật
                sync.mergeGuestData(validUserId)
                Log.i(TAG, "Guest data merged for user: $validUserId")

                // Bước 2: Đồng bộ 2 chiều với server
                val syncSuccess = sync.performFullSync()
                Log.i(TAG, "Post-login sync result: $syncSuccess")
            }
        } catch (e: Exception) {
            // Sync failure should NOT prevent login
            Log.e(TAG, "Post-login sync failed (non-fatal)", e)
        }

        return userEntity
    }

    suspend fun register(email: String, password: String, fullName: String, accountName: String) : UserEntity {
        val request = com.nhom2.learnenglish.core.network.auth.RegisterRequest(email, password, fullName, accountName)
        val response = authApi.register(request)

        val validToken = response.tokens?.accessToken
        if (validToken.isNullOrEmpty()) {
            throw Exception("Đăng ký thành công, nhưng vui lòng đăng nhập lại!")
        }

        val validUserId = response.userId

        val refreshToken = response.tokens.refreshToken ?: ""
        sessionManager.createLoginSession(validToken, validUserId, refreshToken)

        val userEntity = UserEntity(
            userId = validUserId,
            fullName = response.fullName,
            avatarUrl = "",
            email = response.email,
            coins = 0,
            currentStreak = 0,
            longestStreak = 0
        )

        userDao.deleteAll()
        userDao.insert(userEntity)

        try {
            syncRepository?.let { sync ->
                sync.mergeGuestData(validUserId)
                Log.i(TAG, "Guest data merged for user: $validUserId")
                sync.performFullSync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Post-register sync failed (non-fatal)", e)
        }

        return userEntity
    }

    fun ensureLocalUserExists(): UserEntity {
        var localUser = userDao.getByUserId(LOCAL_USER_ID)

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