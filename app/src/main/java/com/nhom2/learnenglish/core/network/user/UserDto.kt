package com.nhom2.learnenglish.core.network.user

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("avatar_url") val avatarUrl: String?
)

data class UpdateProfileResponse(
    @SerializedName("user_id") val userId: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("coins") val coins: Int,
    @SerializedName("current_streak") val currentStreak: Int,
    @SerializedName("longest_streak") val longestStreak: Int
)

data class ChangePasswordRequest(
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("new_password") val newPassword: String
)

data class ChangePasswordResponse(
    @SerializedName("message") val message: String
)
