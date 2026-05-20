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
import com.nhom2.learnenglish.core.data.local.entity.WordEntity;
import com.nhom2.learnenglish.core.data.repository.wordRespotoryTest1;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.SessionManager;
import com.nhom2.learnenglish.model.Question;

import java.util.ArrayList;
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

    private wordRespotoryTest1 wordRepository;
    private SessionManager sessionManager;
    private List<Question> questionList = new ArrayList<>();
    private TextToSpeech tts;

    private int currentQuestionIndex = 0;
    private int score = 0;
    private boolean isAnswerRevealed = false;
    private GameOptionAdapter adapter;
    private long userId = 1L;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_game);

        sessionManager = new SessionManager(this);
        // Try-Catch ở đây để phòng trường hợp hàm getUserId bên SessionManager bị lỗi chưa viết xong
        try {
            long currentUserId = sessionManager.getUserId();
            if (currentUserId != -1L) {
                userId = currentUserId;
            }
        } catch (Exception e) {
            userId = 1L;
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
                if (!isAnswerRevealed && !questionList.isEmpty() && currentQuestionIndex < questionList.size()) {
                    if (questionList.get(currentQuestionIndex).getType() == Question.Type.FILL_IN_BLANK) {
                        btnContinue.setEnabled(s.toString().trim().length() > 0);
                    }
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
        // FIX: Đã xóa tham số db.wordSetCrossDao() bị thừa, giờ chỉ còn đúng 5 tham số
        wordRepository = new wordRespotoryTest1(
                db.wordDao(),
                db.wordSetDao(),
                db.wordSrsDao(),
                db.userWordSetDao(),
                AppExecutors.Companion.getInstance()
        );
    }

    private void setupTTS() {
        tts = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.US);
            }
        });
    }

    private void loadGameData(long setId) {
        String gameMode = getIntent().getStringExtra("GAME_MODE");

        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                List<WordEntity> targetWords = new ArrayList<>();

                // FIX: Dùng các hàm ForJava để lấy dữ liệu đồng bộ
                if ("REVIEW".equals(gameMode)) {
                    targetWords = wordRepository.getWordsForReviewForJava(userId);
                } else if (setId != -1L) {
                    if ("LEARN_NEW".equals(gameMode)) {
                        targetWords = wordRepository.getNewWordsToLearnForJava(userId, setId);
                    } else {
                        targetWords = wordRepository.getWordsInSetForJava(setId);
                    }
                }

                List<WordEntity> allWords = wordRepository.getAllWordsForJava();
                List<Question> generated = GameLogicHelper.generateQuestions(targetWords, allWords, 20);

                AppExecutors.Companion.getInstance().getMainThread().execute(() -> {
                    if (generated != null && !generated.isEmpty()) {
                        questionList = generated;
                        progressBar.setMax(questionList.size());
                        showQuestion(0);
                    } else {
                        String msg = "Không có từ vựng nào để thực hiện!";
                        if ("REVIEW".equals(gameMode)) {
                            msg = "Bạn đã hoàn thành hết các từ cần ôn tập!";
                        } else if ("LEARN_NEW".equals(gameMode)) {
                            msg = "Bạn đã học hết từ vựng mới trong bộ này!";
                        }
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                AppExecutors.Companion.getInstance().getMainThread().execute(() -> {
                    Toast.makeText(this, "Lỗi khi tải dữ liệu game", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void showQuestion(int index) {
        Question question = questionList.get(index);
        isAnswerRevealed = false;
        btnContinue.setText("KIỂM TRA");
        btnContinue.setEnabled(false);
        progressBar.setProgress(index);

        if (question.getType() == Question.Type.MULTIPLE_CHOICE) {
            tvWord.setText(question.getTargetWord().getEnglishWord());
            rvOptions.setVisibility(View.VISIBLE);
            layoutFillBlank.setVisibility(View.GONE);

            adapter = new GameOptionAdapter(question.getOptions(), question.getCorrectOptionIndex(), position -> {
                btnContinue.setEnabled(true);
            });
            rvOptions.setAdapter(adapter);
        } else {
            tvWord.setText(question.getTargetWord().getVietnameseMeaning());
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
                tts.speak(question.getTargetWord().getEnglishWord(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    private void handleContinueClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < 600) return;
        lastClickTime = currentTime;

        if (questionList.isEmpty() || currentQuestionIndex >= questionList.size()) return;

        Question currentQuestion = questionList.get(currentQuestionIndex);

        if (!isAnswerRevealed) {
            boolean isCorrect;
            if (currentQuestion.getType() == Question.Type.MULTIPLE_CHOICE) {
                isCorrect = adapter.checkAnswer();
            } else {
                String userAnswer = etAnswer.getText().toString().trim();
                String correctAnswer = currentQuestion.getTargetWord().getEnglishWord().trim();
                isCorrect = userAnswer.equalsIgnoreCase(correctAnswer);
                showFillBlankFeedback(isCorrect, correctAnswer);
            }

            if (isCorrect) score++;

            boolean finalIsCorrect = isCorrect;
            AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
                // FIX: Dùng hàm ForJava để lưu DB
                wordRepository.processWordLearningForJava(userId, currentQuestion.getTargetWord().getId(), finalIsCorrect);
            });

            isAnswerRevealed = true;
            btnContinue.setText("TIẾP TỤC");
            btnContinue.setEnabled(true);
        } else {
            currentQuestionIndex++;
            if (currentQuestionIndex < questionList.size()) {
                showQuestion(currentQuestionIndex);
            } else {
                finishGame();
            }
        }
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

    private void finishGame() {
        progressBar.setProgress(questionList.size());
        Toast.makeText(this, "Hoàn thành! Điểm: " + score + "/" + questionList.size(), Toast.LENGTH_LONG).show();
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