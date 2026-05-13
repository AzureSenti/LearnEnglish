package com.nhom2.learnenglish.core.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun createLoginSession(token: String, userId: Long) {
        prefs.edit()
            .putString("auth_token", token)
            .putBoolean("is_logged_in", true)
            .putLong("current_user_id", userId)
            .apply()
    }

    fun activateGuestMode() {
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .putLong("current_user_id", -1L)
            .apply()
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
        prefs.edit().clear().apply()
    }
}