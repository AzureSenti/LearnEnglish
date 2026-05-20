package com.nhom2.learnenglish.feature.game;

import com.nhom2.learnenglish.core.data.local.entity.WordEntity;
import com.nhom2.learnenglish.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameLogicHelper {

    /**
     * Sinh danh sách câu hỏi. Mỗi từ vựng sẽ xuất hiện 2 lần liên tiếp:
     * 1. Trắc nghiệm (Nghĩa -> Từ hoặc ngược lại)
     * 2. Điền từ (Gõ lại từ đó)
     * Điều này giúp đảm bảo Game thứ 2 luôn xuất hiện ngay sau Game 1 của cùng 1 từ.
     */
    public static List<Question> generateQuestions(List<WordEntity> targetWords, List<WordEntity> allWords, int wordLimit) {
        List<Question> questionList = new ArrayList<>();

        if (targetWords == null || targetWords.isEmpty() || allWords == null || allWords.isEmpty()) {
            return questionList;
        }

        List<WordEntity> shuffledTargets = new ArrayList<>(targetWords);
        Collections.shuffle(shuffledTargets);

        // Giới hạn số lượng TỪ VỰNG sẽ học (mỗi từ có 2 câu hỏi)
        for (WordEntity targetWord : shuffledTargets) {
            // 1. Tạo câu hỏi trắc nghiệm
            questionList.add(createMultipleChoice(targetWord, allWords));

            // 2. Tạo câu hỏi điền từ (Game thứ 2 cho cùng từ đó)
            questionList.add(new Question(targetWord, null, -1, Question.Type.FILL_IN_BLANK));
            
            if (questionList.size() >= wordLimit) break;
        }

        return questionList;
    }

    private static Question createMultipleChoice(WordEntity targetWord, List<WordEntity> allWords) {
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
}
