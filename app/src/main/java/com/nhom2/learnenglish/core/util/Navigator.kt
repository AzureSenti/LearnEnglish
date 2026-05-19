package com.nhom2.learnenglish.core.util

import android.app.Activity
import android.content.Intent
import android.os.Bundle

object Navigator {

    /**
     * Dùng cho các trang chuyển đi bình thường, KHÔNG cần mang theo dữ liệu.
     */
    @JvmStatic
    fun navigateTo(currentActivity: Activity, targetActivity: Class<*>) {
        navigateTo(currentActivity, targetActivity, null)
    }

    /**
     * Dùng cho các trang CẦN mang theo dữ liệu (như truyền ID bài báo, ID ngữ pháp).
     */
    @JvmStatic
    fun navigateTo(currentActivity: Activity, targetActivity: Class<*>, extras: Bundle?) {
        val intent = Intent(currentActivity, targetActivity)

        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

        if (extras != null) {
            intent.putExtras(extras)
        }

        currentActivity.startActivity(intent)
        currentActivity.overridePendingTransition(0, 0)
    }
}