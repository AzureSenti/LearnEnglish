package com.nhom2.learnenglish.core.network.auth

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("user_id") val userId: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("coins") val coins: Int,
    @SerializedName("current_streak") val currentStreak: Int?,
    @SerializedName("longest_streak") val longestStreak: Int?,
    @SerializedName("tokens") val tokens: Tokens?
) {
    // Class con để hứng object "tokens"
    data class Tokens(
        @SerializedName("access_token") val accessToken: String?,
        @SerializedName("refresh_token") val refreshToken: String?,
        @SerializedName("token_type") val tokenType: String?
    )
}