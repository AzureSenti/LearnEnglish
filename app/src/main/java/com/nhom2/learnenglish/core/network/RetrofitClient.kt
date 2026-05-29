package com.nhom2.learnenglish.core.network

import com.nhom2.learnenglish.BuildConfig.BASE_URL
import com.nhom2.learnenglish.core.network.sync.SyncApi

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    @Volatile
    private var retrofit: Retrofit? = null
    fun getInstance(): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: buildRetrofit().also { retrofit = it }
        }
    }

    private fun buildRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Convenience accessor for SyncApi
    fun getSyncApi(): SyncApi {
        return getInstance().create(SyncApi::class.java)
    }
}