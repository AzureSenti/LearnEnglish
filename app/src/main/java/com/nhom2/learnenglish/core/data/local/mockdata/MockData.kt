package com.nhom2.learnenglish.core.data.local.mockdata

import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity
import com.nhom2.learnenglish.core.data.local.entity.WordEntity
import com.nhom2.learnenglish.core.data.local.entity.WordSetCrossRef
import com.nhom2.learnenglish.core.data.local.entity.WordSetEntity

object MockData {

    val wordSets = listOf(
        WordSetEntity(id = 1, name = "Irregular Verbs", description = "Essential verb forms", unlockCost = 0, iconCategory = "book"),
        WordSetEntity(id = 2, name = "IELTS Prep", description = "Academic vocabulary", unlockCost = 0, iconCategory = "study"),
        WordSetEntity(id = 3, name = "IT Vocabulary", description = "Tech & programming", unlockCost = 0, iconCategory = "laptop"),
        WordSetEntity(id = 4, name = "Travel Essentials", description = "Airport & trips", unlockCost = 0, iconCategory = "travel"),
        WordSetEntity(id = 5, name = "Business Idioms", description = "Workplace phrases", unlockCost = 0, iconCategory = "business")
    )

    val words = listOf(
        WordEntity(id = 1, englishWord = "Go", vietnameseMeaning = "Đi", audio = null),
        WordEntity(id = 2, englishWord = "Went", vietnameseMeaning = "Đã đi", audio = null),
        WordEntity(id = 3, englishWord = "Gone", vietnameseMeaning = "Đã đi (V3)", audio = null),
        WordEntity(id = 4, englishWord = "Take", vietnameseMeaning = "Lấy", audio = null),
        WordEntity(id = 5, englishWord = "Took", vietnameseMeaning = "Đã lấy", audio = null),

        WordEntity(id = 6, englishWord = "Resilient", vietnameseMeaning = "Kiên cường", audio = null),
        WordEntity(id = 7, englishWord = "Articulate", vietnameseMeaning = "Diễn đạt rõ ràng", audio = null),
        WordEntity(id = 8, englishWord = "Coherent", vietnameseMeaning = "Mạch lạc", audio = null),
        WordEntity(id = 9, englishWord = "Proficient", vietnameseMeaning = "Thành thạo", audio = null),
        WordEntity(id = 10, englishWord = "Comprehensive", vietnameseMeaning = "Toàn diện", audio = null),

        WordEntity(id = 11, englishWord = "Database", vietnameseMeaning = "Cơ sở dữ liệu", audio = null),
        WordEntity(id = 12, englishWord = "Algorithm", vietnameseMeaning = "Thuật toán", audio = null),
        WordEntity(id = 13, englishWord = "Deployment", vietnameseMeaning = "Triển khai", audio = null),

        WordEntity(id = 14, englishWord = "Itinerary", vietnameseMeaning = "Lịch trình", audio = null),
        WordEntity(id = 15, englishWord = "Boarding pass", vietnameseMeaning = "Thẻ lên máy bay", audio = null),

        WordEntity(id = 16, englishWord = "Touch base", vietnameseMeaning = "Liên lạc nhanh", audio = null),
        WordEntity(id = 17, englishWord = "Ballpark figure", vietnameseMeaning = "Con số ước lượng", audio = null)
    )

    val wordSetRefs = listOf(
        WordSetCrossRef(1, 1), WordSetCrossRef(2, 1), WordSetCrossRef(3, 1), WordSetCrossRef(4, 1), WordSetCrossRef(5, 1),
        WordSetCrossRef(6, 2), WordSetCrossRef(7, 2), WordSetCrossRef(8, 2), WordSetCrossRef(9, 2), WordSetCrossRef(10, 2),
        WordSetCrossRef(11, 3), WordSetCrossRef(12, 3), WordSetCrossRef(13, 3),
        WordSetCrossRef(14, 4), WordSetCrossRef(15, 4),
        WordSetCrossRef(16, 5), WordSetCrossRef(17, 5)
    )

    val articles = listOf(
        ArticleEntity(
            title = "Understanding Emotional Elasticity",
            content = "Emotional elasticity is the cognitive ability to adapt to stressful situations and bounce back from adversity without losing your inner sense of calm. In the journey of language acquisition, resilience becomes a vital pillar.\n\nWhen we encounter a phrase we do not understand, frustration can trigger a fight or flight response. True mastery is not the absence of errors, but the graceful recovery from them.",
            level = "B2 Intermediate",
            category = "English Mastery",
            readTime = "8 min read",
            author = "Dr. Elena Vance",
            isCompleted = false
        ),
        ArticleEntity(
            title = "A Saharan Spinosaurus",
            content = "Today, Jenguebi is dry and barren, with few trees and endless sand. Sereno was drawn to the Sahara because of a report by French geologist Hugues Faure in the 1950s.\n\nLed by a local guide, Sereno and Vidal found a Spinosaurus jawbone along with other fossils including Carcharodontosaurus and sauropod dinosaurs.",
            level = "B2 Intermediate",
            category = "Science",
            readTime = "5 min read",
            isCompleted = true
        )
    )
}
