package com.nhom2.learnenglish.feature.articles;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity;
import com.nhom2.learnenglish.core.data.repository.ArticleRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;

public class ArticleDetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvContent, tvAuthor, tvInfo, tvToolbarTitle;
    private ArticleRepository articleRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        tvTitle = findViewById(R.id.tv_detail_title);
        tvContent = findViewById(R.id.tv_detail_content);
        tvAuthor = findViewById(R.id.tv_detail_author);
        tvInfo = findViewById(R.id.tv_detail_info);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);

        // Khởi tạo repository
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        articleRepository = new ArticleRepository(AppExecutors.Companion.getInstance(), db.articleDao());

        long articleId = getIntent().getLongExtra("article_id", -1);
        if (articleId != -1) {
            loadArticleDetail(articleId);
        }

        setupToolbar();
        setupBackNavigation();
    }

    private void loadArticleDetail(long id) {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                ArticleEntity article = kotlinx.coroutines.BuildersKt.runBlocking(
                        kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> articleRepository.getById(id, continuation)
                );

                if (article != null) {
                    runOnUiThread(() -> {
                        tvTitle.setText(article.getTitle());
                        tvContent.setText(article.getContent());
                        if (tvAuthor != null && article.getAuthor() != null) {
                            tvAuthor.setText(article.getAuthor());
                        }
                        if (tvInfo != null) {
                            String info = article.getReadTime() + " • " + article.getCategory();
                            tvInfo.setText(info);
                        }
                        if (tvToolbarTitle != null) {
                            tvToolbarTitle.setText(article.getTitle());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setupToolbar() {
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(0, 0);
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