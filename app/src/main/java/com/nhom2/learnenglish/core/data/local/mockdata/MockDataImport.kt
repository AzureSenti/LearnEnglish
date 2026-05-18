package com.nhom2.learnenglish.core.data.local.mockdata

import android.content.Context
import com.nhom2.learnenglish.core.data.local.AppDatabase
import com.nhom2.learnenglish.core.util.AppExecutors

object MockDataImport {

    private const val PREFS_NAME = "learn_english_prefs"
    private const val KEY_DB_SEEDED = "db_seeded_v2"

    fun importIfNeeded(context: Context) {
        importIfNeeded(context, null)
    }

    fun importIfNeeded(context: Context, onComplete: Runnable?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_DB_SEEDED, false)) {
            if (onComplete != null) {
                AppExecutors.getInstance().mainThread.execute { onComplete.run() }
            }
            return
        }

        val db = AppDatabase.getInstance(context)
        AppExecutors.getInstance().diskIO.execute {
            try {
                kotlinx.coroutines.runBlocking {
                    db.articleDao().deleteAll()
                    db.articleDao().insertAll(MockData.articles)

                    db.wordDao().deleteAll()
                    db.wordSetDao().deleteAll()
                    db.wordSetDao().insertAll(MockData.wordSets)
                    db.wordDao().insertAll(MockData.words)
                    db.wordSetCrossDao().insertAll(MockData.wordSetRefs)
                }

                prefs.edit().putBoolean(KEY_DB_SEEDED, true).apply()

                if (onComplete != null) {
                    AppExecutors.getInstance().mainThread.execute { onComplete.run() }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
