package com.nhom2.learnenglish.feature.game;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.word.WordEntity;
import com.nhom2.learnenglish.core.data.repository.WordRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.SessionManager;
import com.nhom2.learnenglish.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class VocabularyGameActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ProgressBar progressBar;
    private TextView tvWord;
    private RecyclerView rvOptions;
    private MaterialButton btnContinue;
    private View btnSpeak;

    private LinearLayout layoutFillBlank;
    private TextInputLayout tilAnswer;
    private TextInputEditText etAnswer;
    private TextView tvFeedback;

    private WordRepository wordRepository;
    private SessionManager sessionManager;
    private TextToSpeech tts;

    // Trạng thái hàng đợi từ vựng
    private List<WordEntity> wordQueue = new ArrayList<>();
    private List<WordEntity> allWords = new ArrayList<>();
    private int totalWordsInSession = 0; // Quản lý tổng số từ của màn học
    private boolean isCurrentWordInGame2 = false; // false = Game 1 (Trắc nghiệm), true = Game 2 (Gõ từ)
    private Question currentQuestion;

    private int score = 0;
    private boolean isAnswerRevealed = false;
    private GameOptionAdapter adapter;
    private String userId = "-1"; // Đặt mặc định là tài khoản ngoại tuyến/local
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_game);

        sessionManager = new SessionManager(this);
        try {
            String currentUserId = sessionManager.getCurrentUserId();
            if (currentUserId != "-1") {
                userId = currentUserId;
            }
        } catch (Exception e) {
            userId = "-1";
        }

        initViews();
        setupRepository();
        setupTTS();

        long setId = getIntent().getLongExtra("SET_ID", -1L);
        loadGameData(setId);

        btnBack.setOnClickListener(v -> finish());
        btnContinue.setOnClickListener(v -> handleContinueClick());

        etAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isAnswerRevealed && currentQuestion != null && currentQuestion.getType() == Question.Type.FILL_IN_BLANK) {
                    btnContinue.setEnabled(s.toString().trim().length() > 0);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
        tvWord = findViewById(R.id.tvWord);
        rvOptions = findViewById(R.id.rvOptions);
        btnContinue = findViewById(R.id.btnContinue);
        btnSpeak = findViewById(R.id.btnSpeak);

        layoutFillBlank = findViewById(R.id.layoutFillBlank);
        tilAnswer = findViewById(R.id.tilAnswer);
        etAnswer = findViewById(R.id.etAnswer);
        tvFeedback = findViewById(R.id.tvFeedback);

        rvOptions.setLayoutManager(new LinearLayoutManager(this));
        btnContinue.setEnabled(false);
    }

    private void setupRepository() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        wordRepository = new WordRepository(
                db.wordDao(),
                db.wordSetDao(),
                db.wordSrsDao(),
                db.userWordSetDao(),
                db.wordSetCrossDao(),
                AppExecutors.Companion.getInstance()
        );
    }

    private void setupTTS() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    if (btnSpeak != null) {
                        btnSpeak.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void loadGameData(long setId) {
        String gameMode = getIntent().getStringExtra("GAME_MODE");

        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                List<WordEntity> tempWords = new ArrayList<>();

                if ("REVIEW".equals(gameMode)) {
                    if (setId != -1L) {
                        // Ôn tập riêng một bộ
                        tempWords = wordRepository.getWordsForReview(userId, setId);
                    } else {
                        // Ôn tập toàn cục
                        tempWords = wordRepository.getGlobalWordsForReview(userId);
                    }
                } else if ("LEARN_NEW".equals(gameMode)) {
                    if (setId != -1L) {
                        // Học mới từ của một bộ
                        tempWords = wordRepository.getNewWordsToLearn(userId, setId);
                    } else {
                        // Học mới toàn cục (Tất cả các bộ)
                        tempWords = wordRepository.getGlobalNewWordsToLearn(userId);
                    }
                } else if (setId != -1L) {
                    // Mặc định -> Lấy tất cả từ trong Set
                    tempWords = wordRepository.getWordsInSet(setId);
                }

                allWords = wordRepository.getAllWords();

                final List<WordEntity> finalTargetWords = tempWords;

                AppExecutors.Companion.getInstance().getMainThread().execute(() -> {
                    // Phòng tránh lỗi khi người dùng bấm thoát trước khi luồng ngầm chạy xong
                    if (isFinishing() || isDestroyed()) return;

                    if (finalTargetWords != null && !finalTargetWords.isEmpty()) {
                        Collections.shuffle(finalTargetWords);

                        List<WordEntity> selected = finalTargetWords.size() > 10 ? finalTargetWords.subList(0, 10) : finalTargetWords;
                        wordQueue = new ArrayList<>(selected);
                        totalWordsInSession = wordQueue.size();

                        progressBar.setMax(totalWordsInSession);
                        showNextGame();
                    } else {
                        String msg = "Không có từ vựng nào để thực hiện!";
                        if ("REVIEW".equals(gameMode)) {
                            msg = "Bạn đã hoàn thành hết các từ cần ôn tập!";
                        } else if ("LEARN_NEW".equals(gameMode)) {
                            msg = "Bạn đã học hết từ vựng mới trong bộ này!";
                        }
                        Toast.makeText(VocabularyGameActivity.this, msg, Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                AppExecutors.Companion.getInstance().getMainThread().execute(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    Toast.makeText(VocabularyGameActivity.this, "Lỗi khi tải dữ liệu game", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void showNextGame() {
        if (wordQueue.isEmpty()) {
            finishGame();
            return;
        }

        WordEntity currentWord = wordQueue.get(0);
        isAnswerRevealed = false;
        btnContinue.setText("KIỂM TRA");
        btnContinue.setEnabled(false);

        progressBar.setProgress(totalWordsInSession - wordQueue.size());

        if (!isCurrentWordInGame2) {
            // Game 1: Trắc nghiệm
            hideKeyboard(); // Ẩn bàn phím ảo nhằm tránh che khuất đáp án trắc nghiệm
            currentQuestion = GameLogicHelper.generateMultipleChoice(currentWord, allWords);
            tvWord.setText(currentQuestion.getTargetWord().getEnglishWord());
            rvOptions.setVisibility(View.VISIBLE);
            layoutFillBlank.setVisibility(View.GONE);

            adapter = new GameOptionAdapter(currentQuestion.getOptions(), currentQuestion.getCorrectOptionIndex(), position -> {
                btnContinue.setEnabled(true);
            });
            rvOptions.setAdapter(adapter);
        } else {
            // Game 2: Gõ từ
            currentQuestion = GameLogicHelper.generateFillInBlank(currentWord);
            tvWord.setText(currentQuestion.getTargetWord().getVietnameseMeaning());
            rvOptions.setVisibility(View.GONE);
            layoutFillBlank.setVisibility(View.VISIBLE);

            etAnswer.setText("");
            etAnswer.setEnabled(true);
            tvFeedback.setVisibility(View.INVISIBLE);
            tilAnswer.setBoxStrokeColor(ContextCompat.getColor(this, R.color.border_light));
            etAnswer.requestFocus();
        }

        btnSpeak.setOnClickListener(v -> {
            if (tts != null) {
                tts.speak(currentQuestion.getTargetWord().getEnglishWord(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    private void handleContinueClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < 600) return;
        lastClickTime = currentTime;

        if (isAnswerRevealed) {
            showNextGame();
            return;
        }

        if (wordQueue.isEmpty()) return;

        boolean isCorrect;
        if (currentQuestion.getType() == Question.Type.MULTIPLE_CHOICE) {
            isCorrect = adapter.checkAnswer();
        } else {
            String userAnswer = etAnswer.getText().toString().trim();
            String correctAnswer = currentQuestion.getTargetWord().getEnglishWord().trim();
            isCorrect = userAnswer.equalsIgnoreCase(correctAnswer);
            showFillBlankFeedback(isCorrect, correctAnswer);
        }

        WordEntity currentWord = wordQueue.get(0);

        if (!isCurrentWordInGame2) {
            if (isCorrect) {
                isCurrentWordInGame2 = true;
            } else {
                saveProgress(currentWord.getId(), false);
                wordQueue.remove(0);
                wordQueue.add(currentWord);
                isCurrentWordInGame2 = false;
            }
        } else {
            if (isCorrect) {
                score++;
                saveProgress(currentWord.getId(), true);
                wordQueue.remove(0);
            } else {
                saveProgress(currentWord.getId(), false);
                wordQueue.remove(0);
                wordQueue.add(currentWord);
            }
            isCurrentWordInGame2 = false;
        }

        isAnswerRevealed = true;
        btnContinue.setText("TIẾP TỤC");
        btnContinue.setEnabled(true);
    }

    private void saveProgress(long wordId, boolean isMastered) {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                wordRepository.processWordLearning(userId, wordId, isMastered);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void showFillBlankFeedback(boolean isCorrect, String correctAnswer) {
        tvFeedback.setVisibility(View.VISIBLE);
        if (isCorrect) {
            tvFeedback.setText(R.string.game_correct);
            tvFeedback.setTextColor(ContextCompat.getColor(this, R.color.green_tag_text));
            tilAnswer.setBoxStrokeColor(ContextCompat.getColor(this, R.color.green_tag_text));
        } else {
            String feedback = getString(R.string.game_incorrect_template, correctAnswer);
            tvFeedback.setText(feedback);
            tvFeedback.setTextColor(ContextCompat.getColor(this, R.color.error_red));
            tilAnswer.setBoxStrokeColor(ContextCompat.getColor(this, R.color.error_red));
        }
        etAnswer.setEnabled(false);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void finishGame() {
        progressBar.setProgress(totalWordsInSession);
        Toast.makeText(this, "Hoàn thành! Bạn đã thuộc " + score + " từ.", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}