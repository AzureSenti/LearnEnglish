

package com.nhom2.learnenglish.model;

public class Word {
    private String word;
    private String phonetic;
    private String level;

    public Word(String word, String phonetic, String level) {
        this.word = word;
        this.phonetic = phonetic;
        this.level = level;
    }

    public String getWord() { return word; }
    public String getPhonetic() { return phonetic; }
    public String getLevel() { return level; }
}
