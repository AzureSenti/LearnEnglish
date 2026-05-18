package com.nhom2.learnenglish.model;

import com.nhom2.learnenglish.core.data.local.entity.WordSetEntity;
import com.nhom2.learnenglish.core.data.local.model.WordWithProgress;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class WordSetMapper {

    private WordSetMapper() {
    }

    public static WordSet fromEntity(WordSetEntity entity, int wordCount) {
        return new WordSet(
                entity.getId(),
                entity.getName(),
                wordCount,
                entity.getIconCategory() != null ? entity.getIconCategory() : "folder",
                entity.getDescription()
        );
    }

    public static List<WordSet> fromEntities(List<WordSetEntity> entities, Map<Long, Integer> countBySetId) {
        List<WordSet> result = new ArrayList<>();
        for (WordSetEntity entity : entities) {
            int count = countBySetId.containsKey(entity.getId())
                    ? countBySetId.get(entity.getId())
                    : 0;
            result.add(fromEntity(entity, count));
        }
        return result;
    }

    public static Word fromProgress(WordWithProgress item) {
        String phonetic = item.getVietnameseMeaning();
        int level = item.getLevel() != null ? item.getLevel() : 0;
        return new Word(
                item.getWordId(),
                item.getEnglishWord(),
                phonetic,
                item.getVietnameseMeaning(),
                level,
                item.getAudio()
        );
    }

    public static List<Word> fromProgressList(List<WordWithProgress> items) {
        List<Word> result = new ArrayList<>();
        for (WordWithProgress item : items) {
            result.add(fromProgress(item));
        }
        return result;
    }
}
