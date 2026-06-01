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
import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity;
import com.nhom2.learnenglish.core.data.repository.ArticleRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.feature.articles.ArticleDetailActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StoriesActivity extends AppCompatActivity {

    private ArticleRepository articleRepository;
    private StoryAdapter adapter;
    private ChipGroup chipGroup;
    private EditText etSearch;
    
    private final List<ArticleEntity> masterList = new ArrayList<>();
    private String currentSearchQuery = "";
    private String currentCategory = "All Stories";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stories_library);

        initViews();
        setupWindowInsets();
        setupToolbar();
        setupRecyclerView();
        setupSearch();
        loadData();
    }

    private void initViews() {
        chipGroup = findViewById(R.id.cg_categories);
        etSearch = findViewById(R.id.et_search);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ll_header).getParent(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupToolbar() {
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }
    }

    private void setupRecyclerView() {
        RecyclerView rvStories = findViewById(R.id.rv_stories);
        adapter = new StoryAdapter(story -> {
            Intent intent = new Intent(this, ArticleDetailActivity.class);
            intent.putExtra("article_id", story.getId());
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
        
        rvStories.setLayoutManager(new GridLayoutManager(this, 2));
        rvStories.setAdapter(adapter);
    }

    private void setupSearch() {
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentSearchQuery = s.toString().toLowerCase().trim();
                    filterData();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void loadData() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        articleRepository = new ArticleRepository(
                AppExecutors.Companion.getInstance(),
                db.articleDao()
        );

        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            List<ArticleEntity> list = articleRepository.getAll();
            runOnUiThread(() -> {
                masterList.clear();
                masterList.addAll(list);
                setupCategories(list);
                filterData();
            });
        });
    }

    private void setupCategories(List<ArticleEntity> list) {
        if (chipGroup == null) return;
        chipGroup.removeAllViews();

        addChip("All Stories");
        
        Set<String> categories = new HashSet<>();
        for (ArticleEntity item : list) {
            if (item.getCategory() != null) categories.add(item.getCategory());
        }

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

    private void filterData() {
        List<ArticleEntity> filtered = new ArrayList<>();
        for (ArticleEntity item : masterList) {
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
