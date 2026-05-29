package com.nhom2.learnenglish

import android.app.Application
import android.content.Context

class LearnEnglishApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}
