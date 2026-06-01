package com.nhom2.learnenglish.feature.game;

import com.nhom2.learnenglish.core.data.local.entity.word.WordEntity;
import com.nhom2.learnenglish.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameLogicHelper {

    /**
     * Sinh câu hỏi trắc nghiệm (Game 1)
     */
    public static Question generateMultipleChoice(WordEntity targetWord, List<WordEntity> allWords) {
        String correctAnswer = targetWord.getVietnameseMeaning().trim();
        List<String> distractors = new ArrayList<>();
        List<WordEntity> pool = new ArrayList<>(allWords);
        Collections.shuffle(pool);

        for (WordEntity word : pool) {
            if (distractors.size() >= 3) break;
            String meaning = word.getVietnameseMeaning().trim();
            if (word.getId() != targetWord.getId() && !meaning.equalsIgnoreCase(correctAnswer) && !distractors.contains(meaning)) {
                distractors.add(meaning);
            }
        }

        List<String> options = new ArrayList<>(distractors);
        options.add(correctAnswer);
        Collections.shuffle(options);

        return new Question(targetWord, options, options.indexOf(correctAnswer), Question.Type.MULTIPLE_CHOICE);
    }

    /**
     * Sinh câu hỏi điền từ (Game 2)
     */
    public static Question generateFillInBlank(WordEntity targetWord) {
        return new Question(targetWord, null, -1, Question.Type.FILL_IN_BLANK);
    }
}