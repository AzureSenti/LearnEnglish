package com.nhom2.learnenglish.feature.wordsets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.model.WordWithProgress;
import com.nhom2.learnenglish.core.data.repository.WordRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.SessionManager; // IMPORT THÊM
import com.nhom2.learnenglish.feature.game.VocabularyGameActivity;
import com.nhom2.learnenglish.feature.words.WordAdapter;

import java.util.List;

public class WordSetDetailActivity extends AppCompatActivity {
    private MaterialButton btnLearnNew;
    private MaterialButton btnReview;
    private TextView tvMasteryProgress;
    private ProgressBar pbMasteryProgress;

    private WordRepository wordRepository;
    private SessionManager sessionManager; // THÊM SESSION MANAGER
    private WordAdapter adapter;
    private long setId;
    private String userId; // LƯU USER ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_set_detail);

        setId = getIntent().getLongExtra("SET_ID", -1);

        setupData(); // Khởi tạo dữ liệu trước
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupBackNavigation();

        // Chú ý: Đã bỏ loadWords() ở đây, chuyển sang onResume() để auto-refresh
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mỗi lần màn hình này hiện lên (kể cả khi vừa chơi game xong quay lại),
        // nó sẽ tự động tính toán lại số từ và cập nhật giao diện
        if (setId != -1) {
            loadWords();
            loadReviewCount(); // Tính số lượng từ cần ôn
        }
    }

    private void setupData() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getCurrentUserId(); // Lấy ID chuẩn

        wordRepository = new WordRepository(
                db.wordDao(),
                db.wordSetDao(),
                db.wordSrsDao(),
                db.userWordSetDao(),
                db.wordSetCrossDao(),
                AppExecutors.Companion.getInstance()
        );
    }

    private void initViews() {
        btnLearnNew = findViewById(R.id.btnLearnNew);
        btnReview = findViewById(R.id.btnReview);

        if (btnLearnNew != null) {
            btnLearnNew.setOnClickListener(v -> {
                Intent intent = new Intent(WordSetDetailActivity.this, VocabularyGameActivity.class);
                intent.putExtra("SET_ID", setId);
                intent.putExtra("GAME_MODE", "LEARN_NEW");
                startActivity(intent);
            });
        }

        if (btnReview != null) {
            btnReview.setOnClickListener(v -> {
                Intent intent = new Intent(WordSetDetailActivity.this, VocabularyGameActivity.class);
                intent.putExtra("SET_ID", setId);
                intent.putExtra("GAME_MODE", "REVIEW");
                startActivity(intent);
            });
        }
    }

    private void setupToolbar() {
        ImageView ivBack = findViewById(R.id.iv_back);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvMasteryProgress = findViewById(R.id.tv_mastery_progress_text);
        pbMasteryProgress = findViewById(R.id.pb_mastery_progress);

        String title = getIntent().getStringExtra("SET_TITLE");
        if (title != null) {
            tvTitle.setText(title);
        }

        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }
    }

    private void setupRecyclerView() {
        RecyclerView rvWords = findViewById(R.id.rv_words);
        if (rvWords != null) {
            rvWords.setLayoutManager(new LinearLayoutManager(this));
            adapter = new WordAdapter();
            rvWords.setAdapter(adapter);
        }
    }

    private void loadWords() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                List<WordWithProgress> list = wordRepository.getWordListWithProgress(setId, userId);

                // Tính toán tiến độ
                int masteredCount = 0;
                for (WordWithProgress word : list) {
                    // Giả định từ có level > 0 là đã học thuộc (mastered)
                    if (word.getLevel() != null && word.getLevel() > 0) {
                        masteredCount++;
                    }
                }

                final int finalMasteredCount = masteredCount;
                final int totalWords = list.size();

                runOnUiThread(() -> {
                    if (adapter != null) {
                        adapter.updateData(list);
                    }
                    // Cập nhật UI cho Progress Bar
                    if (tvMasteryProgress != null && pbMasteryProgress != null) {
                        tvMasteryProgress.setText(finalMasteredCount + "/" + totalWords + " words mastered");
                        pbMasteryProgress.setMax(totalWords > 0 ? totalWords : 1);
                        pbMasteryProgress.setProgress(finalMasteredCount);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // TÍNH NĂNG MỚI: Đếm số từ đến hạn ôn tập và cập nhật UI
    private void loadReviewCount() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                // Lấy danh sách từ ĐẾN HẠN của user này
                int reviewCount = wordRepository.getWordsForReview(userId).size();

                runOnUiThread(() -> {
                    if (btnReview != null) {
                        if (reviewCount > 0) {
                            btnReview.setText("ÔN TẬP (" + reviewCount + ")");
                            btnReview.setEnabled(true);
                            btnReview.setAlpha(1.0f);
                        } else {
                            btnReview.setText("CHƯA CÓ TỪ CẦN ÔN");
                            btnReview.setEnabled(false); // Khóa nút nếu ko có từ
                            btnReview.setAlpha(0.6f);    // Làm mờ nút
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }
}