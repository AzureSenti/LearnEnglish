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
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.nhom2.learnenglish.core.util.SessionManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom2.learnenglish.core.data.model.DictionaryViewModel;
import com.nhom2.learnenglish.feature.profile.StreakUtils;
import com.nhom2.learnenglish.feature.wordsets.WordSetSelectionAdapter;
import es.dmoral.toasty.Toasty;

public class MainMenuActivity extends AppCompatActivity {

    private ArticleRepository articleRepository;
    private WordRepository wordRepository;
    private DictionaryRepository dictionaryRepository;

    private DictionaryViewModel dictionaryViewModel;
    private List<WordSetEntity> availableWordSets = new ArrayList<>();

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
                db.wordDao(),
                db.wordSetDao(),
                db.wordSrsDao(),
                db.userWordSetDao(),
                db.wordSetCrossDao(),
                db.deletedSyncItemDao(),
                AppExecutors.Companion.getInstance()
        );

        // KHỞI TẠO DICTIONARY REPOSITORY
        DictionaryApi dictionaryApi = RetrofitClient.INSTANCE.getInstance().create(DictionaryApi.class);
        dictionaryRepository = new DictionaryRepository(dictionaryApi);
        // --- BỔ SUNG KHỞI TẠO TỪ ĐIỂN Ở ĐÂY ---

        DictionaryRepository dictRepo = new DictionaryRepository(dictionaryApi);
        dictionaryViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new DictionaryViewModel(dictRepo, wordRepository);
            }
        }).get(DictionaryViewModel.class);

        // Quan sát danh sách bộ từ vựng để đổ vào Modal chọn
        dictionaryViewModel.getWordSets().observe(this, sets -> {
            if (sets != null) {
                availableWordSets.clear();
                availableWordSets.addAll(sets);
            }
        });

        //  Bổ sung lắng nghe kết quả khi lưu từ vựng thành công
        dictionaryViewModel.getSaveStatus().observe(this, message -> {
            // Kiểm tra nội dung message để hiển thị Toasty tương ứng
            if ("Lưu từ vựng thành công!".equals(message)) {
                Toasty.success(this, message, Toast.LENGTH_SHORT, true).show();

                loadRecentWordSets(); // Cập nhật lại số lượng từ trên thẻ màn hình chính
            } else {
                Toasty.error(this, message, Toast.LENGTH_SHORT, true).show();
            }
        });
        dictionaryViewModel.loadWordSets();
    }
    private void setupSearchBar() {
        // Lưu ý đổi từ EditText thành AutoCompleteTextView để có gợi ý
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

            // 3. Sự kiện : Khi user tự gõ và bấm nút Search (Kính lúp) trên bàn phím ảo
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
        Toasty.info(this, "Đang tra từ: " + word + "...", Toast.LENGTH_SHORT, true).show();

        AppExecutors.Companion.getInstance().getNetworkIO().execute(() -> {
            try {
                // Gọi suspend function của Kotlin từ Java
                DictionaryResult result = kotlinx.coroutines.BuildersKt.runBlocking(
                        kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> dictionaryRepository.lookupWord(word, continuation)
                );

                runOnUiThread(() -> {
                    //  Chỉ mở bảng dịch khi lấy được dữ liệu thành công
                    if (result != null
                            && result.getVietnameseMeaning() != null
                            && !result.getVietnameseMeaning().isEmpty()
                            && !result.getVietnameseMeaning().toLowerCase().contains("không thể dịch từ này")
                            && !result.getVietnameseMeaning().toLowerCase().contains("không tìm thấy")) {
                        showTranslationBottomSheet(result);
                    } else {
                        Toasty.warning(MainMenuActivity.this, "Không tìm thấy nghĩa của từ này. Vui lòng kiểm tra lại kết nối mạng!", Toast.LENGTH_LONG, true).show() ;                   }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toasty.error(MainMenuActivity.this, "Lỗi kết nối mạng, không thể tra từ!", Toast.LENGTH_SHORT, true).show());
            }
        });
    }
    //  Hàm hiển thị BottomSheet tra từ
    private void showTranslationBottomSheet(DictionaryResult result) {
        //  BẢO MẬT BỔ SUNG: Không cho mở nếu result rỗng
        if (result == null || result.getVietnameseMeaning() == null || result.getVietnameseMeaning().isEmpty()) {
            Toasty.error(this, "Dữ liệu từ vựng không hợp lệ!", Toast.LENGTH_SHORT, true).show();
            return;
        }

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_translation, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView tvWord = bottomSheetView.findViewById(R.id.tv_bs_word);
        TextView tvPhonetic = bottomSheetView.findViewById(R.id.tv_bs_phonetic);
        TextView tvMeaning = bottomSheetView.findViewById(R.id.tv_bs_meaning);
        Button btnSave = bottomSheetView.findViewById(R.id.btn_bs_save);

        // Ánh xạ layout chọn bộ từ
        View layoutSelectWordSet = bottomSheetView.findViewById(R.id.layout_select_word_set);
        TextView tvSelectedWordSet = bottomSheetView.findViewById(R.id.tv_selected_word_set);

        tvWord.setText(result.getWord());
        tvPhonetic.setText(result.getPhonetic().isEmpty() ? "/.../" : result.getPhonetic());
        tvMeaning.setText(result.getVietnameseMeaning());

        final String[] selectedSetId = {""};

        // Khi mở lên, mờ nút lưu vì chưa chọn thư mục nào
        btnSave.setEnabled(false);
        btnSave.setAlpha(0.5f);

        // Sự kiện: Bấm để mở Modal chọn bộ từ vựng
        layoutSelectWordSet.setOnClickListener(v -> {
            showWordSetSelectionDialog(tvSelectedWordSet, selectedSetId, btnSave);
        });

        // Sự kiện: Bấm lưu từ
        btnSave.setOnClickListener(v -> {
            if (!selectedSetId[0].isEmpty()) {
                // Đảm bảo chỉ truyền result thật xuống Repository
                dictionaryViewModel.saveWordToSet(result, selectedSetId[0]);
                bottomSheetDialog.dismiss();
            } else {
                Toasty.info(this, "Vui lòng chọn bộ từ vựng trước", Toast.LENGTH_SHORT, true).show();
            }
        });

        bottomSheetDialog.show();
    }

    //  Hàm hiển thị Modal chọn Bộ từ vựng phụ
    private void showWordSetSelectionDialog(TextView tvSelectedWordSet, String[] selectedSetId, Button btnSave) {
        BottomSheetDialog selectionDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_dialog_select_word_set, null);
        selectionDialog.setContentView(view);

        RecyclerView rvSelection = view.findViewById(R.id.rv_word_set_selection);
        rvSelection.setLayoutManager(new LinearLayoutManager(this));

        WordSetSelectionAdapter adapter = new WordSetSelectionAdapter(availableWordSets, item -> {
            selectedSetId[0] = item.getId();
            tvSelectedWordSet.setText(item.getName());

            // Bật sáng nút Lưu
            btnSave.setEnabled(true);
            btnSave.setAlpha(1.0f);

            selectionDialog.dismiss();
        });

        rvSelection.setAdapter(adapter);
        selectionDialog.show();
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
    // Hàm  Truy vấn số lượng từ đến hạn trong Database
    // Hàm tính Streak thực tế cho màn hình chính
    private void loadGlobalStreak() {
        SessionManager sessionManager = new SessionManager(this);
        String userId = sessionManager.getCurrentUserId();

        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                // Truy vấn thực tế từ WordSrsDao
                List<com.nhom2.learnenglish.core.data.local.entity.word.WordSrsEntity> srsRecords = 
                    AppDatabase.Companion.getInstance(this).wordSrsDao().getAllForUser(userId);
                
                List<Long> studyDates = new ArrayList<>();
                if (srsRecords != null) {
                    for (com.nhom2.learnenglish.core.data.local.entity.word.WordSrsEntity record : srsRecords) {
                        if (record.getLastReviewDate() != null) {
                            studyDates.add(record.getLastReviewDate());
                        }
                    }
                }

                // Sử dụng StreakUtils
                int streak = StreakUtils.calculateStreak(studyDates);

                runOnUiThread(() -> {
                    TextView tvHomeStreak = findViewById(R.id.tv_home_streak);
                    if (tvHomeStreak != null) {
                        tvHomeStreak.setText(streak + "\nDays");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadGlobalReviewCount() {
        SessionManager sessionManager = new SessionManager(this);
        String userId = sessionManager.getCurrentUserId();

        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                // Lấy toàn bộ từ đến hạn (Global)
                int reviewCount = wordRepository.getGlobalWordsForReview(userId).size();

                runOnUiThread(() -> {
                    // Truyền con số xuống hàm cập nhật giao diện
                    updateReviewUI(reviewCount);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Hàm  Cập nhật giao diện và gắn sự kiện cho nút bấm mới
    private void updateReviewUI(int reviewCount) {
        TextView tvReviewCount = findViewById(R.id.tv_global_review_count);
        com.google.android.material.button.MaterialButton btnReview = findViewById(R.id.btn_action_global_review);

        if (tvReviewCount == null || btnReview == null) return;

        if (reviewCount > 0) {
            // Trạng thái 1: Có từ cần ôn
            String htmlText = "Bạn có <font color='#FF0000'><b>" + reviewCount + "</b></font> từ đến hạn";
            tvReviewCount.setText(android.text.Html.fromHtml(htmlText, android.text.Html.FROM_HTML_MODE_LEGACY));

            // Kích hoạt nút bấm
            btnReview.setEnabled(true);
            btnReview.setAlpha(1.0f);

            // Chuyển sang màn hình Game (Global)
            btnReview.setOnClickListener(v -> {
                Intent intent = new Intent(MainMenuActivity.this, com.nhom2.learnenglish.feature.game.VocabularyGameActivity.class);
                intent.putExtra("GAME_MODE", "REVIEW");
                // Cố tình KHÔNG truyền SET_ID để Game bốc toàn bộ từ đến hạn
                startActivity(intent);
            });
        } else {
            // Trạng thái 2: Không có từ nào cần ôn
            tvReviewCount.setText("Bạn đã hoàn thành mục tiêu");
            tvReviewCount.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.green_tag_text));

            // Làm mờ và vô hiệu hóa nút
            btnReview.setEnabled(false);
            btnReview.setAlpha(0.5f);
            btnReview.setOnClickListener(null);
        }
    }
    // Hàm 1: Đếm số từ mới trong Database
    private void loadGlobalLearnCount() {
        SessionManager sessionManager = new SessionManager(this);
        String userId = sessionManager.getCurrentUserId();

        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                int learnCount = wordRepository.getGlobalNewWordsToLearn(userId).size();
                runOnUiThread(() -> updateLearnUI(learnCount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    // Hàm 2: Cập nhật chữ và gắn lệnh chuyển sang Game
    private void updateLearnUI(int count) {
        TextView tvLearnCount = findViewById(R.id.tv_global_learn_count);
        com.google.android.material.button.MaterialButton btnLearn = findViewById(R.id.btn_action_global_learn);

        if (tvLearnCount == null || btnLearn == null) return;

        if (count > 0) {
            String htmlText = "Có <font color='#FF9800'><b>" + count + "</b></font> từ đang chờ";
            tvLearnCount.setText(android.text.Html.fromHtml(htmlText, android.text.Html.FROM_HTML_MODE_LEGACY));

            btnLearn.setEnabled(true);
            btnLearn.setAlpha(1.0f);

            btnLearn.setOnClickListener(v -> {
                Intent intent = new Intent(MainMenuActivity.this, com.nhom2.learnenglish.feature.game.VocabularyGameActivity.class);
                intent.putExtra("GAME_MODE", "LEARN_NEW"); // Đẩy cờ HỌC MỚI sang Game
                startActivity(intent);
            });
        } else {
            tvLearnCount.setText("Bạn đã học hết từ vựng!");
            tvLearnCount.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.text_secondary));

            btnLearn.setEnabled(false);
            btnLearn.setAlpha(0.5f);
            btnLearn.setOnClickListener(null);
        }}

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

        // 1. Explore (Chính nó - hiện tại đang active -> Khóa click)
        LinearLayout navExplore = findViewById(R.id.nav_explore);
        if (navExplore != null) {
            navExplore.setOnClickListener(null);
        }

        // 2. Chuyển sang Library
        LinearLayout navLibrary = findViewById(R.id.nav_library);
        if (navLibrary != null) {
            navLibrary.setOnClickListener(v -> {
                Navigator.navigateTo(this, LibraryActivity.class);
                overridePendingTransition(0, 0); // THÊM DÒNG NÀY: Xóa hiệu ứng chuyển trang
            });
        }

        // 3. Chuyển sang Learn (Ngữ pháp)
        LinearLayout navLearn = findViewById(R.id.nav_learn);
        if (navLearn != null) {
            navLearn.setOnClickListener(v -> {
                Navigator.navigateTo(this, GrammarRoadmapActivity.class);
                overridePendingTransition(0, 0); // THÊM DÒNG NÀY: Xóa hiệu ứng chuyển trang
            });
        }

        // 4. Chuyển sang Profile
        LinearLayout navProfile = findViewById(R.id.nav_profile);
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Navigator.navigateTo(this, ProfileActivity.class);
                overridePendingTransition(0, 0); // THÊM DÒNG NÀY: Xóa hiệu ứng chuyển trang
            });
        }

        // Nút Ngữ pháp ở phần Categories giữa màn hình (Giữ nguyên)
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
    private void loadUserProfile() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                // Lấy thông tin người dùng đang đăng nhập từ UserDao
                com.nhom2.learnenglish.core.data.local.entity.UserEntity activeUser =
                        AppDatabase.Companion.getInstance(this).userDao().getActiveUser();

                runOnUiThread(() -> {
                    TextView tvGreeting = findViewById(R.id.tv_greeting);
                    if (tvGreeting != null) {
                        if (activeUser != null && activeUser.getFullName() != null && !activeUser.getFullName().isEmpty()) {
                            // Hiển thị tên người dùng
                            tvGreeting.setText("Hi, " + activeUser.getFullName() + "!");
                        } else {
                            // Fallback nếu không có dữ liệu
                            tvGreeting.setText("Hi, Guest!");
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateFeaturedUI(ArticleEntity article) {
        ImageView ivFeaturedImage = findViewById(R.id.iv_featured_image);
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
        // 2. Tải ảnh bằng Glide
        if (ivFeaturedImage != null) {
            if (article.getImage() != null && !article.getImage().isEmpty()) {
                Glide.with(this)
                        .load(article.getImage())
                        .placeholder(android.R.color.darker_gray) // Ảnh chờ khi đang tải
                        .into(ivFeaturedImage);
            } else {
                ivFeaturedImage.setImageResource(android.R.color.darker_gray);
            }
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
    protected void onResume() {
        super.onResume();
        // Tự động cập nhật lại số lượng từ mới nhất mỗi khi quay lại trang chủ
        loadRecentWordSets();
        //  Tự động đếm và cập nhật lại số từ cần ôn tập và từ mới mỗi khi vào trang chủ
        loadGlobalReviewCount();
        loadGlobalLearnCount();
        //
        loadUserProfile();
        loadGlobalStreak(); // THÊM DÒNG NÀY

        // Tự động đồng bộ dữ liệu khi có mạng
        com.nhom2.learnenglish.core.util.NetworkSyncManager.INSTANCE.syncIfOnline(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }
}
