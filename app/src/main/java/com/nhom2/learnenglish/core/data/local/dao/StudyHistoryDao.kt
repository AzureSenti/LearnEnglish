package com.nhom2.learnenglish.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.entity.StudyHistoryEntity

@Dao
interface StudyHistoryDao : BaseDao<StudyHistoryEntity> {

    @Query("SELECT * FROM study_history WHERE user_id = :userId ORDER BY timestamp DESC")
    suspend fun getAllHistory(userId: Long): List<StudyHistoryEntity>

    @Query("SELECT * FROM study_history WHERE user_id = :userId AND timestamp BETWEEN :from AND :to ORDER BY timestamp DESC")
    suspend fun getHistoryForPeriod(userId: Long, from: Long, to: Long): List<StudyHistoryEntity>

    @Query("SELECT IFNULL(SUM(xp_earned), 0) FROM study_history WHERE user_id = :userId AND timestamp BETWEEN :from AND :to")
    suspend fun getXpEarnedForPeriod(userId: Long, from: Long, to: Long): Int

    @Query("SELECT COUNT(DISTINCT date(timestamp / 1000, 'unixepoch')) FROM study_history WHERE user_id = :userId AND timestamp BETWEEN :from AND :to")
    suspend fun getActiveDaysForPeriod(userId: Long, from: Long, to: Long): Int

    @Query("SELECT IFNULL(MAX(streak_day), 0) FROM study_history WHERE user_id = :userId")
    suspend fun getLongestRecordedStreak(userId: Long): Int
}
