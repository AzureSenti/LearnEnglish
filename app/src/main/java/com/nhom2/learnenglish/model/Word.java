package com.nhom2.learnenglish.model;

/**
 * UI model cho một từ vựng trong danh sách.
 */
public class Word {

    private final long id;
    private final String english;
    private final String phonetic;
    private final String vietnameseMeaning;
    private final int level;
    private final String audioUrl;

    public Word(long id, String english, String phonetic, String vietnameseMeaning, int level, String audioUrl) {
        this.id = id;
        this.english = english;
        this.phonetic = phonetic;
        this.vietnameseMeaning = vietnameseMeaning;
        this.level = level;
        this.audioUrl = audioUrl;
    }

    public long getId() {
        return id;
    }

    public String getEnglish() {
        return english;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public String getVietnameseMeaning() {
        return vietnameseMeaning;
    }

    public int getLevel() {
        return level;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getLevelLabel() {
        return "Lv. " + level;
    }
}
