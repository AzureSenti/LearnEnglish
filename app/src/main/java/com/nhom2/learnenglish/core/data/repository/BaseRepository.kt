package com.nhom2.learnenglish.core.data.repository

import com.nhom2.learnenglish.core.util.AppExecutors


abstract class BaseRepository(
    protected val executors: AppExecutors = AppExecutors.getInstance()
) {

    protected fun runOnDiskIO(block: Runnable) {
        executors.diskIO.execute(block)
    }


    protected fun runOnMain(block: Runnable) {
        executors.mainThread.execute(block)
    }


    protected fun <T> runAndCallback(work: () -> T, callback: (T) -> Unit) {
        executors.diskIO.execute {
            val result = work()
            executors.mainThread.execute { callback(result) }
        }
    }
}