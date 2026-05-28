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


    // 1DÙNG CHO TRANG CHỦ (MAIN): Ôn tập tất cả các từ đến hạn của user, bất kể nằm ở bộ nào
    @Query("""
        SELECT w.* FROM words w
        INNER JOIN word_srs srs ON w.id = srs.word_id
        WHERE srs.user_id = :userId AND srs.next_review_date <= :currentTime
        ORDER BY srs.next_review_date ASC
        LIMIT :limit
    """)
    fun getGlobalWordsDueForReview(userId: String, currentTime: Long, limit: Int = 20): List<WordEntity>
    //  DÙNG CHO TRANG CHI TIẾT (WORD SET DETAIL): Chỉ lấy các từ đến hạn nằm trong 1 bộ cụ thể
    @Query("""
        SELECT w.* FROM words w
        INNER JOIN word_srs srs ON w.id = srs.word_id
        INNER JOIN word_set_cross_ref ref ON w.id = ref.word_id 
        WHERE srs.user_id = :userId 
          AND ref.set_id = :setId 
          AND srs.next_review_date <= :currentTime
        ORDER BY srs.next_review_date ASC
        LIMIT :limit
    """)
    fun getWordsDueForReviewBySet(userId: String, setId: Long, currentTime: Long, limit: Int = 20): List<WordEntity>

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
    fun getWordsWithProgressBySet(setId: Long, userId: String): List<WordWithProgress>

    // Từ chưa học
    @Query("""
        SELECT w.* FROM words w
        INNER JOIN word_set_cross_ref ref ON w.id = ref.word_id
        LEFT JOIN word_srs srs ON w.id = srs.word_id AND srs.user_id = :userId
        WHERE ref.set_id = :setId AND srs.word_id IS NULL
        LIMIT :limit
    """)
    fun getUnlearnedWords(setId: Long, userId: String, limit: Int = 10): List<WordEntity>
    // Lấy các từ mới (chưa học) trên toàn hệ thống (không phân biệt bộ)
    @Query("""
        SELECT w.* FROM words w
        WHERE w.id NOT IN (SELECT word_id FROM word_srs WHERE user_id = :userId)
        LIMIT :limit
    """)
    fun getGlobalNewWords(userId: String, limit: Int = 20): List<WordEntity>


    @Query("DELETE FROM words")
    fun deleteAll()
    // Kiểm tra xem từ vựng đã tồn tại trong từ điển chung chưa
    @Query("SELECT * FROM words WHERE english_word = :word LIMIT 1")
    fun getWordByEnglish(word: String): WordEntity?
}
