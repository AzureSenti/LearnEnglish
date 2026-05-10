package com.nhom2.learnenglish;

public class Article {
    private String title;
    private String description;
    private String level;
    private String category;
    private String readTime;
    private boolean isCompleted;

    public Article(String title, String description, String level, String category, String readTime, boolean isCompleted) {
        this.title = title;
        this.description = description;
        this.level = level;
        this.category = category;
        this.readTime = readTime;
        this.isCompleted = isCompleted;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLevel() { return level; }
    public String getCategory() { return category; }
    public String getReadTime() { return readTime; }
    public boolean isCompleted() { return isCompleted; }
}