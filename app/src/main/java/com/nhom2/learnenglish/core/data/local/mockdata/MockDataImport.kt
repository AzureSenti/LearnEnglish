package com.nhom2.learnenglish.core.data.local.mockdata

import android.content.Context
import com.nhom2.learnenglish.core.data.local.AppDatabase
import com.nhom2.learnenglish.core.util.AppExecutors

object MockDataImport {

    private const val PREFS_NAME = "learn_english_prefs"
    private const val KEY_DB_SEEDED = "db_seeded"

    fun importIfNeeded(context: Context) {
        importIfNeeded(context, null)
    }

    fun importIfNeeded(context: Context, onComplete: Runnable?) {


        val db = AppDatabase.getInstance(context)
        AppExecutors.getInstance().diskIO.execute {
            try {
                kotlinx.coroutines.runBlocking {

                    db.articleDao().deleteAll()


                    db.articleDao().insertAll(MockData.articles)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}