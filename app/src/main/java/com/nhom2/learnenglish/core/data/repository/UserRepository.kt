package com.nhom2.learnenglish.core.data.repository

import com.nhom2.learnenglish.core.data.local.dao.UserDao
import com.nhom2.learnenglish.core.data.local.entity.UserEntity
import com.nhom2.learnenglish.core.network.Auth.AuthApi
import com.nhom2.learnenglish.core.network.Auth.LoginRequest
import com.nhom2.learnenglish.core.util.AppExecutors
import com.nhom2.learnenglish.core.util.SessionManager


class UserRepository(
    executors: AppExecutors = AppExecutors.getInstance(),
    private val userDao: UserDao,
    private val authApi: AuthApi,
    private val sessionManager: SessionManager
    ) : BaseRepository(executors) {


    suspend fun login(username: String, password: String) : UserEntity {
        val response = authApi.login(LoginRequest(username, password))

        sessionManager.saveAuthToken(response.token)

        val userEntity = UserEntity(
            userId = response.userId,
            fullName = response.fullName,
            avatarUrl = response.avatarUrl,
            email = response.email,
            coins = response.coins
        )

        userDao.deleteAll()
        userDao.insert(userEntity)

        return userEntity
    }

}