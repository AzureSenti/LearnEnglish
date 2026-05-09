package com.nhom2.learnenglish.core.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class AppExecutors(
    val diskIO: Executor = Executors.newSingleThreadExecutor(),
    val networkIO: Executor = Executors.newFixedThreadPool(3),
    val mainThread: Executor = MainThreadExecutor()
) {
    companion object {
        @Volatile
        private var INSTANCE: AppExecutors? = null

        fun getInstance(): AppExecutors {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppExecutors().also { INSTANCE = it }
            }
        }
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}
