package com.nhom2.learnenglish.core.util

import android.content.Intent

import android.app.Activity

object Navigator{
    fun navigateTo(currentActivity: Activity, targetActivity: Class<*>) {
        val intent = Intent(currentActivity, targetActivity)
        currentActivity.startActivity(intent)

    }
}