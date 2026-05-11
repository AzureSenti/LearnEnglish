package com.nhom2.learnenglish.ui.activity;

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

public class MainActivity extends AppCompatActivity {

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
            btnSeeAllArticles.setOnClickListener(v -> navigateTo(ArticlesActivity.class));
        }

        // Card bài báo nổi bật ở trang chủ
        LinearLayout cardFeaturedArticle = findViewById(R.id.card_featured_article);
        if (cardFeaturedArticle != null) {
            cardFeaturedArticle.setOnClickListener(v -> navigateTo(ArticleDetailActivity.class));
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
            navLibrary.setOnClickListener(v -> navigateTo(LibraryActivity.class));
        }
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(MainActivity.this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }
}