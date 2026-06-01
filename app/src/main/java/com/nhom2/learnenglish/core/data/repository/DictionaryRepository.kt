package com.nhom2.learnenglish.core.data.repository

import com.nhom2.learnenglish.core.data.model.DictionaryResult
import com.nhom2.learnenglish.core.network.dictionary.DictionaryApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DictionaryRepository(
    private val dictionaryApi: DictionaryApi
) {
    /**
     * Hàm suspend này sẽ tự động gọi 2 API nối tiếp nhau và gộp kết quả lại.
     */
    suspend fun lookupWord(word: String): DictionaryResult {
        // Đảm bảo tác vụ mạng luôn chạy trên luồng IO để không đơ màn hình
        return withContext(Dispatchers.IO) {
            var phonetic = ""
            var audioUrl = ""
            var meaning = "Không thể dịch từ này."

            // --- BƯỚC 1: Gọi API lấy âm thanh ---
            try {
                val dictResponse = dictionaryApi.getWordDetails(word)
                if (dictResponse.isSuccessful && !dictResponse.body().isNullOrEmpty()) {
                    val dictData = dictResponse.body()!![0]
                    phonetic = dictData.phonetic ?: ""

                    // Lọc tìm cái link audio đầu tiên không bị rỗng
                    audioUrl = dictData.phonetics?.firstOrNull {
                        !it.audio.isNullOrEmpty()
                    }?.audio ?: ""
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // --- BƯỚC 2: Gọi API lấy nghĩa Tiếng Việt ---
            try {
                val transResponse = dictionaryApi.translateWord(word)
                if (transResponse.isSuccessful && transResponse.body() != null) {
                    meaning = transResponse.body()!!.responseData?.translatedText ?: meaning
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // --- BƯỚC 3: Trả về chiếc hộp chứa đầy đủ thông tin ---
            DictionaryResult(
                word = word,
                phonetic = phonetic,
                audioUrl = audioUrl,
                vietnameseMeaning = meaning
            )
        }
    }
}