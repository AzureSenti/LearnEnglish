package com.nhom2.learnenglish.core.network.auth

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val userId: String,
    val fullName: String,
    val avatarUrl: String,
    val email: String,
    val coins: Int,
    val token: String,
)