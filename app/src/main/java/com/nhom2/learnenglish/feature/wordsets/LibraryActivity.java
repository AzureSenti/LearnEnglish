package com.nhom2.learnenglish.feature.wordsets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.WordSetEntity;
import com.nhom2.learnenglish.core.data.local.mockdata.MockDataImport;
import com.nhom2.learnenglish.core.data.repository.WordRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.BottomNavTab;
import com.nhom2.learnenglish.core.util.BottomNavigationHelper;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.databinding.ActivityLibraryBinding;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;
import com.nhom2.learnenglish.model.WordSet;
import com.nhom2.learnenglish.model.WordSetMapper;
import com.nhom2.learnenglish.ui.bottomsheet.WordSetFormBottomSheetDialog;

import java.util.List;
import java.util.Map;

public class LibraryActivity extends AppCompatActivity {

    private ActivityLibraryBinding binding;
    private WordRepository wordRepository;
    private WordSetAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLibraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupData();
        setupBackNavigation();
        setupBottomNavigation();
        setupRecyclerView();
        setupFab();
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

        MockDataImport.INSTANCE.importIfNeeded(this, this::loadWordSetData);
    }

    private void setupFab() {
        binding.fabAdd.setOnClickListener(v -> showWordSetForm(null));
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveToLearnHome();
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationHelper.setup(this, BottomNavTab.LIBRARY);
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.rvWordSets.setLayoutManager(layoutManager);
        binding.rvWordSets.setItemAnimator(new DefaultItemAnimator());

        adapter = new WordSetAdapter(
                item -> {
                    Intent intent = new Intent(this, WordSetDetailActivity.class);
                    intent.putExtra(WordSetDetailActivity.EXTRA_SET_ID, item.getId());
                    intent.putExtra(WordSetDetailActivity.EXTRA_SET_TITLE, item.getTitle());
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                },
                this::showWordSetMenu
        );
        binding.rvWordSets.setAdapter(adapter);
    }

    private void showWordSetMenu(WordSet item, View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_word_set, popup.getMenu());
        popup.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.action_edit) {
                showWordSetForm(item);
                return true;
            }
            if (id == R.id.action_delete) {
                confirmDeleteWordSet(item);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showWordSetForm(WordSet existing) {
        WordSetFormBottomSheetDialog sheet = existing == null
                ? WordSetFormBottomSheetDialog.newInstanceForAdd()
                : WordSetFormBottomSheetDialog.newInstanceForEdit(
                        existing.getId(),
                        existing.getTitle(),
                        existing.getDescription(),
                        existing.getCategoryIcon()
                );
        sheet.setListener((name, description, iconCategory, existingId) ->
                saveWordSet(name, iconCategory, existingId));
        sheet.show(getSupportFragmentManager(), "word_set_form");
    }

    private void saveWordSet(String name, String iconCategory, Long existingId) {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                if (existingId == null) {
                    kotlinx.coroutines.BuildersKt.runBlocking(
                            kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                            (scope, continuation) -> wordRepository.createWordSet(
                                    name, null, iconCategory, continuation)
                    );
                } else {
                    WordSetEntity current = kotlinx.coroutines.BuildersKt.runBlocking(
                            kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                            (scope, continuation) -> wordRepository.getSetById(existingId, continuation)
                    );
                    if (current != null) {
                        WordSetEntity updated = new WordSetEntity(
                                current.getId(),
                                name,
                                current.getDescription(),
                                current.getUnlockCost(),
                                iconCategory
                        );
                        kotlinx.coroutines.BuildersKt.runBlocking(
                                kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                                (scope, continuation) -> {
                                    wordRepository.updateWordSet(updated, continuation);
                                    return kotlin.Unit.INSTANCE;
                                }
                        );
                    }
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT).show();
                    loadWordSetData();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void confirmDeleteWordSet(WordSet item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_word_set_title)
                .setMessage(getString(R.string.delete_word_set_message, item.getTitle()))
                .setPositiveButton(R.string.action_delete, (d, w) -> deleteWordSet(item.getId()))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void deleteWordSet(long setId) {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                kotlinx.coroutines.BuildersKt.runBlocking(
                        kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> {
                            wordRepository.deleteWordSet(setId, continuation);
                            return kotlin.Unit.INSTANCE;
                        }
                );
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                    loadWordSetData();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadWordSetData() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                List<WordSetEntity> entities = kotlinx.coroutines.BuildersKt.runBlocking(
                        kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> wordRepository.getAllSets(continuation)
                );
                Map<Long, Integer> counts = kotlinx.coroutines.BuildersKt.runBlocking(
                        kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> wordRepository.getWordCountsBySet(continuation)
                );
                List<WordSet> list = WordSetMapper.fromEntities(entities, counts);

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

    private void moveToLearnHome() {
        Navigator.INSTANCE.navigateTo(this, MainMenuActivity.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }
}
