package com.nhom2.learnenglish.core.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String, rememberMe: Boolean = true) {
        prefs.edit()
            .putString("auth_token", token)
            .putBoolean("is_logged_in", true)
            .putBoolean("remember_me", rememberMe)
            .apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun isRemembered(): Boolean {
        return prefs.getBoolean("remember_me", false)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false) && !fetchAuthToken().isNullOrEmpty()
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}