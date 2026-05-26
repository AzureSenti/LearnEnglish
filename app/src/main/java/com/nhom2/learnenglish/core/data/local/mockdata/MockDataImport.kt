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
        // DÙNG SHAREDPREFERENCES ĐỂ KIỂM TRA ĐIỀU KIỆN
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Nếu ứng dụng đã được nạp dữ liệu mẫu rồi thì bỏ qua luôn, không xoá DB nữa
        if (sharedPref.getBoolean(KEY_DB_SEEDED, false)) {
            if (onComplete != null) {
                AppExecutors.getInstance().mainThread.execute {
                    onComplete.run()
                }
            }
            return
        }

        val db = AppDatabase.getInstance(context)
        AppExecutors.getInstance().diskIO.execute {
            try {
                kotlinx.coroutines.runBlocking {
                    // Chỉ thực hiện xoá và chèn lại duy nhất 1 lần đầu tiên khi cài app
                    // Article
                    db.articleDao().deleteAll()
                    db.articleDao().insertAll(MockData.articles)

                    // Word and Set
                    db.wordDao().deleteAll()
                    db.wordSetDao().deleteAll()
                    db.wordSetCrossDao().deleteAll()
                    db.wordSetDao().insertAll(MockData.wordSets)
                    db.wordDao().insertAll(MockData.words)
                    db.wordSetCrossDao().insertAll(MockData.wordSetRefs)

                    // Grammar
                    db.grammarLessonDao().deleteAll()
                    db.grammarQuestionDao().deleteAll()

                    db.grammarLessonDao().insertAll(MockData.grammarLessons)
                    db.grammarQuestionDao().insertAll(MockData.grammarQuestion)
                }

                // ĐÁNH DẤU: Đã nạp thành công dữ liệu mẫu vào máy, lần sau không nạp lại nữa
                sharedPref.edit().putBoolean(KEY_DB_SEEDED, true).apply()

                if (onComplete != null) {
                    AppExecutors.getInstance().mainThread.execute {
                        onComplete.run()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}