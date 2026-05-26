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
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.feature.articles.ArticlesActivity;
import com.nhom2.learnenglish.feature.articles.ArticleDetailActivity;
import com.nhom2.learnenglish.feature.grammar.GrammarRoadmapActivity;
import com.nhom2.learnenglish.feature.profile.ProfileActivity;
import com.nhom2.learnenglish.feature.wordsets.LibraryActivity;
import com.nhom2.learnenglish.feature.wordsets.WordSetDetailActivity;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.nhom2.learnenglish.core.data.model.DictionaryResult;
import com.nhom2.learnenglish.core.network.RetrofitClient;
import com.nhom2.learnenglish.core.network.dictionary.DictionaryApi;
import com.nhom2.learnenglish.core.data.repository.DictionaryRepository;
import java.util.List;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.nhom2.learnenglish.core.data.local.entity.word.WordEntity;
import java.util.ArrayList;

public class MainMenuActivity extends AppCompatActivity {

    private ArticleRepository articleRepository;
    private WordRepository wordRepository;
    private DictionaryRepository dictionaryRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupWindowInsets();
        setupData();
        setupNavigation();

        setupSearchBar();
        MockDataImport.INSTANCE.importIfNeeded(this, () -> {
            loadFeaturedArticle();
            loadRecentWordSets();
        });

    }

    private void setupData() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        articleRepository = new ArticleRepository(AppExecutors.Companion.getInstance(), db.articleDao());
        wordRepository = new WordRepository(
                db.wordDao(), db.wordSetDao(), db.wordSrsDao(), db.userWordSetDao(), db.wordSetCrossDao(), AppExecutors.Companion.getInstance()
        );

        // KHỞI TẠO DICTIONARY REPOSITORY
        DictionaryApi dictionaryApi = RetrofitClient.INSTANCE.getInstance().create(DictionaryApi.class);
        dictionaryRepository = new DictionaryRepository(dictionaryApi);
    }
    private void setupSearchBar() {
        // Lưu ý đổi từ EditText thành AutoCompleteTextView
        AutoCompleteTextView etSearch = findViewById(R.id.et_search);

        if (etSearch != null) {

            // 1. Load danh sách từ vựng từ Local DB để làm dữ liệu gợi ý
            AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
                try {
                    List<WordEntity> allWords = wordRepository.getAllWords();
                    List<String> suggestionList = new ArrayList<>();

                    // Lấy ra danh sách các từ tiếng Anh
                    for (WordEntity word : allWords) {
                        suggestionList.add(word.getEnglishWord());
                    }

                    // Đưa danh sách gợi ý lên giao diện (phải chạy trên Main Thread)
                    runOnUiThread(() -> {
                        // Đổi tham số để Android biết phải nhét String vào cái TextView nào trong file item_suggestion.xml
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                R.layout.item_suggestion,       // Layout custom mình vừa tạo
                                R.id.tv_suggestion_word,        // ID của TextView bên trong layout đó
                                suggestionList
                        );
                        etSearch.setAdapter(adapter);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // 2. Sự kiện: Khi user BẤM CHỌN một từ trong danh sách gợi ý
            etSearch.setOnItemClickListener((parent, view, position, id) -> {
                String selectedWord = (String) parent.getItemAtPosition(position);
                performSearch(selectedWord);

                // Ẩn bàn phím và xoá chữ sau khi search
                hideKeyboard(etSearch);
                etSearch.setText("");
            });

            // 3. Sự kiện cũ: Khi user tự gõ và bấm nút Search (Kính lúp) trên bàn phím ảo
            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = etSearch.getText().toString().trim();
                    if (!query.isEmpty()) {
                        performSearch(query);
                        hideKeyboard(etSearch);
                        etSearch.setText(""); // clear text
                    }
                    return true;
                }
                return false;
            });
        }
    }

    // Hàm phụ trợ để ẩn bàn phím ảo đi cho gọn code
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void performSearch(String word) {
        Toast.makeText(this, "Đang tra từ: " + word + "...", Toast.LENGTH_SHORT).show();

        AppExecutors.Companion.getInstance().getNetworkIO().execute(() -> {
            try {
                // Gọi suspend function của Kotlin từ Java
                DictionaryResult result = kotlinx.coroutines.BuildersKt.runBlocking(
                        kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> dictionaryRepository.lookupWord(word, continuation)
                );

                runOnUiThread(() -> {
                    if (result != null) {
                        showTranslationBottomSheet(result);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainMenuActivity.this, "Không tìm thấy từ này", Toast.LENGTH_SHORT).show());
            }
        });
    }
    private void showTranslationBottomSheet(DictionaryResult result) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_translation, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView tvWord = bottomSheetView.findViewById(R.id.tv_bs_word);
        TextView tvPhonetic = bottomSheetView.findViewById(R.id.tv_bs_phonetic);
        TextView tvMeaning = bottomSheetView.findViewById(R.id.tv_bs_meaning);
        Button btnSave = bottomSheetView.findViewById(R.id.btn_bs_save);

        tvWord.setText(result.getWord());
        tvPhonetic.setText(result.getPhonetic().isEmpty() ? "/.../" : result.getPhonetic());
        tvMeaning.setText(result.getVietnameseMeaning());

        btnSave.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng lưu từ đang được cập nhật", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void updateWordSetUI(WordSetEntity set, int index, int wordCount) {
        int cardId = (index == 1) ? R.id.card_word_set_1 : R.id.card_word_set_2;
        int titleId = (index == 1) ? R.id.tv_word_set_title_1 : R.id.tv_word_set_title_2;
        int countId = (index == 1) ? R.id.tv_word_count_1 : R.id.tv_word_count_2;

        LinearLayout card = findViewById(cardId);
        TextView tvTitle = findViewById(titleId);
        TextView tvCount = findViewById(countId);

        if (tvTitle != null) tvTitle.setText(set.getName());
        // UPDATE DỮ LIỆU ĐỘNG VÀO ĐÂY
        if (tvCount != null) tvCount.setText(wordCount + " words");

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

                if (list != null && list.size() >= 2) {
                    // Truy vấn DB lấy danh sách từ thuộc Set đó, rồi đếm size()
                    int count1 = wordRepository.getWordsInSet(list.get(0).getId()).size();
                    int count2 = wordRepository.getWordsInSet(list.get(1).getId()).size();

                    runOnUiThread(() -> {
                        updateWordSetUI(list.get(0), 1, count1);
                        updateWordSetUI(list.get(1), 2, count2);
                    });
                }
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
        // --- Featured Articles ---
        TextView btnSeeAllArticles = findViewById(R.id.btn_see_all_articles);
        if (btnSeeAllArticles != null) {
            btnSeeAllArticles.setOnClickListener(v -> Navigator.navigateTo(this, ArticlesActivity.class));
        }

        // --- Recent Word Sets ---
        TextView btnViewAllWordSets = findViewById(R.id.btn_view_all_word_sets);
        if (btnViewAllWordSets != null) {
            btnViewAllWordSets.setOnClickListener(v -> Navigator.navigateTo(this, LibraryActivity.class));
        }

        // --- Bottom Navigation ---
        
        // 1. Explore (Chính nó - hiện tại đang active)
        LinearLayout navExplore = findViewById(R.id.nav_explore);
        if (navExplore != null) {
            navExplore.setOnClickListener(null); 
        }

        // 2. Library
        LinearLayout navLibrary = findViewById(R.id.nav_library);
        if (navLibrary != null) {
            navLibrary.setOnClickListener(v -> Navigator.navigateTo(this, LibraryActivity.class));
        }

        // 3. Learn (Ngữ pháp)
        LinearLayout navLearn = findViewById(R.id.nav_learn);
        if (navLearn != null) {
            navLearn.setOnClickListener(v -> Navigator.navigateTo(this, GrammarRoadmapActivity.class));
        }

        // 4. Profile
        LinearLayout navProfile = findViewById(R.id.nav_profile);
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> Navigator.navigateTo(this, ProfileActivity.class));
        }

        // Nút Ngữ pháp ở phần Categories giữa màn hình
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
