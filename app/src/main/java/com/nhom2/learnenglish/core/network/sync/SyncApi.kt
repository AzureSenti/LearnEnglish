package com.nhom2.learnenglish.core.network.sync

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface SyncApi {

    @POST("sync/upload")
    suspend fun uploadProgress(
        @Header("Authorization") token: String,
        @Body request: SyncUploadRequest
    ): Response<Unit>

    @GET("sync/download")
    suspend fun downloadProgress(
        @Header("Authorization") token: String
    ): Response<SyncDownloadResponse>
}
