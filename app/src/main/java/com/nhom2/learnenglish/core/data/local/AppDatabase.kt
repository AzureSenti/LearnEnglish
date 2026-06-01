package com.nhom2.learnenglish.core.data.local

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.TypeConverters
import com.nhom2.learnenglish.core.data.local.dao.ArticleDao
import com.nhom2.learnenglish.core.data.local.dao.UserDao
import com.nhom2.learnenglish.core.data.local.dao.grammar.GrammarLessonDao
import com.nhom2.learnenglish.core.data.local.dao.grammar.GrammarQuestionDao
import com.nhom2.learnenglish.core.data.local.dao.grammar.UserGrammarProgressDao
import com.nhom2.learnenglish.core.data.local.dao.word.UserWordSetDao
import com.nhom2.learnenglish.core.data.local.dao.word.WordDao
import com.nhom2.learnenglish.core.data.local.dao.word.WordSetCrossDao
import com.nhom2.learnenglish.core.data.local.dao.word.WordSetDao
import com.nhom2.learnenglish.core.data.local.dao.word.WordSrsDao
import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity
import com.nhom2.learnenglish.core.data.local.entity.StoryEntity
import com.nhom2.learnenglish.core.data.local.dao.StoryDao
import com.nhom2.learnenglish.core.data.local.entity.UserEntity
import com.nhom2.learnenglish.core.data.local.entity.grammar.GrammarLessonEntity
import com.nhom2.learnenglish.core.data.local.entity.grammar.GrammarQuestionEntity
import com.nhom2.learnenglish.core.data.local.entity.grammar.UserGrammarProgress
import com.nhom2.learnenglish.core.data.local.entity.word.UserWordSetCrossRef
import com.nhom2.learnenglish.core.data.local.entity.word.WordEntity
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetCrossRef
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity
import com.nhom2.learnenglish.core.data.local.entity.word.WordSrsEntity

@Database(
    entities = [
        UserEntity::class,
        UserWordSetCrossRef::class,
        WordEntity::class,
        WordSrsEntity::class,
        WordSetEntity::class,
        WordSetCrossRef::class,
        ArticleEntity::class,
        StoryEntity::class,
        GrammarLessonEntity::class,
        GrammarQuestionEntity::class,
        UserGrammarProgress::class,
        com.nhom2.learnenglish.core.data.local.entity.sync.DeletedSyncItemEntity::class
    ],
    version = 1,
    exportSchema = true
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun storyDao(): StoryDao
    abstract fun userDao() : UserDao
    abstract fun wordDao(): WordDao
    abstract fun wordSetDao(): WordSetDao
    abstract fun wordSrsDao(): WordSrsDao
    abstract fun userWordSetDao(): UserWordSetDao
    abstract fun wordSetCrossDao(): WordSetCrossDao

    abstract fun grammarLessonDao(): GrammarLessonDao
    abstract fun grammarQuestionDao(): GrammarQuestionDao
    abstract fun userGrammarProgressDao(): UserGrammarProgressDao
    abstract fun deletedSyncItemDao(): com.nhom2.learnenglish.core.data.local.dao.sync.DeletedSyncItemDao

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
