package com.nhom2.learnenglish.core.data.local.mockdata

import android.content.Context
import com.nhom2.learnenglish.core.data.local.AppDatabase
import com.nhom2.learnenglish.core.util.AppExecutors

object MockDataImport {

    private const val PREFS_NAME = "learn_english_prefs"
    private const val KEY_DB_SEEDED = "db_seeded"

    fun importIfNeeded(context: Context) {
        val prefs = context.getSharedPreferences(com.nhom2.learnenglish.core.data.local.mockdata.MockDataImport.PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(com.nhom2.learnenglish.core.data.local.mockdata.MockDataImport.KEY_DB_SEEDED, false)) return

        val db = AppDatabase.getInstance(context)
        AppExecutors.getInstance().diskIO.execute {
            try {
                kotlinx.coroutines.runBlocking {


                    db.articleDao().insertAll(MockData.articles)

                }

                prefs.edit().putBoolean(com.nhom2.learnenglish.core.data.local.mockdata.MockDataImport.KEY_DB_SEEDED, true).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}