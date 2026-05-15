package com.nhom2.learnenglish.core.util

import android.app.Activity
import android.content.Intent

object Navigator {
    fun navigateTo(currentActivity: Activity, targetActivity: Class<*>) {
        val intent = Intent(currentActivity, targetActivity)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        currentActivity.startActivity(intent)
        currentActivity.overridePendingTransition(0, 0)
    }
}