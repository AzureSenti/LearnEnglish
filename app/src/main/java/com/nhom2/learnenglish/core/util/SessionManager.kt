package com.nhom2.learnenglish.core.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    // Thêm hàm này để sau này người làm form Login sẽ gọi nó
    fun saveUserId(userId: Long) {
        prefs.edit().putLong("user_id", userId).apply()
    }

    // Sửa hàm này thành như sau:
    fun getUserId(): Long {
        // Tạm thời trả về 1L (ID của Guest) nếu user chưa đăng nhập.
        // Bằng cách này, Game của bạn vẫn lưu được dữ liệu xuống DB local ở chế độ "Log without login".
        return prefs.getLong("user_id", 1L)
    }

    fun saveAuthToken(token: String) {
        prefs.edit()
            .putString("auth_token", token)
            .putBoolean("is_logged_in", true)
            .apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}