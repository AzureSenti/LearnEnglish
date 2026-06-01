package com.nhom2.learnenglish.feature.wordsets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.word.WordEntity;
import com.nhom2.learnenglish.core.data.model.WordWithProgress;
import com.nhom2.learnenglish.core.data.repository.WordRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.SessionManager;
import com.nhom2.learnenglish.feature.game.VocabularyGameActivity;
import com.nhom2.learnenglish.feature.words.WordAdapter;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class WordSetDetailActivity extends AppCompatActivity {
    private MaterialButton btnLearnNew, btnReview;
    private TextView tvMasteryProgress;
    private ProgressBar pbMasteryProgress;

    private WordRepository wordRepository;
    private SessionManager sessionManager;
    private WordAdapter adapter;
    private String setId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_set_detail);

        setId = getIntent().getStringExtra("SET_ID");
        if (setId == null) setId = "";
        
        setupData();
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupBackNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!setId.isEmpty()) {
            loadWords();
            loadReviewCount();
        }
    }

    private void setupData() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getCurrentUserId();

        wordRepository = new WordRepository(
                db.wordDao(),
                db.wordSetDao(),
                db.wordSrsDao(),
                db.userWordSetDao(),
                db.wordSetCrossDao(),
                db.deletedSyncItemDao(),
                AppExecutors.Companion.getInstance()
        );
    }

    private void initViews() {
        btnLearnNew = findViewById(R.id.btnLearnNew);
        btnReview = findViewById(R.id.btnReview);
        FloatingActionButton fabAddWord = findViewById(R.id.fab_add_word);

        if (btnLearnNew != null) {
            btnLearnNew.setOnClickListener(v -> {
                Intent intent = new Intent(this, VocabularyGameActivity.class);
                intent.putExtra("SET_ID", setId);
                intent.putExtra("GAME_MODE", "LEARN_NEW");
                startActivity(intent);
            });
        }

        if (btnReview != null) {
            btnReview.setOnClickListener(v -> {
                Intent intent = new Intent(this, VocabularyGameActivity.class);
                intent.putExtra("SET_ID", setId);
                intent.putExtra("GAME_MODE", "REVIEW");
                startActivity(intent);
            });
        }

        if (fabAddWord != null) {
            fabAddWord.setOnClickListener(v -> showWordForm(null));
        }
    }

    private void setupRecyclerView() {
        RecyclerView rvWords = findViewById(R.id.rv_words);
        if (rvWords != null) {
            rvWords.setLayoutManager(new LinearLayoutManager(this));
            adapter = new WordAdapter();
            
            adapter.setOnWordActionListener(new WordAdapter.OnWordActionListener() {
                @Override
                public void onWordClick(WordWithProgress word) {}

                @Override
                public void onMoreClick(WordWithProgress word, View anchor) {
                    showWordActions(word);
                }
            });
            
            rvWords.setAdapter(adapter);
        }
    }

    private void showWordForm(WordWithProgress wordToEdit) {
        final boolean isEdit = wordToEdit != null;
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_word_form, null);

        TextView tvTitle = view.findViewById(R.id.tv_sheet_title);
        EditText etEnglish = view.findViewById(R.id.et_english_word);
        EditText etMeaning = view.findViewById(R.id.et_vietnamese_meaning);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);

        // Hide set selection since it is fixed to the current set
        View labelChooseSet = view.findViewById(R.id.label_choose_set);
        View cgContainer = view.findViewById(R.id.cg_word_sets);
        if (labelChooseSet != null) labelChooseSet.setVisibility(View.GONE);
        if (cgContainer != null && cgContainer.getParent() instanceof View) {
            ((View) cgContainer.getParent()).setVisibility(View.GONE);
        }

        if (isEdit) {
            tvTitle.setText("Edit Word");
            etEnglish.setText(wordToEdit.getEnglishWord());
            etMeaning.setText(wordToEdit.getVietnameseMeaning());
            btnSave.setText("Save Changes");
        } else {
            tvTitle.setText("Add New Word");
            btnSave.setText("Add to Set");
        }

        view.findViewById(R.id.iv_close).setOnClickListener(v -> dialog.dismiss());

        // Nạp hiệu ứng lò xo khi hiện (Spring Animation giả lập qua TranslationY)
        view.setTranslationY(100f);
        view.animate().translationY(0).setDuration(400).start();

        btnSave.setOnClickListener(v -> {
            String eng = etEnglish.getText().toString().trim();
            String vi = etMeaning.getText().toString().trim();

            if (eng.isEmpty() || vi.isEmpty()) {
                Toasty.warning(this, "Vui lòng điền đủ thông tin", Toast.LENGTH_SHORT, true).show();                return;
            }

            AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
                try {
                    AppDatabase db = AppDatabase.Companion.getInstance(this);
                    if (isEdit) {
                        WordEntity existing = db.wordDao().getWordByEnglish(eng);
                        if (existing != null && existing.getId() != wordToEdit.getWordId()) {
                            runOnUiThread(() -> Toasty.warning(this, "Từ vựng tiếng Anh đã tồn tại trong hệ thống!", Toast.LENGTH_SHORT, true).show());
                            return;
                        }
                        WordEntity updated = new WordEntity(wordToEdit.getWordId(), eng, vi, wordToEdit.getAudio(), false);
                        db.wordDao().update(updated);
                    } else {
                        WordEntity existing = db.wordDao().getWordByEnglish(eng);
                        String wordIdToLink;
                        if (existing != null) {
                            wordIdToLink = existing.getId();
                        } else {
                            WordEntity newWord = new WordEntity(java.util.UUID.randomUUID().toString(), eng, vi, null, false);
                            db.wordDao().insert(newWord);
                            wordIdToLink = newWord.getId();
                        }
                        
                        boolean isAlreadyInSet = db.wordSetCrossDao().isWordInSet(wordIdToLink, setId);
                        if (isAlreadyInSet) {
                            runOnUiThread(() -> Toasty.warning(this, "Từ này đã có trong bộ từ hiện tại!", Toast.LENGTH_SHORT, true).show());
                            return;
                        }
                        
                        db.wordSetCrossDao().insert(new com.nhom2.learnenglish.core.data.local.entity.word.WordSetCrossRef(wordIdToLink, setId, false));
                    }

                    runOnUiThread(() -> {
                        Toasty.success(this, isEdit ? "Đã cập nhật!" : "Đã thêm từ mới!", Toast.LENGTH_SHORT, true).show();                        dialog.dismiss();
                        loadWords();
                        loadReviewCount();
                    });
                } catch (Exception e) { 
                    e.printStackTrace(); 
                    runOnUiThread(() -> Toasty.error(this, "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT, true).show());
                }
            });
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void showWordActions(WordWithProgress word) {
        new AlertDialog.Builder(this)
                .setTitle(word.getEnglishWord())
                .setItems(new CharSequence[]{"Sửa từ vựng", "Xóa khỏi bộ"}, (dialog, which) -> {
                    if (which == 0) showWordForm(word);
                    else confirmDeleteWord(word);
                }).show();
    }

    private void confirmDeleteWord(WordWithProgress word) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa từ")
                .setMessage("Bạn có chắc muốn gỡ \"" + word.getEnglishWord() + "\" khỏi bộ này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    wordRepository.removeWordFromSpecificSet(userId,word.getWordId(), setId, () -> {
                        Toasty.success(this, "Đã gỡ từ vựng", Toast.LENGTH_SHORT, true).show();
                        loadWords();
                        loadReviewCount();
                        return kotlin.Unit.INSTANCE;
                    });
                })
                .setNegativeButton("Hủy", null).show();
    }

    private void setupToolbar() {
        ImageView ivBack = findViewById(R.id.iv_back);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvMasteryProgress = findViewById(R.id.tv_mastery_progress_text);
        pbMasteryProgress = findViewById(R.id.pb_mastery_progress);

        String title = getIntent().getStringExtra("SET_TITLE");
        if (title != null) tvTitle.setText(title);
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());
    }

    private void loadWords() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                List<WordWithProgress> list = wordRepository.getWordListWithProgress(setId, userId);
                int masteredCount = 0;
                for (WordWithProgress word : list) {
                    if (word.getLevel() != null && word.getLevel() > 0) masteredCount++;
                }
                final int finalMasteredCount = masteredCount;
                final int totalWords = list.size();
                runOnUiThread(() -> {
                    if (adapter != null) adapter.updateData(list);
                    if (tvMasteryProgress != null && pbMasteryProgress != null) {
                        tvMasteryProgress.setText(finalMasteredCount + "/" + totalWords + " mastered");
                        pbMasteryProgress.setMax(totalWords > 0 ? totalWords : 1);
                        pbMasteryProgress.setProgress(finalMasteredCount);
                    }
                });
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    private void loadReviewCount() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                int reviewCount = wordRepository.getWordsForReview(userId, setId).size();
                runOnUiThread(() -> {
                    if (btnReview != null) {
                        btnReview.setText(reviewCount > 0 ? "ÔN TẬP (" + reviewCount + ")" : "CHƯA CÓ TỪ CẦN ÔN");
                        btnReview.setEnabled(reviewCount > 0);
                        btnReview.setAlpha(reviewCount > 0 ? 1.0f : 0.6f);
                    }
                });
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() { finish(); }
        });
    }
}
