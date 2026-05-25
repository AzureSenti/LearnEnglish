package com.nhom2.learnenglish.core.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)



    fun createLoginSession(token: String, userId: String) {
        prefs.edit {
            putString("auth_token", token)
                .putBoolean("is_logged_in", true)
                .putString("current_user_id", userId)
        }
    }

    fun activateGuestMode() {
        prefs.edit {
            putBoolean("is_logged_in", false)
                .putLong("current_user_id", -1L)
        }
    }

    fun fetchAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun getCurrentUserId(): Long {
        return prefs.getLong("current_user_id", -1L)
    }

    fun logout() {
        prefs.edit { clear() }
    }

}