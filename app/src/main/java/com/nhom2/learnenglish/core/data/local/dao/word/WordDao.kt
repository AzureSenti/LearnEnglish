package com.nhom2.learnenglish.core.data.local.dao.word

import androidx.room3.Dao
import androidx.room3.Query
import com.nhom2.learnenglish.core.data.local.dao.BaseDao
import com.nhom2.learnenglish.core.data.local.entity.word.WordEntity
import com.nhom2.learnenglish.core.data.model.WordWithProgress

@Dao
interface WordDao : BaseDao<WordEntity> {

    @Query("SELECT * FROM words")
    fun getAll(): List<WordEntity>

    @Query("SELECT * FROM words WHERE id = :id")
    fun getById(id: Long): WordEntity?

    // Lấy tất cả các từ thuộc về một Bộ từ (Set) cụ thể
    @Query("""
        SELECT w.* FROM words w 
        INNER JOIN word_set_cross_ref ref ON w.id = ref.word_id 
        WHERE ref.set_id = :setId
    """)
    fun getWordsBySetId(setId: Long): List<WordEntity>

    // Lấy danh sách các từ ĐẾN HẠN ÔN TẬP của một User cụ thể
    @Query("""
        SELECT w.* FROM words w
        INNER JOIN word_srs srs ON w.id = srs.word_id
        WHERE srs.user_id = :userId AND srs.next_review_date <= :currentTime
        ORDER BY srs.next_review_date ASC
        LIMIT :limit
    """)
    fun getWordsDueForReview(userId: Long, currentTime: Long, limit: Int = 20): List<WordEntity>

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
    fun getWordsWithProgressBySet(setId: Long, userId: Long): List<WordWithProgress>

    // Từ chưa học
    @Query("""
        SELECT w.* FROM words w
        INNER JOIN word_set_cross_ref ref ON w.id = ref.word_id
        LEFT JOIN word_srs srs ON w.id = srs.word_id AND srs.user_id = :userId
        WHERE ref.set_id = :setId AND srs.word_id IS NULL
        LIMIT :limit
    """)
    fun getUnlearnedWords(setId: Long, userId: Long, limit: Int = 10): List<WordEntity>


    @Query("DELETE FROM words")
    fun deleteAll()
    // Kiểm tra xem từ vựng đã tồn tại trong từ điển chung chưa
    @Query("SELECT * FROM words WHERE english_word = :word LIMIT 1")
    fun getWordByEnglish(word: String): WordEntity?
}
