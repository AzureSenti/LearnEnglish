package com.nhom2.learnenglish.model;

import com.nhom2.learnenglish.core.data.local.entity.word.WordEntity;
import com.nhom2.learnenglish.core.data.local.entity.word.WordEntity;

import java.util.List;

public class Question {

    private WordEntity targetWord;
    private List<String> options;
    private int correctOptionIndex;
    private Type type;

    public Question(com.nhom2.learnenglish.core.data.local.entity.word.WordEntity targetWord, Object options, int correctOptionIndex, Type type) {
    }

    // Định nghĩa 2 loại câu hỏi: Trắc nghiệm và Điền từ
    public enum Type {
        MULTIPLE_CHOICE,
        FILL_IN_BLANK
    }

    // Constructor
    public Question(WordEntity targetWord, List<String> options, int correctOptionIndex, Type type) {
        this.targetWord = targetWord;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.type = type;
    }

    // Các hàm Getter để lấy dữ liệu
    public WordEntity getTargetWord() {
        return targetWord;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public Type getType() {
        return type;
    }
}