package com.nhom2.learnenglish.feature.wordsets;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.model.WordWithProgress;
import com.nhom2.learnenglish.core.data.repository.WordRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.feature.words.WordAdapter;

import java.util.List;

public class WordSetDetailActivity extends AppCompatActivity {

    private WordRepository wordRepository;
    private WordAdapter adapter;
    private long setId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_set_detail);

        setId = getIntent().getLongExtra("SET_ID", -1);
        
        setupData();
        setupToolbar();
        setupRecyclerView();
        setupBackNavigation();
        
        if (setId != -1) {
            loadWords();
        }
    }

    private void setupData() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        wordRepository = new WordRepository(
                db.wordDao(),
                db.wordSetDao(),
                db.wordSrsDao(),
                db.userWordSetDao(),
                AppExecutors.Companion.getInstance()
        );
    }

    private void setupToolbar() {
        ImageView ivBack = findViewById(R.id.iv_back);
        TextView tvTitle = findViewById(R.id.tv_title);
        
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
                // Giả định userId = -1 để lấy tất cả từ (chưa lọc theo user cụ thể trong mock data này)
                List<WordWithProgress> list =  wordRepository.getWordListWithProgress(setId, -1L);

                runOnUiThread(() -> {
                    if (adapter != null) {
                        adapter.updateData(list);
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
