package com.nhom2.learnenglish.feature.wordsets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.dao.word.WordSetDao;
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity;
import com.nhom2.learnenglish.core.data.local.mockdata.MockDataImport;
import com.nhom2.learnenglish.core.data.repository.WordRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.feature.grammar.GrammarRoadmapActivity;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;
import com.nhom2.learnenglish.ui.activity.ProfileActivity;

import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    private WordRepository wordRepository;
    private WordSetDao wordSetDao;
    private WordSetAdapter adapter;
    private String selectedCategory = "folder"; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        setupData();
        setupBackNavigation();
        setupBottomNavigation();
        setupRecyclerView();
        setupFAB();
    }

    private void setupData() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        wordSetDao = db.wordSetDao();
        wordRepository = new WordRepository(
                db.wordDao(),
                wordSetDao,
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
                Navigator.navigateTo(LibraryActivity.this, MainMenuActivity.class);
            }
        });
    }

    private void setupBottomNavigation() {
        LinearLayout navExplore = findViewById(R.id.nav_explore);
        if (navExplore != null) {
            navExplore.setOnClickListener(v -> Navigator.navigateTo(this, MainMenuActivity.class));
        }

        LinearLayout navLearn = findViewById(R.id.nav_learn);
        if (navLearn != null) {
            navLearn.setOnClickListener(v -> Navigator.navigateTo(this, GrammarRoadmapActivity.class));
        }

        LinearLayout navProfile = findViewById(R.id.nav_profile);
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> Navigator.navigateTo(this, ProfileActivity.class));
        }
    }

    private void setupFAB() {
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> showWordSetForm(null));
        }
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
                public void onEditClick(WordSetEntity item) {
                    showWordSetForm(item);
                }

                @Override
                public void onDeleteClick(WordSetEntity item) {
                    showDeleteConfirmDialog(item);
                }
            });

            rvWordSets.setAdapter(adapter);
        }
    }

    private void showWordSetForm(WordSetEntity item) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_word_set_form, null);
        
        TextView tvTitle = view.findViewById(R.id.tv_sheet_title);
        EditText etName = view.findViewById(R.id.et_set_name);
        View btnSave = view.findViewById(R.id.btn_save);

        setupCategorySelection(view);

        if (item != null) {
            tvTitle.setText(R.string.word_set_edit_title);
            etName.setText(item.getName());
            selectedCategory = item.getIconCategory();
        } else {
            tvTitle.setText(R.string.word_set_add_title);
            selectedCategory = "folder";
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, R.string.error_name_required, Toast.LENGTH_SHORT).show();
                return;
            }

            AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
                try {
                    if (item == null) {
                        WordSetEntity newSet = new WordSetEntity(0, name, "New Set", 0, selectedCategory);
                        wordSetDao.insert(newSet);
                    } else {
                        WordSetEntity updatedSet = new WordSetEntity(item.getId(), name, item.getDescription(), item.getUnlockCost(), selectedCategory);
                        wordSetDao.update(updatedSet);
                    }

                    runOnUiThread(() -> {
                        Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        loadWordSetData(); 
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void setupCategorySelection(View view) {
        if (view.findViewById(R.id.cat_travel) != null)
            view.findViewById(R.id.cat_travel).setOnClickListener(v -> selectedCategory = "travel");
        if (view.findViewById(R.id.cat_food) != null)
            view.findViewById(R.id.cat_food).setOnClickListener(v -> selectedCategory = "food");
        if (view.findViewById(R.id.cat_study) != null)
            view.findViewById(R.id.cat_study).setOnClickListener(v -> selectedCategory = "study");
        if (view.findViewById(R.id.cat_business) != null)
            view.findViewById(R.id.cat_business).setOnClickListener(v -> selectedCategory = "business");
        if (view.findViewById(R.id.cat_tech) != null)
            view.findViewById(R.id.cat_tech).setOnClickListener(v -> selectedCategory = "tech");
    }

    private void showDeleteConfirmDialog(WordSetEntity item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_word_set_title)
                .setMessage(getString(R.string.delete_word_set_message, item.getName()))
                .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                    AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
                        try {
                            wordSetDao.delete(item);
                            runOnUiThread(() -> {
                                Toast.makeText(this, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                                loadWordSetData();
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                })
                .setNegativeButton(R.string.action_close, null)
                .show();
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

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }
}
