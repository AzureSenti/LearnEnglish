package com.nhom2.learnenglish.feature.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity;
import com.nhom2.learnenglish.core.data.repository.ArticleRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.feature.articles.ArticlesActivity;
import com.nhom2.learnenglish.feature.articles.ArticleDetailActivity;
import com.nhom2.learnenglish.feature.wordsets.LibraryActivity;
import com.nhom2.learnenglish.feature.wordsets.WordSetDetailActivity;

import java.util.List;

public class MainMenuActivity extends AppCompatActivity {

    private ArticleRepository articleRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupWindowInsets();
        setupData();
        setupNavigation();
        loadFeaturedArticle();
    }

    private void setupData() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        articleRepository = new ArticleRepository(
                AppExecutors.Companion.getInstance(),
                db.articleDao()
        );
    }

    private void setupWindowInsets() {
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void setupNavigation() {
        // Nút See All trong phần Featured Articles
        TextView btnSeeAllArticles = findViewById(R.id.btn_see_all_articles);
        if (btnSeeAllArticles != null) {
            btnSeeAllArticles.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(this, ArticlesActivity.class));
        }

        // Word Set: Tech Idioms
        LinearLayout cardWordSetTech = findViewById(R.id.card_word_set_tech);
        if (cardWordSetTech != null) {
            cardWordSetTech.setOnClickListener(v -> {
                Intent intent = new Intent(this, WordSetDetailActivity.class);
                intent.putExtra("SET_TITLE", "Tech Idioms");
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        // Word Set: Daily Phrases
        LinearLayout cardWordSetDaily = findViewById(R.id.card_word_set_daily);
        if (cardWordSetDaily != null) {
            cardWordSetDaily.setOnClickListener(v -> {
                Intent intent = new Intent(this, WordSetDetailActivity.class);
                intent.putExtra("SET_TITLE", "Daily Phrases");
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        // Điều hướng Bottom Navigation - Library
        LinearLayout navLibrary = findViewById(R.id.nav_library);
        if (navLibrary != null) {
            navLibrary.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(this, LibraryActivity.class));
        }
    }

    private void loadFeaturedArticle() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                // Lấy danh sách bài báo từ Repository
                List<ArticleEntity> articles = kotlinx.coroutines.BuildersKt.runBlocking(
                        kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> articleRepository.getAll(continuation)
                );

                if (articles != null && !articles.isEmpty()) {
                    // Lấy bài báo đầu tiên làm Featured Article
                    ArticleEntity featured = articles.get(0);

                    runOnUiThread(() -> updateFeaturedUI(featured));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateFeaturedUI(ArticleEntity article) {
        TextView tvLevel = findViewById(R.id.tv_featured_level);
        TextView tvCategory = findViewById(R.id.tv_featured_category);
        TextView tvTitle = findViewById(R.id.tv_featured_title);
        TextView tvDesc = findViewById(R.id.tv_featured_desc);
        LinearLayout cardFeaturedArticle = findViewById(R.id.card_featured_article);

        if (tvLevel != null) tvLevel.setText(article.getLevel());
        if (tvCategory != null) tvCategory.setText(article.getCategory());
        if (tvTitle != null) tvTitle.setText(article.getTitle());

        // Cắt bớt content để làm description
        if (tvDesc != null) {
            String desc = article.getContent();
            if (desc != null && desc.length() > 100) {
                desc = desc.substring(0, 100) + "...";
            }
            tvDesc.setText(desc);
        }

        // Cập nhật sự kiện click với ID thực tế
        if (cardFeaturedArticle != null) {
            cardFeaturedArticle.setOnClickListener(v -> {
                Intent intent = new Intent(this, ArticleDetailActivity.class);
                intent.putExtra("article_id", article.getId());
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }
}
