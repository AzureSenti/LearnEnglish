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
        val authInterceptor = okhttp3.Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            
            if (response.code() == 401) {
                // If 401 Unauthorized, automatically log out
                try {
                    val context = com.nhom2.learnenglish.LearnEnglishApp.appContext
                    val sessionManager = com.nhom2.learnenglish.core.util.SessionManager(context)
                    
                    if (sessionManager.isLoggedIn()) {
                        sessionManager.logout()
                        
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            android.widget.Toast.makeText(
                                context, 
                                "Tài khoản của bạn đã bị đăng xuất hoặc xóa!", 
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }

                        val intent = android.content.Intent(
                            context, 
                            com.nhom2.learnenglish.feature.auth.LoginActivity::class.java
                        ).apply {
                            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            response
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
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