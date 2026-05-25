package com.nhom2.learnenglish.core.data.model

import com.nhom2.learnenglish.core.data.local.entity.grammar.GrammarLessonEntity

data class GrammarLessonWithStatus(
    val lesson: GrammarLessonEntity,
    val isUnlocked: Boolean,
    val isTheoryCompleted: Boolean,
    val isQuizPassed: Boolean
)
