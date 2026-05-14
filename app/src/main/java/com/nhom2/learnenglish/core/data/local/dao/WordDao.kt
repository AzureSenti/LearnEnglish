package com.nhom2.learnenglish.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.entity.WordEntity
import com.nhom2.learnenglish.core.data.local.model.WordWithProgress

@Dao
interface WordDao : BaseDao<WordEntity> {

    // Lấy tất cả các từ thuộc về một Bộ từ (Set) cụ thể
    @Query("""
        SELECT w.* FROM words w 
        INNER JOIN word_set_cross_ref ref ON w.id = ref.word_id 
        WHERE ref.set_id = :setId
    """)
    suspend fun getWordsBySetId(setId: Long): List<WordEntity>

    // Lấy danh sách các từ ĐẾN HẠN ÔN TẬP của một User cụ thể
    @Query("""
        SELECT w.* FROM words w
        INNER JOIN word_srs srs ON w.id = srs.word_id
        WHERE srs.user_id = :userId AND srs.next_review_date <= :currentTime
        ORDER BY srs.next_review_date ASC
        LIMIT :limit
    """)
    suspend fun getWordsDueForReview(userId: Long, currentTime: Long, limit: Int = 20): List<WordEntity>

    // Từ với tiến trình
    @Query("""
        SELECT 
            w.id AS word_id, 
            w.english_word, 
            w.vietnamese_meaning, 
            w.audio, 
            srs.level 
        FROM words w
        INNER JOIN word_set_cross_ref ref ON w.id = ref.word_id
        LEFT JOIN word_srs srs ON w.id = srs.word_id AND srs.user_id = :userId
        WHERE ref.set_id = :setId
    """)
    suspend fun getWordsWithProgressBySet(setId: Long, userId: Long): List<WordWithProgress>

    // Từ chưa học
    @Query("""
        SELECT w.* FROM words w
        INNER JOIN word_set_cross_ref ref ON w.id = ref.word_id
        LEFT JOIN word_srs srs ON w.id = srs.word_id AND srs.user_id = :userId
        WHERE ref.set_id = :setId AND srs.word_id IS NULL
        LIMIT :limit
    """)
    suspend fun getUnlearnedWords(setId: Long, userId: Long, limit: Int = 10): List<WordEntity>


    @Query("DELETE FROM words")
    suspend fun deleteAll()
}