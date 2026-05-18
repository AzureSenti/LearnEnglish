package com.nhom2.learnenglish.model;

/**
 * UI model cho bộ từ vựng (My Word Sets).
 */
public class WordSet {

    private final long id;
    private final String title;
    private final int wordCount;
    private final String categoryIcon;
    private final String description;

    public WordSet(long id, String title, int wordCount, String categoryIcon, String description) {
        this.id = id;
        this.title = title;
        this.wordCount = wordCount;
        this.categoryIcon = categoryIcon;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getWordCount() {
        return wordCount;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public String getDescription() {
        return description;
    }

    public String getWordCountLabel() {
        return wordCount + " words";
    }
}
