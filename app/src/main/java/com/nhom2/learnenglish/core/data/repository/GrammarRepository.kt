package com.nhom2.learnenglish.core.data.repository

import com.nhom2.learnenglish.core.data.local.dao.grammar.GrammarLessonDao
import com.nhom2.learnenglish.core.data.local.dao.grammar.GrammarQuestionDao
import com.nhom2.learnenglish.core.data.local.dao.grammar.UserGrammarProgressDao
import com.nhom2.learnenglish.core.data.local.entity.grammar.GrammarLessonEntity
import com.nhom2.learnenglish.core.data.local.entity.grammar.GrammarQuestionEntity
import com.nhom2.learnenglish.core.data.local.entity.grammar.UserGrammarProgress
import com.nhom2.learnenglish.core.data.model.GrammarLessonWithStatus
import com.nhom2.learnenglish.core.util.AppExecutors

class GrammarRepository(
    private val lessonDao: GrammarLessonDao,
    private val questionDao: GrammarQuestionDao,
    private val progressDao: UserGrammarProgressDao,
    executors: AppExecutors = AppExecutors.getInstance()
) : BaseRepository(executors) {

    /**
     * 1. LẤY BẢN ĐỒ LỘ TRÌNH (Danh sách bài học kèm Trạng thái khóa/mở)
     */
    fun getGrammarRoadmap(userId: Long): List<GrammarLessonWithStatus> {
        val lessons = lessonDao.getAllLessons()
        val progressList = progressDao.getAllProgressForUser(userId)

        // Chuyển List thành Map để tra cứu O(1) cho nhanh
        val progressMap = progressList.associateBy { it.lessonId }

        val roadmap = mutableListOf<GrammarLessonWithStatus>()

        // Bài ĐẦU TIÊN mặc định luôn luôn được Mở khóa
        var isPreviousLessonPassed = true

        for (lesson in lessons) {
            val progress = progressMap[lesson.id]
            val isTheoryDone = progress?.isTheoryCompleted ?: false
            val isQuizDone = progress?.isQuizPassed ?: false

            roadmap.add(
                GrammarLessonWithStatus(
                    lesson = lesson,
                    isUnlocked = isPreviousLessonPassed,
                    isTheoryCompleted = isTheoryDone,
                    isQuizPassed = isQuizDone
                )
            )

            // Cập nhật điều kiện Mở khóa cho Bài TIẾP THEO trong vòng lặp
            // Bài tiếp theo chỉ mở nếu bài hiện tại đã qua bài kiểm tra
            isPreviousLessonPassed = isQuizDone
        }

        return roadmap
    }

    /**
     * 2. LẤY CHI TIẾT ĐỂ HỌC
     */
    fun getLessonTheory(lessonId: Long): GrammarLessonEntity? {
        return lessonDao.getLessonById(lessonId)
    }

    fun getLessonQuestions(lessonId: Long): List<GrammarQuestionEntity> {
        return questionDao.getQuestionsByLessonId(lessonId)
    }

    /**
     * 3. LƯU TIẾN ĐỘ HỌC TẬP
     */

    // Gọi khi User bấm nút "Đã hiểu / Tiếp tục" ở cuối trang Lý thuyết
    fun markTheoryAsCompleted(userId: Long, lessonId: Long) {
        val progress = progressDao.getProgress(userId, lessonId)
            ?: UserGrammarProgress(userId = userId, lessonId = lessonId)

        progressDao.insertOrUpdate(progress.copy(isTheoryCompleted = true))
    }

    // Gọi khi User nộp bài Trắc nghiệm
    fun submitQuizResult(userId: Long, lessonId: Long, score: Int, isPassed: Boolean) {
        val progress = progressDao.getProgress(userId, lessonId)
            ?: UserGrammarProgress(userId = userId, lessonId = lessonId)

        // Chỉ lưu điểm cao nhất (High Score)
        val highestScore = maxOf(progress.score, score)

        progressDao.insertOrUpdate(
            progress.copy(
                // Nếu đã pass 1 lần trong quá khứ thì luôn giữ trạng thái pass
                isQuizPassed = progress.isQuizPassed || isPassed,
                score = highestScore
            )
        )
    }
}