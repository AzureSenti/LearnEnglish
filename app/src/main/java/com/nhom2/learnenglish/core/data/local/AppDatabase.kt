package com.nhom2.learnenglish.core.data.local

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.TypeConverters
import com.nhom2.learnenglish.core.data.local.dao.ArticleDao
import com.nhom2.learnenglish.core.data.local.dao.UserDao
import com.nhom2.learnenglish.core.data.local.dao.UserWordSetDao
import com.nhom2.learnenglish.core.data.local.dao.WordDao
import com.nhom2.learnenglish.core.data.local.dao.WordSetCrossDao
import com.nhom2.learnenglish.core.data.local.dao.WordSetDao
import com.nhom2.learnenglish.core.data.local.dao.WordSrsDao
import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity
import com.nhom2.learnenglish.core.data.local.entity.UserEntity
import com.nhom2.learnenglish.core.data.local.entity.WordEntity

@Database(
    entities = [
        UserEntity::class,
        WordEntity::class,
        ArticleEntity::class,
    ],
    version = 1,
    exportSchema = true
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun userDao() : UserDao
    abstract fun wordDao(): WordDao
    abstract fun wordSetDao(): WordSetDao
    abstract fun wordSrsDao(): WordSrsDao
    abstract fun userWordSetDao(): UserWordSetDao
    abstract fun wordSetCrossDao(): WordSetCrossDao




    companion object {
        private const val DATABASE_NAME = "learn_english_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }


        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}