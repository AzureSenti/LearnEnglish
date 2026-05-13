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
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.feature.articles.ArticlesActivity;
import com.nhom2.learnenglish.feature.articles.ArticleDetailActivity;
import com.nhom2.learnenglish.ui.activity.LibraryActivity;
import com.nhom2.learnenglish.ui.activity.WordSetDetailActivity;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupWindowInsets();
        setupNavigation();
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

        // Card bài báo nổi bật ở trang chủ
        LinearLayout cardFeaturedArticle = findViewById(R.id.card_featured_article);
        if (cardFeaturedArticle != null) {
            cardFeaturedArticle.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(this, ArticleDetailActivity.class));
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

    @Override
    protected void onPause() {
        super.onPause();
        // Xóa bỏ animation khi Activity kết thúc để tránh bị nháy màn hình
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }
}
