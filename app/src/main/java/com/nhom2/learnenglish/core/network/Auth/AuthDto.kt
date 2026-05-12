package com.nhom2.learnenglish.core.network.Auth

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val userId: Long,
    val fullName: String,
    val avatarUrl: String,
    val email: String,
    val coins: Int,
    val token: String,
)