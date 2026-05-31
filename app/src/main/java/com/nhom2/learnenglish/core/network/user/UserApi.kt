package com.nhom2.learnenglish.core.network.user

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT

interface UserApi {
    @PUT("user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<UpdateProfileResponse>

    @PUT("user/password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<ChangePasswordResponse>
}
