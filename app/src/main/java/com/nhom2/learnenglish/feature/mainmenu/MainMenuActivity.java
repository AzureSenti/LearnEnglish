package com.nhom2.learnenglish.feature.mainmenu;

import android.annotation.SuppressLint;
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
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity;
import com.nhom2.learnenglish.core.data.local.mockdata.MockDataImport;
import com.nhom2.learnenglish.core.data.repository.ArticleRepository;
import com.nhom2.learnenglish.core.data.repository.WordRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.BottomNavTab;
import com.nhom2.learnenglish.core.util.BottomNavigationHelper;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.feature.articles.ArticlesActivity;
import com.nhom2.learnenglish.feature.articles.ArticleDetailActivity;
import com.nhom2.learnenglish.feature.grammar.GrammarRoadmapActivity;
import com.nhom2.learnenglish.feature.wordsets.LibraryActivity;
import com.nhom2.learnenglish.feature.wordsets.WordSetDetailActivity;

import java.util.List;

public class MainMenuActivity extends AppCompatActivity {

    private ArticleRepository articleRepository;
    private WordRepository wordRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupWindowInsets();
        setupData();
        setupNavigation();
        setupBottomNavigation();
        loadFeaturedArticle();
        MockDataImport.INSTANCE.importIfNeeded(this, () -> {
            loadFeaturedArticle();
            loadRecentWordSets();
        });
    }

    private void setupData() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        articleRepository = new ArticleRepository(
                AppExecutors.Companion.getInstance(),
                db.articleDao()
        );
        wordRepository = new WordRepository(
                db.wordDao(),
                db.wordSetDao(),
                db.wordSrsDao(),
                db.userWordSetDao(),
                AppExecutors.Companion.getInstance()
        );
    }

    private void setupBottomNavigation() {
        // Kích hoạt click cho các tab ở Bottom Navigation
        BottomNavigationHelper.setup(this, BottomNavTab.EXPLORE);
    }

    @SuppressLint("SetTextI18n")
    private void updateWordSetUI(WordSetEntity set, int index) {
        int cardId = (index == 1) ? R.id.card_word_set_1 : R.id.card_word_set_2;
        int titleId = (index == 1) ? R.id.tv_word_set_title_1 : R.id.tv_word_set_title_2;
        int countId = (index == 1) ? R.id.tv_word_count_1 : R.id.tv_word_count_2;

        LinearLayout card = findViewById(cardId);
        TextView tvTitle = findViewById(titleId);
        TextView tvCount = findViewById(countId);

        if (tvTitle != null) tvTitle.setText(set.getName());
        if (tvCount != null) tvCount.setText("5 words");

        if (card != null) {
            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, WordSetDetailActivity.class);
                intent.putExtra("SET_ID", set.getId());
                intent.putExtra("SET_TITLE", set.getName());
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }
    }

    private void loadRecentWordSets() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                List<WordSetEntity> list = wordRepository.getAllSets();
                runOnUiThread(() -> {
                    if (list != null && list.size() >= 2) {
                        updateWordSetUI(list.get(0), 1);
                        updateWordSetUI(list.get(1), 2);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
        TextView btnSeeAllArticles = findViewById(R.id.btn_see_all_articles);
        if (btnSeeAllArticles != null) {
            btnSeeAllArticles.setOnClickListener(v -> Navigator.navigateTo(this,ArticlesActivity.class));
        }

        TextView btnViewAllWordSets = findViewById(R.id.btn_view_all_word_sets);
        if (btnViewAllWordSets != null) {
            btnViewAllWordSets.setOnClickListener(v -> Navigator.navigateTo(this, LibraryActivity.class));
        }

        LinearLayout cardGrammar = findViewById(R.id.card_grammar);
        if (cardGrammar != null) {
            cardGrammar.setOnClickListener(v -> Navigator.navigateTo(this, GrammarRoadmapActivity.class));
        }
    }

    private void loadFeaturedArticle() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                List<ArticleEntity> articles = articleRepository.getAll();
                if (articles != null && !articles.isEmpty()) {
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

        if (tvDesc != null) {
            String desc = article.getContent();
            if (desc != null && desc.length() > 100) {
                desc = desc.substring(0, 100) + "...";
            }
            tvDesc.setText(desc);
        }

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
