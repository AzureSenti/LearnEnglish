package com.nhom2.learnenglish.feature.wordsets;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.dao.word.WordSetDao;
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity;
import com.nhom2.learnenglish.core.data.local.mockdata.MockDataImport;
import com.nhom2.learnenglish.core.data.repository.WordRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;
import com.nhom2.learnenglish.feature.grammar.GrammarRoadmapActivity; // Bổ sung import
import com.nhom2.learnenglish.feature.profile.ProfileActivity; // Bổ sung import

import java.util.List;

import es.dmoral.toasty.Toasty;

public class LibraryActivity extends AppCompatActivity {

    private WordRepository wordRepository;
    private WordSetDao wordSetDao;
    private WordSetAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        setupData();
        setupBackNavigation();
        setupRecyclerView();
        setupFab();
        setupBottomNav(); // BỔ SUNG: Gọi hàm thiết lập điều hướng
    }

    private void setupData() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        wordSetDao = db.wordSetDao();
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

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(LibraryActivity.this, MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private void setupFab() {
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> showWordSetForm(null));
        }
    }

    private void setupRecyclerView() {
        RecyclerView rvWordSets = findViewById(R.id.rv_word_sets);
        if (rvWordSets == null) return;

        rvWordSets.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new WordSetAdapter(new WordSetAdapter.OnItemActionListener() {
            @Override
            public void onItemClick(WordSetEntity item) {
                Intent intent = new Intent(LibraryActivity.this, WordSetDetailActivity.class);
                intent.putExtra("SET_ID", item.getId());
                intent.putExtra("SET_TITLE", item.getName());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }

            @Override
            public void onMoreClick(WordSetEntity item) {
                showWordSetActions(item);
            }
        });
        rvWordSets.setAdapter(adapter);
    }

    private void showWordSetActions(WordSetEntity item) {
        new AlertDialog.Builder(this)
                .setTitle(item.getName())
                .setItems(new CharSequence[]{"Sửa bộ từ", "Xóa bộ từ"}, (dialog, which) -> {
                    if (which == 0) showWordSetForm(item);
                    else confirmDeleteWordSet(item);
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void confirmDeleteWordSet(WordSetEntity item) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa bộ từ")
                .setMessage("Bạn có chắc muốn xóa \"" + item.getName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) ->
                        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
                            try {
                                wordSetDao.delete(item);
                                runOnUiThread(() -> {
                                    Toasty.success(this, "Đã xóa bộ từ", Toast.LENGTH_SHORT, true).show();
                                    loadWordSetData();
                                });
                            } catch (Exception e) { e.printStackTrace(); }
                        }))
                .setNegativeButton("Hủy", null).show();
    }

    private void showWordSetForm(WordSetEntity item) {
        final boolean isEdit = item != null;
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        View content = getLayoutInflater().inflate(R.layout.bottom_sheet_word_set_form, null);

        EditText etName = content.findViewById(R.id.et_set_name);
        MaterialButton btnSave = content.findViewById(R.id.btn_save);

        // Emerald color cho nút Save
        btnSave.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#10B981")));

        String defaultIcon = isEdit && item.getDescription() != null ? item.getDescription() : "briefcase";
        if ("folder".equals(defaultIcon)) defaultIcon = "briefcase";
        final String[] selectedIcon = {defaultIcon};
        setupCategoryIcons(content, selectedIcon);

        if (isEdit) {
            ((TextView)content.findViewById(R.id.tv_sheet_title)).setText("Sửa Bộ Từ Vựng");
            etName.setText(item.getName());
            btnSave.setText("Cập Nhật");
        }

        content.findViewById(R.id.iv_close).setOnClickListener(v -> sheet.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                etName.setError("Nhập tên bộ từ");
                return;
            }

            AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
                try {
                    if (isEdit) {
                        wordSetDao.update(new WordSetEntity(item.getId(), name, selectedIcon[0], item.getUnlockCost(),item.isSynced()));
                    } else {
                        wordSetDao.insert(new WordSetEntity(0L, name, selectedIcon[0], 0,item.isSynced()));
                    }
                    runOnUiThread(() -> {
                        Toasty.success(this, isEdit ? "Đã cập nhật" : "Đã tạo bộ từ mới", Toast.LENGTH_SHORT, true).show();                        sheet.dismiss();
                        loadWordSetData();
                    });
                } catch (Exception e) { e.printStackTrace(); }
            });
        });

        content.setTranslationY(100f);
        content.animate().translationY(0).setDuration(400).start();

        sheet.setContentView(content);
        sheet.show();
    }

    private void setupCategoryIcons(View view, String[] selectedIcon) {
        int[] ids = {R.id.cat_briefcase, R.id.cat_brain, R.id.cat_restaurant, R.id.cat_airplane, R.id.cat_more};
        String[] tags = {"briefcase", "brain", "restaurant", "airplane", "more"};

        for (int i = 0; i < ids.length; i++) {
            MaterialButton btn = view.findViewById(ids[i]);
            if (btn == null) continue;
            final String tag = tags[i];
            btn.setTag(tag);

            Runnable updateStyle = () -> {
                boolean active = tag.equals(selectedIcon[0]);
                // ĐÃ FIX LỖI: R.color.blue_primary
                btn.setStrokeColorResource(active ? R.color.blue_primary : R.color.border_light);
                btn.setStrokeWidth(active ? 4 : 1);
            };
            updateStyle.run();

            btn.setOnClickListener(v -> {
                selectedIcon[0] = tag;
                v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction(() ->
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                ).start();

                for (int id : ids) {
                    MaterialButton b = view.findViewById(id);
                    if (b != null) {
                        boolean active = b.getTag().equals(selectedIcon[0]);
                        // ĐÃ FIX LỖI: R.color.blue_primary
                        b.setStrokeColorResource(active ? R.color.blue_primary : R.color.border_light);
                        b.setStrokeWidth(active ? 4 : 1);
                    }
                }
            });
        }
    }

    private void loadWordSetData() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                List<WordSetEntity> list = wordSetDao.getAllSets();
                runOnUiThread(() -> {
                    if (adapter != null) adapter.updateData(list);
                });
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    // BỔ SUNG: Hàm điều hướng chuẩn theo cấu trúc của dự án
    private void setupBottomNav() {
        LinearLayout navExplore = findViewById(R.id.nav_explore);
        LinearLayout navLibrary = findViewById(R.id.nav_library);
        LinearLayout navLearn = findViewById(R.id.nav_learn);
        LinearLayout navProfile = findViewById(R.id.nav_profile);

        if (navExplore != null) {
            navExplore.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (navLibrary != null) {
            navLibrary.setOnClickListener(null); // Đang ở Library nên khóa click
        }

        if (navLearn != null) {
            navLearn.setOnClickListener(v -> {
                Intent intent = new Intent(this, GrammarRoadmapActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0); // Tắt animation khi đóng activity
        }
    }
}