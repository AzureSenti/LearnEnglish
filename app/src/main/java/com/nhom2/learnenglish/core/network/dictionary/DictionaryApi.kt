package com.nhom2.learnenglish.core.network.dictionary


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DictionaryApi {

    @GET("https://api.dictionaryapi.dev/api/v2/entries/en/{word}")
    suspend fun getWordDetails(@Path("word") word: String): Response<List<DictWordResponse>>

    @GET("https://api.mymemory.translated.net/get")
    suspend fun translateWord(
        @Query("q") word: String,
        @Query("langpair") langPair: String = "en|vi"
    ): Response<TranslateResponse>
}