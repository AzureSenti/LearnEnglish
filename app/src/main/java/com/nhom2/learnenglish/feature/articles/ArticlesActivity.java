package com.nhom2.learnenglish.feature.articles;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity;
import com.nhom2.learnenglish.core.data.local.mockdata.MockDataImport;
import com.nhom2.learnenglish.core.data.repository.ArticleRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArticlesActivity extends AppCompatActivity {

    private ArticleRepository articleRepository;
    private ArticleAdapter adapter;
    private ChipGroup chipGroup;
    
    private final List<ArticleEntity> masterArticleList = new ArrayList<>();
    private String currentSearchQuery = "";
    private String currentCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_articles);
        
        chipGroup = findViewById(R.id.chip_group_categories);
        
        setupWindowInsets();
        setupData();
        setupToolbar();
        setupRecyclerView();
        setupSearch();
    }

    private void setupData() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        articleRepository = new ArticleRepository(
                AppExecutors.Companion.getInstance(),
                db.articleDao()
        );

        MockDataImport.INSTANCE.importIfNeeded(this, this::loadArticleData);
    }

    private void setupWindowInsets() {
        View mainView = findViewById(android.R.id.content);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void setupToolbar() {
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }
    }

    private void setupRecyclerView() {
        RecyclerView rvArticles = findViewById(R.id.rv_articles);
        if (rvArticles != null) {
            rvArticles.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ArticleAdapter(article -> {
                android.content.Intent intent = new android.content.Intent(this, ArticleDetailActivity.class);
                intent.putExtra("article_id", article.getId());
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
            rvArticles.setAdapter(adapter);

            // Tự động ẩn bàn phím khi người dùng cuộn danh sách
            rvArticles.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        hideKeyboard();
                    }
                }
            });
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            view.clearFocus();
        }
    }

    private void setupSearch() {
        EditText etSearch = findViewById(R.id.et_search);
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

    private void createDynamicChips(List<ArticleEntity> articles) {
        if (chipGroup == null) return;
        
        chipGroup.removeAllViews();

        // 1. Tạo Chip "All" mặc định
        Chip allChip = new Chip(this);
        allChip.setText("All");
        allChip.setCheckable(true);
        allChip.setChecked(true);
        allChip.setId(View.generateViewId());
        chipGroup.addView(allChip);

        // 2. Lấy danh sách thể loại duy nhất từ dữ liệu
        Set<String> categories = new HashSet<>();
        for (ArticleEntity article : articles) {
            if (article.getCategory() != null && !article.getCategory().isEmpty()) {
                categories.add(article.getCategory());
            }
        }

        // 3. Tạo Chip cho từng thể loại
        for (String category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category);
            chip.setCheckable(true);
            chip.setId(View.generateViewId());
            chipGroup.addView(chip);
        }

        // 4. Lắng nghe sự kiện chọn Chip
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip selectedChip = findViewById(checkedId);
            if (selectedChip != null) {
                currentCategory = selectedChip.getText().toString();
                filterData();
            }
        });
    }

    private void filterData() {
        List<ArticleEntity> filteredList = new ArrayList<>();
        
        for (ArticleEntity article : masterArticleList) {
            boolean matchesSearch = article.getTitle().toLowerCase().contains(currentSearchQuery);
            boolean matchesCategory = currentCategory.equals("All") || 
                                     (article.getCategory() != null && article.getCategory().equalsIgnoreCase(currentCategory));
            
            if (matchesSearch && matchesCategory) {
                filteredList.add(article);
            }
        }
        
        if (adapter != null) {
            adapter.updateData(filteredList);
        }
    }

    private void loadArticleData() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                List<ArticleEntity> list = articleRepository.getAll();
                masterArticleList.clear();
                masterArticleList.addAll(list);
                
                runOnUiThread(() -> {
                    createDynamicChips(masterArticleList);
                    if (adapter != null) {
                        adapter.updateData(new ArrayList<>(masterArticleList));
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
