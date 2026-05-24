package com.nhom2.learnenglish.feature.wordsets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity;
import com.nhom2.learnenglish.core.data.local.mockdata.MockDataImport;
import com.nhom2.learnenglish.core.data.repository.WordRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.BottomNavTab;
import com.nhom2.learnenglish.core.util.BottomNavigationHelper;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;

import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    private WordRepository wordRepository;
    private WordSetAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        setupData();
        setupBackNavigation();
        setupBottomNavigation();
        setupRecyclerView();
        setupAddButton();
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

        MockDataImport.INSTANCE.importIfNeeded(this, this::loadWordSetData);
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
        // Sử dụng helper để đồng nhất việc điều hướng
        BottomNavigationHelper.setup(this, BottomNavTab.LIBRARY);
    }

    private void setupRecyclerView() {
        RecyclerView rvWordSets = findViewById(R.id.rv_word_sets);
        if (rvWordSets != null) {
            rvWordSets.setLayoutManager(new GridLayoutManager(this, 2));
            adapter = new WordSetAdapter(new WordSetAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(WordSetEntity item) {
                    Intent intent = new Intent(LibraryActivity.this, WordSetDetailActivity.class);
                    intent.putExtra("SET_ID", item.getId());
                    intent.putExtra("SET_TITLE", item.getName());
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }

                @Override
                public void onItemLongClick(WordSetEntity item) {
                    confirmDeleteWordSet(item);
                }
            });
            rvWordSets.setAdapter(adapter);
        }
    }

    private void setupAddButton() {
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> showAddWordSetDialog());
        }
    }

    private void showAddWordSetDialog() {
        WordSetFormBottomSheetDialog dialog = WordSetFormBottomSheetDialog.newInstanceForAdd();
        dialog.setListener((name, description, iconCategory, existingId) -> saveNewWordSet(name, description, iconCategory));
        dialog.show(getSupportFragmentManager(), "WordSetFormDialog");
    }

    private void saveNewWordSet(String name, String description, String iconCategory) {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                wordRepository.createWordSet(name, description, iconCategory);
                runOnUiThread(() -> {
                    Toast.makeText(LibraryActivity.this, "Tạo bộ từ mới thành công", Toast.LENGTH_SHORT).show();
                    loadWordSetData();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(LibraryActivity.this, "Không thể tạo bộ từ mới", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void confirmDeleteWordSet(WordSetEntity item) {
        new AlertDialog.Builder(this)
            .setTitle("Xóa bộ từ")
            .setMessage("Bạn có muốn xóa bộ từ này không?")
            .setPositiveButton("Xóa", (dialog, which) -> deleteWordSet(item))
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void deleteWordSet(WordSetEntity item) {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            boolean deleted = wordRepository.deleteWordSet(item);
            runOnUiThread(() -> {
                if (deleted) {
                    Toast.makeText(LibraryActivity.this, "Xóa bộ từ thành công", Toast.LENGTH_SHORT).show();
                    loadWordSetData();
                } else {
                    Toast.makeText(LibraryActivity.this, "Không thể xóa bộ từ", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadWordSetData() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                List<WordSetEntity> list = wordRepository.getAllSets();

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
        Navigator.navigateTo(this, MainMenuActivity.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }
}
