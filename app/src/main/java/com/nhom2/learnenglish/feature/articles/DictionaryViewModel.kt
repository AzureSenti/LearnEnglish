package com.nhom2.learnenglish.feature.dictionary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom2.learnenglish.core.data.local.entity.word.WordEntity
import com.nhom2.learnenglish.core.data.model.DictionaryResult
import com.nhom2.learnenglish.core.data.repository.DictionaryRepository
import com.nhom2.learnenglish.core.data.repository.WordRepository
import kotlinx.coroutines.launch

class DictionaryViewModel(
    private val dictionaryRepo: DictionaryRepository,
    private val wordRepo: WordRepository
) : ViewModel() {

    // Trả về kết quả dịch để Activity hiển thị lên BottomSheet
    private val _translationResult = MutableLiveData<DictionaryResult?>()
    val translationResult: LiveData<DictionaryResult?> get() = _translationResult

    // Trả về thông báo (Lưu thành công hoặc Lỗi trùng từ)
    private val _saveStatus = MutableLiveData<String>()
    val saveStatus: LiveData<String> get() = _saveStatus

    // 1. Hàm gọi API dịch từ
    fun translateWord(word: String) {
        viewModelScope.launch {
            try {
                val result = dictionaryRepo.lookupWord(word)
                _translationResult.postValue(result)
            } catch (e: Exception) {
                _translationResult.postValue(null)
            }
        }
    }

    // 2. Hàm lưu từ vựng vào Room Database
    fun saveWordToSet(dictResult: DictionaryResult, setId: Long) {
        val newWord = WordEntity(
            id = 0, // id = 0 để Room tự động tăng
            englishWord = dictResult.word,
            vietnameseMeaning = dictResult.vietnameseMeaning,
            audio = dictResult.audioUrl
        )

        wordRepo.addNewWordToSet(
            word = newWord,
            setId = setId,
            onSuccess = { _saveStatus.postValue("Lưu từ vựng thành công!") },
            onError = { errorMsg -> _saveStatus.postValue(errorMsg) }
        )
    }
}