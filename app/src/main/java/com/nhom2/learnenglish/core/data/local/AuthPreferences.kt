package com.nhom2.learnenglish.core.data.local

import android.content.Context

object AuthPreferences {
    private const val PREF_NAME = "learn_english_auth"
    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_REMEMBER = "remember"

    fun isSessionRestored(context: Context): Boolean {
        val p = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return p.getBoolean(KEY_LOGGED_IN, false) && p.getBoolean(KEY_REMEMBER, false)
    }

    fun saveSession(context: Context, remember: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        if (remember) {
            prefs.putBoolean(KEY_REMEMBER, true)
            prefs.putBoolean(KEY_LOGGED_IN, true)
        } else {
            prefs.putBoolean(KEY_REMEMBER, false)
            prefs.putBoolean(KEY_LOGGED_IN, false)
        }
        prefs.apply()
    }
}
