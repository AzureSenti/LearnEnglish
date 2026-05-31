package com.nhom2.learnenglish.core.data.repository

import com.nhom2.learnenglish.core.data.local.dao.word.UserWordSetDao
import com.nhom2.learnenglish.core.data.local.dao.word.WordDao
import com.nhom2.learnenglish.core.data.local.dao.word.WordSetCrossDao
import com.nhom2.learnenglish.core.data.local.dao.word.WordSetDao
import com.nhom2.learnenglish.core.data.local.dao.word.WordSrsDao
import com.nhom2.learnenglish.core.data.local.entity.word.UserWordSetCrossRef
import com.nhom2.learnenglish.core.data.local.entity.word.WordEntity
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetCrossRef
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity
import com.nhom2.learnenglish.core.data.local.entity.word.WordSrsEntity
import com.nhom2.learnenglish.core.data.model.WordWithProgress
import com.nhom2.learnenglish.core.util.AppExecutors


class WordRepository(
    private val wordDao: WordDao,
    private val wordSetDao: WordSetDao,
    private val wordSrsDao: WordSrsDao,
    private val userWordSetDao: UserWordSetDao,
    private val wordSetCrossDao: WordSetCrossDao,
    executors: AppExecutors = AppExecutors.getInstance()
) : BaseRepository(executors) {

    fun getAllSets(): List<WordSetEntity> {
        return wordSetDao.getAllSets()
    }

    fun getSetById(setId: Long): WordSetEntity? {
        return wordSetDao.getSetById(setId)
    }

    fun createSet(set: WordSetEntity) {
        wordSetDao.insert(set)
    }

    fun updateSet(set: WordSetEntity) {
        wordSetDao.update(set.copy(isSynced = false))
    }
    fun addWordToSet(wordId: Long, setId: Long) {
        wordSetCrossDao.insert(WordSetCrossRef(wordId = wordId, setId = setId, isSynced = false))
    }

    fun  removeWordFromSet(wordId: Long, setId: Long) {
        wordSetCrossDao.removeWordFromSet(wordId, setId)
    }

    fun getWordsInSet(setId: Long): List<WordEntity> {
        return wordDao.getWordsBySetId(setId)
    }
    // 1. Dùng cho MainMenuActivity (Nút ôn tập toàn bộ sau này)
    fun getGlobalWordsForReview(userId: String): List<WordEntity> {
        val currentTimeMillis = System.currentTimeMillis()
        return wordDao.getGlobalWordsDueForReview(userId, currentTimeMillis)
    }

    // 2. Dùng cho WordSetDetailActivity (Nút ôn tập trong từng bộ)
    fun getWordsForReview(userId: String, setId: Long): List<WordEntity> {
        val currentTimeMillis = System.currentTimeMillis()
        return wordDao.getWordsDueForReviewBySet(userId, setId, currentTimeMillis)
    }



    fun getUnlockedWordSets(userId: Long): List<WordSetEntity> {
        return if (userId == -1L) {
            wordSetDao.getAllSets()
        } else {
            wordSetDao.getUnlockedWordSets(userId)
        }
    }


    fun getWordListWithProgress(setId: Long, userId: String): List<WordWithProgress> {
        val rawList = wordDao.getWordsWithProgressBySet(setId, userId)

        return rawList.map { item ->
            if (item.level == null) {
                item.copy(level = 0)
            } else {
                item
            }
        }
    }
    // Dùng cho nút HỌC MỚI ở trang chủ (Lấy từ chưa học từ tất cả các bộ)
    fun getGlobalNewWordsToLearn(userId: String): List<WordEntity> {
        return wordDao.getGlobalNewWords(userId)
    }

    fun unlockWordSet(userId: Long, setId: Long): Boolean {
        try {
            val crossRef = UserWordSetCrossRef(userId = userId, setId = setId)
            userWordSetDao.unlockSet(crossRef)
            return true
        } catch (e: Exception) {
            e.printStackTrace();
            return false
        }
    }

    fun getNewWordsToLearn(userId: String, setId: Long): List<WordEntity> {
        return wordDao.getUnlearnedWords(setId, userId, limit = 10)
    }

    fun processWordLearning(userId: String, wordId: Long, isCorrect: Boolean) {
        val existingSrs = wordSrsDao.getWordSrs(userId, wordId)
        val currentTime = System.currentTimeMillis()

        if (existingSrs == null) {

            val initialLevel = if (isCorrect) 1 else 0
            val interval = calculateInterval(initialLevel)

            val newSrs = WordSrsEntity(
                userId = userId,
                wordId = wordId,
                level = initialLevel,
                lastReviewDate = currentTime,
                nextReviewDate = currentTime + interval,
                isSynced = false
            )
            wordSrsDao.insertOrUpdate(newSrs)

        } else {
            val newLevel = if (isCorrect) existingSrs.level + 1 else maxOf(0, existingSrs.level - 1)
            val interval = calculateInterval(newLevel)

            val updatedSrs = existingSrs.copy(
                level = newLevel,
                lastReviewDate = currentTime,
                nextReviewDate = currentTime + interval,
                isSynced = false
            )
            wordSrsDao.insertOrUpdate(updatedSrs)
        }
    }

    private fun calculateInterval(level: Int): Long {
        // THỜI GIAN TEST (Tính bằng phút)
        val minutes: Long = when (level) {
            0 -> 1  // Sai hoặc chưa thuộc: Chờ 1 phút để ôn lại
            1 -> 1  // Thuộc level 1: Chờ 1 phút (thay vì 12 tiếng)
            2 -> 2  // Thuộc level 2: Chờ 2 phút (thay vì 1 ngày)
            3 -> 3  // Thuộc level 3: Chờ 3 phút (thay vì 3 ngày)
            4 -> 4  // Thuộc level 4: Chờ 4 phút (thay vì 7 ngày)
            5 -> 5  // Thuộc level 5: Chờ 5 phút (thay vì 14 ngày)
            else -> 6 // Từ level 6 trở lên: Chờ 6 phút (thay vì 30 ngày)
        }
        return minutes * 60 * 1000L // Nhân với 60 * 1000 để đổi từ phút sang milliseconds
    }

    fun getAllWords(): List<WordEntity> {
        return wordDao.getAll()
    }

    fun getWordById(wordId: Long): WordEntity? {
        return wordDao.getById(wordId)
    }
    // QUẢN LÝ TỪ VỰNG TRONG MỘT BỘ TỪ (CRUD TÙY CHỈNH)


    /**
     * THÊM: Thêm từ mới vào một bộ từ (Có kiểm tra trùng lặp)
     */
    fun addNewWordToSet(
        word: WordEntity,
        setId: Long,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        runOnDiskIO {
            try {
                // 1. Kiểm tra từ đã có trong kho từ vựng chung chưa
                val existingWord = wordDao.getWordByEnglish(word.englishWord)

                // 2. Lấy ra ID để liên kết
                val wordIdToLink = if (existingWord != null) {
                    existingWord.id // Dùng lại ID cũ
                } else {
                    wordDao.insert(word) // Thêm mới và lấy ID mới
                }

                // 3. Kiểm tra xem từ đã nằm trong Bộ từ hiện tại chưa
                val isAlreadyInSet = wordSetCrossDao.isWordInSet(wordIdToLink, setId)

                if (isAlreadyInSet) {
                    onError?.let { runOnMain { it("Từ này đã có trong bộ từ hiện tại!") } }
                } else {
                    // 4. Tạo liên kết
                    val crossRef = WordSetCrossRef(wordId = wordIdToLink, setId = setId, isSynced = false)
                    wordSetCrossDao.insert(crossRef)
                    onSuccess?.let { runOnMain { it() } }
                }

            } catch (e: Exception) {
                onError?.let { runOnMain { it("Lỗi hệ thống: ${e.message}") } }
            }
        }
    }

    fun getRandomWords(limit: Int = 70): List<WordEntity> {
        return wordDao.getRandomWords(limit)
    }



    /**
     * XÓA: Gỡ liên kết của từ khỏi bộ từ hiện tại
     */
    fun removeWordFromSpecificSet(wordId: Long, setId: Long, onComplete: (() -> Unit)? = null) {
        runOnDiskIO {
            wordSetCrossDao.removeWordFromSet(wordId = wordId, setId = setId)
            onComplete?.let { runOnMain { it() } }
        }
    }



}