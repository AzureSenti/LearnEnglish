package com.nhom2.learnenglish.core.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private companion object {
        const val KEY_REMEMBER_ME = "remember_me"
    }

    fun createLoginSession(token: String, userId: Long, rememberMe: Boolean = false) {
        prefs.edit()
            .putString("auth_token", token)
            .putBoolean("is_logged_in", true)
            .putBoolean(KEY_REMEMBER_ME, rememberMe)
            .putLong("current_user_id", userId)
            .apply()
    }

    fun activateGuestMode() {
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .putBoolean(KEY_REMEMBER_ME, false)
            .putLong("current_user_id", -1L)
            .apply()
    }

    fun isRemembered(): Boolean {
        return prefs.getBoolean(KEY_REMEMBER_ME, false)
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