package com.nhom2.learnenglish.core.network.sync

import com.google.gson.annotations.SerializedName


// ---------------------------------------------------------------------------
// Upload: Client → Server
// ---------------------------------------------------------------------------

data class WordSyncItem(
    @SerializedName("word_id") val wordId: String,
    @SerializedName("english_word") val englishWord: String,
    @SerializedName("vietnamese_meaning") val vietnameseMeaning: String,
    @SerializedName("audio") val audio: String? = null
)

data class WordSetSyncItem(
    @SerializedName("set_id") val setId: String,
    @SerializedName("name") val name: String,
    @SerializedName("unlock_cost") val unlockCost: Int
)

data class WordSetCrossRefSyncItem(
    @SerializedName("word_id") val wordId: String,
    @SerializedName("set_id") val setId: String
)

data class WordSrsUploadItem(
    @SerializedName("word_id") val wordId: String,
    @SerializedName("level") val level: Int,
    @SerializedName("next_review_date") val nextReviewDate: Long,
    @SerializedName("last_review_date") val lastReviewDate: Long?
)

data class GrammarProgressUploadItem(
    @SerializedName("lesson_id") val lessonId: Int,
    @SerializedName("is_theory_completed") val isTheoryCompleted: Boolean,
    @SerializedName("is_quiz_passed") val isQuizPassed: Boolean,
    @SerializedName("score") val score: Int
)

data class UserWordSetUploadItem(
    @SerializedName("set_id") val setId: String
)

data class UserProfileUploadItem(
    @SerializedName("coins") val coins: Int,
    @SerializedName("current_streak") val currentStreak: Int,
    @SerializedName("longest_streak") val longestStreak: Int
)

data class SyncUploadRequest(
    @SerializedName("words_list") val wordsList: List<WordSyncItem> = emptyList(),
    @SerializedName("word_sets_list") val wordSetsList: List<WordSetSyncItem> = emptyList(),
    @SerializedName("word_set_cross_refs_list") val wordSetCrossRefsList: List<WordSetCrossRefSyncItem> = emptyList(),

    @SerializedName("word_srs_list") val wordSrsList: List<WordSrsUploadItem> = emptyList(),
    @SerializedName("grammar_progress_list") val grammarProgressList: List<GrammarProgressUploadItem> = emptyList(),
    @SerializedName("unlocked_word_sets") val unlockedWordSets: List<UserWordSetUploadItem> = emptyList(),
    @SerializedName("user_profile") val userProfile: UserProfileUploadItem? = null
)


// ---------------------------------------------------------------------------
// Download: Server → Client
// ---------------------------------------------------------------------------

data class WordSrsDownloadItem(
    @SerializedName("word_id") val wordId: String,
    @SerializedName("level") val level: Int,
    @SerializedName("next_review_date") val nextReviewDate: Long,
    @SerializedName("last_review_date") val lastReviewDate: Long?
)

data class GrammarProgressDownloadItem(
    @SerializedName("lesson_id") val lessonId: Int,
    @SerializedName("is_theory_completed") val isTheoryCompleted: Boolean,
    @SerializedName("is_quiz_passed") val isQuizPassed: Boolean,
    @SerializedName("score") val score: Int
)

data class UserProfileDownloadItem(
    @SerializedName("coins") val coins: Int,
    @SerializedName("current_streak") val currentStreak: Int,
    @SerializedName("longest_streak") val longestStreak: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("avatar_url") val avatarUrl: String
)

data class SyncDownloadResponse(
    @SerializedName("words_list") val wordsList: List<WordSyncItem> = emptyList(),
    @SerializedName("word_sets_list") val wordSetsList: List<WordSetSyncItem> = emptyList(),
    @SerializedName("word_set_cross_refs_list") val wordSetCrossRefsList: List<WordSetCrossRefSyncItem> = emptyList(),

    @SerializedName("word_srs_list") val wordSrsList: List<WordSrsDownloadItem> = emptyList(),
    @SerializedName("grammar_progress_list") val grammarProgressList: List<GrammarProgressDownloadItem> = emptyList(),
    @SerializedName("unlocked_word_set_ids") val unlockedWordSetIds: List<String> = emptyList(),
    @SerializedName("user_profile") val userProfile: UserProfileDownloadItem
)
