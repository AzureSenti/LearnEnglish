package com.nhom2.learnenglish.feature.articles;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity;
import com.nhom2.learnenglish.core.data.local.mockdata.MockDataImport;
import com.nhom2.learnenglish.core.data.repository.ArticleRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.Navigator;

import java.util.List;

public class ArticlesActivity extends AppCompatActivity {

    private ArticleRepository articleRepository;
    private ArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_articles);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupData();
        setupToolbar();
        setupRecyclerView();

//        loadArticleData();
    }

//    private void setupData() {
//        AppDatabase db = AppDatabase.Companion.getInstance(this);
//        articleRepository = new ArticleRepository(
//                AppExecutors.Companion.getInstance(),
//                db.articleDao()
//        );
//        MockDataImport.INSTANCE.importIfNeeded(this);
//    }
private void setupData() {
    AppDatabase db = AppDatabase.Companion.getInstance(this);
    articleRepository = new ArticleRepository(
            AppExecutors.Companion.getInstance(),
            db.articleDao()
    );

    // Gọi import và chờ nó xong mới load dữ liệu
    // Di chuyển vào đây
    MockDataImport.INSTANCE.importIfNeeded(this, this::loadArticleData);
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

            // Trong setupRecyclerView()
            adapter = new ArticleAdapter(article ->
            {
                Bundle bundle = new Bundle();
                bundle.putLong("article_id", article.getId());
                Navigator.navigateTo(this, ArticleDetailActivity.class, bundle);
                //android.content.Intent intent = new android.content.Intent(this, ArticleDetailActivity.class);
                //intent.putExtra("article_id", article.getId());
                //startActivity(intent);
                //overridePendingTransition(0, 0);
            });
            rvArticles.setAdapter(adapter);
        }
    }

    private void loadArticleData() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                List<ArticleEntity> list = articleRepository.getAll();
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