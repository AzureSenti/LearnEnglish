package com.nhom2.learnenglish.core.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun createLoginSession(token: String, userId: String, refreshToken: String = "") {
        prefs.edit {
            putString("auth_token", token)
            putString("refresh_token", refreshToken)
            putBoolean("is_logged_in", true)
            putString("current_user_id", userId) // Lưu dạng String
        }
    }

    fun activateGuestMode() {
        prefs.edit {
            putBoolean("is_logged_in", false)
            putString("current_user_id", "-1") // Lưu ID khách dạng String
        }
    }

    fun fetchAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun fetchRefreshToken(): String? {
        return prefs.getString("refresh_token", null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun getCurrentUserId(): String {
        return prefs.getString("current_user_id", "-1") ?: "-1"
    }

    fun updateAuthToken(newToken: String) {
        prefs.edit {
            putString("auth_token", newToken)
        }
    }

    fun logout() {
        prefs.edit { clear() }
    }
}