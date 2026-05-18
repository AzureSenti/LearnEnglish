package com.nhom2.learnenglish.feature.wordsets;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.WordEntity;
import com.nhom2.learnenglish.core.data.local.model.WordWithProgress;
import com.nhom2.learnenglish.core.data.repository.WordRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.databinding.ActivityWordSetDetailBinding;
import com.nhom2.learnenglish.feature.words.WordAdapter;
import com.nhom2.learnenglish.model.Word;
import com.nhom2.learnenglish.model.WordSetMapper;
import com.nhom2.learnenglish.ui.bottomsheet.WordFormBottomSheetDialog;

import java.util.List;

public class WordSetDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SET_ID = "SET_ID";
    public static final String EXTRA_SET_TITLE = "SET_TITLE";

    private ActivityWordSetDetailBinding binding;
    private WordRepository wordRepository;
    private WordAdapter adapter;
    private long setId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWordSetDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setId = getIntent().getLongExtra(EXTRA_SET_ID, -1);

        setupData();
        setupToolbar();
        setupRecyclerView();
        setupFab();
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
                db.wordSetCrossDao(),
                AppExecutors.Companion.getInstance()
        );
    }

    private void setupFab() {
        binding.fabAddWord.setOnClickListener(v -> showWordForm(null));
    }

    private void setupToolbar() {
        String title = getIntent().getStringExtra(EXTRA_SET_TITLE);
        if (title != null) {
            binding.tvTitle.setText(title);
        }
        binding.ivBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        binding.rvWords.setLayoutManager(new LinearLayoutManager(this));
        binding.rvWords.setItemAnimator(new DefaultItemAnimator());
        adapter = new WordAdapter();
        adapter.setOnWordActionListener(this::showWordMenu);
        binding.rvWords.setAdapter(adapter);
    }

    private void showWordMenu(Word word, View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_word_set, popup.getMenu());
        popup.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.action_edit) {
                showWordForm(word);
                return true;
            }
            if (id == R.id.action_delete) {
                confirmRemoveWord(word);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showWordForm(Word existing) {
        WordFormBottomSheetDialog sheet = existing == null
                ? WordFormBottomSheetDialog.newInstanceForAdd()
                : WordFormBottomSheetDialog.newInstanceForEdit(
                        existing.getId(),
                        existing.getEnglish(),
                        existing.getVietnameseMeaning()
                );
        sheet.setListener(this::saveWord);
        sheet.show(getSupportFragmentManager(), "word_form");
    }

    private void saveWord(String english, String vietnamese, Long wordId) {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                if (wordId == null) {
                    kotlinx.coroutines.BuildersKt.runBlocking(
                            kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                            (scope, continuation) -> wordRepository.addWordToSet(
                                    setId, english, vietnamese, null, continuation)
                    );
                } else {
                    kotlinx.coroutines.BuildersKt.runBlocking(
                            kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                            (scope, continuation) -> {
                                WordEntity entity = new WordEntity(wordId, english, vietnamese, null);
                                wordRepository.updateWord(entity, continuation);
                                return kotlin.Unit.INSTANCE;
                            }
                    );
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT).show();
                    loadWords();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void confirmRemoveWord(Word word) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_word_title)
                .setMessage(getString(R.string.delete_word_message, word.getEnglish()))
                .setPositiveButton(R.string.action_delete, (d, w) -> removeWord(word.getId()))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void removeWord(long wordId) {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                kotlinx.coroutines.BuildersKt.runBlocking(
                        kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> {
                            wordRepository.removeWordFromSet(setId, wordId, continuation);
                            return kotlin.Unit.INSTANCE;
                        }
                );
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                    loadWords();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadWords() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                List<WordWithProgress> list = kotlinx.coroutines.BuildersKt.runBlocking(
                        kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> wordRepository.getWordListWithProgress(setId, -1L, continuation)
                );
                List<Word> words = WordSetMapper.fromProgressList(list);

                runOnUiThread(() -> {
                    if (adapter != null) {
                        adapter.updateData(words);
                    }
                    updateMasteryUi(words);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateMasteryUi(List<Word> words) {
        int total = words.size();
        int mastered = 0;
        for (Word word : words) {
            if (word.getLevel() > 0) {
                mastered++;
            }
        }
        binding.tvMasteryProgress.setText(getString(R.string.mastery_progress, mastered, total));
        int percent = total == 0 ? 0 : (mastered * 100) / total;
        binding.progressMastery.setProgress(percent);
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
