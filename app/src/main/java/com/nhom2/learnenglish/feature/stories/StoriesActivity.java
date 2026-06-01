package com.nhom2.learnenglish.feature.stories;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.StoryEntity;
import com.nhom2.learnenglish.core.data.repository.StoryRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.feature.articles.ArticleDetailActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StoriesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StoryAdapter adapter;
    private ChipGroup chipGroup;
    private EditText etSearch;
    private ImageView ivClearSearch, ivBack;

    private StoryRepository storyRepository;
    private final List<StoryEntity> masterList = new ArrayList<>();
    private String currentCategory = "All Stories";
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindowInsets();
        setContentView(R.layout.activity_stories_library);

        // Khởi tạo Repository đúng thực thể Story
        storyRepository = new StoryRepository(
                AppExecutors.Companion.getInstance(),
                AppDatabase.Companion.getInstance(this).storyDao()
        );

        initViews();
        setupRecyclerView();
        setupSearchLogic();

        ivBack.setOnClickListener(v -> finish());
        loadData();
    }

    // FIX TRIỆT ĐỂ WINDOW INSETS: Lấy chuẩn padding hệ thống và gán an toàn vào view root
    private void setupWindowInsets() {
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_stories);
        chipGroup = findViewById(R.id.cg_categories);
        etSearch = findViewById(R.id.et_search);
        ivClearSearch = findViewById(R.id.iv_clear_search);
        ivBack = findViewById(R.id.iv_back);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new StoryAdapter(story -> {
            // SỬA LẠI THÀNH STORY DETAIL
            Intent intent = new Intent(this, StoryDetailActivity.class);
            intent.putExtra("story_id", story.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadData() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                // 1. Kiểm tra xem dưới DB đã có truyện chưa
                List<StoryEntity> list = storyRepository.getAll();

                if (list == null || list.isEmpty()) {
                    // 2. Nếu CHƯA CÓ, lôi toàn bộ dữ liệu từ file MockData.kt sang để nạp vào DB
                    List<StoryEntity> mockStories = com.nhom2.learnenglish.core.data.local.mockdata.MockData.INSTANCE.getStories();

                    if (mockStories != null && !mockStories.isEmpty()) {
                        for (StoryEntity story : mockStories) {
                            // Insert từng truyện vào Room DB
                            AppDatabase.Companion.getInstance(this).storyDao().insert(story);
                        }
                        // Lấy lại danh sách sau khi nạp thành công
                        list = storyRepository.getAll();
                    }
                }

                masterList.clear();
                if (list != null) {
                    masterList.addAll(list);
                }


                // Gom tất cả các thể loại truyện duy nhất để làm bộ lọc Chip
                Set<String> categories = new HashSet<>();
                for (StoryEntity item : masterList) {
                    if (item.getCategory() != null && !item.getCategory().isEmpty()) {
                        categories.add(item.getCategory());
                    }
                }

                // Chuyển Set sang List và xóa "All Stories" nếu lỡ có trong DB để tránh trùng lặp
                List<String> finalCategories = new ArrayList<>(categories);
                finalCategories.remove("All Stories");

                // (Tùy chọn) Sắp xếp các danh mục còn lại theo thứ tự chữ cái A-Z cho đẹp mắt
                java.util.Collections.sort(finalCategories);

                finalCategories.add(0, "All Stories");

                runOnUiThread(() -> {
                    setupChips(finalCategories);
                    filterData();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // HÀM ĐỔ DỮ LIỆU MẪU (MOCK DATA) CHO TRUYỆN TRỰC TIẾP VÀO DATABASE
    private void insertMockStoriesIfNeeded() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        db.storyDao().insert(new StoryEntity(0, "The Tortoise and the Hare", "A classic story about how slow and steady wins the race. The proud hare falls asleep while the tortoise keeps moving forward patiently.", "Fables", "A1", "", "Aesop", false));
        db.storyDao().insert(new StoryEntity(0, "The Secret Garden", "Mary Lennox is a lonely girl sent to live at her uncle's estate. She discovers a hidden, locked garden and breathes new life into it.", "Fiction", "B1", "", "Frances Hodgson", false));
        db.storyDao().insert(new StoryEntity(0, "The Little Prince", "A pilot crashes in the Sahara Desert and meets a young prince from an asteroid who tells him poetic stories of his travels.", "Fantasy", "A2", "", "Antoine de Saint", false));
        db.storyDao().insert(new StoryEntity(0, "A Christmas Carol", "Ebenezer Scrooge, a cold-hearted miser, is visited by ghosts of Christmas Past, Present, and Yet to Come to transform his life.", "Classic", "B2", "", "Charles Dickens", false));
    }

    private void setupChips(List<String> categories) {
        chipGroup.removeAllViews();
        for (String cat : categories) {
            addChip(cat);
        }
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = findViewById(checkedId);
            if (chip != null) {
                currentCategory = chip.getText().toString();
                filterData();
            }
        });
    }

    private void addChip(String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCheckable(true);
        if (text.equals(currentCategory)) chip.setChecked(true);
        chip.setId(View.generateViewId());
        chipGroup.addView(chip);
    }

    private void setupSearchLogic() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().toLowerCase().trim();
                ivClearSearch.setVisibility(currentSearchQuery.isEmpty() ? View.GONE : View.VISIBLE);
                filterData();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ivClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            hideKeyboard();
        });
    }

    private void filterData() {
        List<StoryEntity> filtered = new ArrayList<>();
        for (StoryEntity item : masterList) {
            boolean matchesSearch = item.getTitle().toLowerCase().contains(currentSearchQuery);
            boolean matchesCategory = currentCategory.equals("All Stories") ||
                    (item.getCategory() != null && item.getCategory().equals(currentCategory));

            if (matchesSearch && matchesCategory) {
                filtered.add(item);
            }
        }
        adapter.updateData(filtered);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}