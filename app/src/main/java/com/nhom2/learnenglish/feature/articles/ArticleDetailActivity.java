package com.nhom2.learnenglish.feature.articles;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity;
import com.nhom2.learnenglish.core.data.local.entity.WordEntity;
import com.nhom2.learnenglish.core.data.repository.ArticleRepository;
import com.nhom2.learnenglish.core.data.repository.WordRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.ui.bottomsheet.WordLookupBottomSheet;

import java.util.Map;

public class ArticleDetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvContent, tvAuthor, tvInfo, tvToolbarTitle;
    private ArticleRepository articleRepository;
    private WordRepository wordRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        tvTitle = findViewById(R.id.tv_detail_title);
        tvContent = findViewById(R.id.tv_detail_content);
        tvAuthor = findViewById(R.id.tv_detail_author);
        tvInfo = findViewById(R.id.tv_detail_info);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);

        AppDatabase db = AppDatabase.Companion.getInstance(this);
        articleRepository = new ArticleRepository(AppExecutors.Companion.getInstance(), db.articleDao());
        wordRepository = new WordRepository(
                db.wordDao(),
                db.wordSetDao(),
                db.wordSrsDao(),
                db.userWordSetDao(),
                db.wordSetCrossDao(),
                AppExecutors.Companion.getInstance()
        );

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

                Map<String, WordEntity> vocabularyMap = kotlinx.coroutines.BuildersKt.runBlocking(
                        kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> wordRepository.buildVocabularyLookupMap(continuation)
                );

                if (article != null) {
                    runOnUiThread(() -> {
                        tvTitle.setText(article.getTitle());
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
                        bindClickableContent(article.getContent(), vocabularyMap);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void bindClickableContent(String content, Map<String, WordEntity> vocabularyMap) {
        ArticleTextSpanHelper.INSTANCE.applyToTextView(
                tvContent,
                content,
                vocabularyMap,
                true,
                (word, entity) -> {
                    showWordLookup(word, entity);
                    return kotlin.Unit.INSTANCE;
                }
        );
    }

    private void showWordLookup(String word, WordEntity entity) {
        WordLookupBottomSheet sheet = WordLookupBottomSheet.newInstance(word, entity);
        sheet.show(getSupportFragmentManager(), "word_lookup");
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
