package com.nhom2.learnenglish.core.network.dictionary

import com.google.gson.annotations.SerializedName

data class DictWordResponse(
    @SerializedName("word") val word: String?,
    @SerializedName("phonetic") val phonetic: String?,
    @SerializedName("phonetics") val phonetics: List<Phonetic>?
) {
    data class Phonetic(
        @SerializedName("text") val text: String?,
        @SerializedName("audio") val audio: String?
    )
}



data class TranslateResponse(
    @SerializedName("responseData") val responseData: ResponseData?
) {
    data class ResponseData(
        @SerializedName("translatedText") val translatedText: String?
    )
}