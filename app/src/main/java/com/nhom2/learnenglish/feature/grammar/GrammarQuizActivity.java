package com.nhom2.learnenglish.feature.grammar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.grammar.GrammarQuestionEntity;
import com.nhom2.learnenglish.core.data.repository.GrammarRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.core.util.SessionManager;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;
import com.nhom2.learnenglish.feature.wordsets.LibraryActivity;

import java.util.ArrayList;
import java.util.List;

public class GrammarQuizActivity extends AppCompatActivity {

    private TextView tvQuestionText;
    private RadioGroup radioGroupOptions;
    private TextInputLayout layoutFillBlank;
    private TextInputEditText inputAnswer;

    private GrammarRepository grammarRepository;
    private SessionManager sessionManager;

    private long lessonId = -1;
    private List<GrammarQuestionEntity> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_quiz);

        if (getIntent() != null && getIntent().getExtras() != null) {
            lessonId = getIntent().getExtras().getLong("LESSON_ID", -1);
        }

        initViews();
        setupRepository();
        setupBottomNav();
        loadQuestions();
    }

    private void initViews() {
        tvQuestionText = findViewById(R.id.tv_question_text);
        radioGroupOptions = findViewById(R.id.radio_group_options);
        layoutFillBlank = findViewById(R.id.layout_fill_blank);
        inputAnswer = findViewById(R.id.input_answer);
        Button btnSubmitAnswer = findViewById(R.id.btn_submit_answer);

        btnSubmitAnswer.setOnClickListener(v -> checkAnswer());
    }

    private void setupRepository() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        sessionManager = new SessionManager(this);
        grammarRepository = new GrammarRepository(
                db.grammarLessonDao(), db.grammarQuestionDao(), db.userGrammarProgressDao(), AppExecutors.Companion.getInstance()
        );
    }

    private void loadQuestions() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                questions = grammarRepository.getLessonQuestions(lessonId);

                runOnUiThread(() -> {
                    if (questions.isEmpty()) {
                        Toast.makeText(this, "Chưa có bài tập cho phần này!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        displayCurrentQuestion();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void displayCurrentQuestion() {
        GrammarQuestionEntity currentQuestion = questions.get(currentQuestionIndex);
        tvQuestionText.setText(currentQuestion.getQuestionText());

        if ("MULTIPLE_CHOICE".equals(currentQuestion.getQuestionType())) {
            radioGroupOptions.setVisibility(View.VISIBLE);
            layoutFillBlank.setVisibility(View.GONE);
            radioGroupOptions.removeAllViews();

            if (currentQuestion.getOptions() != null) {
                String[] options = currentQuestion.getOptions().split(",");
                for (String opt : options) {
                    RadioButton rb = new RadioButton(this);
                    rb.setText(opt.trim());
                    rb.setTextSize(16);
                    rb.setPadding(0, 16, 0, 16);
                    radioGroupOptions.addView(rb);
                }
            }
        } else {
            radioGroupOptions.setVisibility(View.GONE);
            layoutFillBlank.setVisibility(View.VISIBLE);
            inputAnswer.setText("");
        }
    }

    private void checkAnswer() {
        GrammarQuestionEntity currentQuestion = questions.get(currentQuestionIndex);
        String userAnswer = "";

        if ("MULTIPLE_CHOICE".equals(currentQuestion.getQuestionType())) {
            int selectedId = radioGroupOptions.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadio = findViewById(selectedId);
                userAnswer = selectedRadio.getText().toString();
            } else {
                Toast.makeText(this, "Vui lòng chọn 1 đáp án", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (inputAnswer.getText() != null) {
                userAnswer = inputAnswer.getText().toString().trim();
            }
            if (userAnswer.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đáp án", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // So sánh đáp án
        if (userAnswer.equalsIgnoreCase(currentQuestion.getCorrectAnswer())) {
            Toast.makeText(this, "Chính xác!", Toast.LENGTH_SHORT).show();
            score++;
        } else {
            Toast.makeText(this, "Sai rồi! Đáp án: " + currentQuestion.getCorrectAnswer(), Toast.LENGTH_LONG).show();
        }

        // Chuyển câu tiếp theo hoặc kết thúc
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            displayCurrentQuestion();
        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {
        long currentUserId = sessionManager.getCurrentUserId();
        // Cần làm đúng tất cả để Pass (Hoặc bạn có thể tùy chỉnh logic)
        boolean isPassed = (score == questions.size());

        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                grammarRepository.submitQuizResult(currentUserId, lessonId, score, isPassed);

                runOnUiThread(() -> {
                    String msg = isPassed ? "Chúc mừng! Bạn đã hoàn thành bài học." : "Bạn cần làm đúng hết để qua bài. Điểm: " + score;
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    finish(); // Trở về Roadmap
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setupBottomNav() {
        LinearLayout navExplore = findViewById(R.id.nav_explore);
        LinearLayout navLibrary = findViewById(R.id.nav_library);

        if (navExplore != null) {
            navExplore.setOnClickListener(v -> {
                Navigator.navigateTo(this, MainMenuActivity.class);
                finishAffinity();
            });
        }
        if (navLibrary != null) {
            navLibrary.setOnClickListener(v -> {
                Navigator.navigateTo(this, LibraryActivity.class);
                finish();
            });
        }
    }
}