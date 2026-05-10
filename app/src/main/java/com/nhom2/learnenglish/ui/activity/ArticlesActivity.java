package com.nhom2.learnenglish.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.model.Article;
import com.nhom2.learnenglish.ui.adapter.ArticleAdapter;

import java.util.ArrayList;
import java.util.List;

public class ArticlesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }
    }

    private void setupRecyclerView() {
        RecyclerView rvArticles = findViewById(R.id.rv_articles);
        if (rvArticles != null) {
            rvArticles.setLayoutManager(new LinearLayoutManager(this));
            
            List<Article> dummyData = getDummyArticles();
            ArticleAdapter adapter = new ArticleAdapter(dummyData, article -> navigateTo(ArticleDetailActivity.class));
            rvArticles.setAdapter(adapter);
        }
    }

    private List<Article> getDummyArticles() {
        List<Article> list = new ArrayList<>();
        list.add(new Article("The Future of Remote Work: A Global Perspective", 
                "Explore how companies worldwide are adapting to hybrid models and what it means...", 
                "B2 Intermediate", "Business", "5 min read", true));
        list.add(new Article("Understanding Idioms in Everyday Conversation", 
                "A deep dive into common English idioms, their historical origins, and how to use...", 
                "C1 Advanced", "Culture", "8 min read", false));
        list.add(new Article("Basic Tech Vocabulary for the Modern Workplace", 
                "Learn the essential vocabulary needed to navigate software interfaces, draft email...", 
                "B1 Pre-Intermediate", "Technology", "4 min read", false));
        return list;
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}