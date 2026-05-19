package com.nhom2.learnenglish.feature.grammar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.grammar.GrammarLessonEntity;
import com.nhom2.learnenglish.core.data.repository.GrammarRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.core.util.SessionManager;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;
import com.nhom2.learnenglish.feature.wordsets.LibraryActivity;

public class GrammarTheoryActivity extends AppCompatActivity {

    private TextView tvTitle, tvBasics, tvUsage, tvExamples;
    private GrammarRepository grammarRepository;
    private SessionManager sessionManager;
    private long lessonId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_theory);

        if (getIntent() != null && getIntent().getExtras() != null) {
            lessonId = getIntent().getExtras().getLong("LESSON_ID", -1);
        }

        if (lessonId == -1) {
            Toast.makeText(this, "Lỗi dữ liệu!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRepository();
        setupBottomNav();
        loadTheory();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvBasics = findViewById(R.id.tv_basics);
        tvUsage = findViewById(R.id.tv_usage);
        tvExamples = findViewById(R.id.tv_examples);
        Button btnContinue = findViewById(R.id.btn_continue);

        btnContinue.setOnClickListener(v -> markTheoryDoneAndContinue());
    }

    private void setupRepository() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        sessionManager = new SessionManager(this);
        grammarRepository = new GrammarRepository(
                db.grammarLessonDao(), db.grammarQuestionDao(), db.userGrammarProgressDao(), AppExecutors.Companion.getInstance()
        );
    }

    private void loadTheory() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                GrammarLessonEntity lesson = grammarRepository.getLessonTheory(lessonId);

                if (lesson != null) {
                    runOnUiThread(() -> {
                        tvTitle.setText(lesson.getTitle());
                        tvBasics.setText(lesson.getTheoryBasics());
                        tvUsage.setText(lesson.getUsageRules());
                        tvExamples.setText(lesson.getExamples());
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void markTheoryDoneAndContinue() {
        long currentUserId = sessionManager.getCurrentUserId();

        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {

                grammarRepository.markTheoryAsCompleted(currentUserId, lessonId);

                runOnUiThread(() -> {
                    // Chuyển sang trang Quiz
                    Bundle bundle = new Bundle();
                    bundle.putLong("LESSON_ID", lessonId);
                    Navigator.navigateTo(this, GrammarQuizActivity.class, bundle);
                    finish(); // Đóng trang lý thuyết
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