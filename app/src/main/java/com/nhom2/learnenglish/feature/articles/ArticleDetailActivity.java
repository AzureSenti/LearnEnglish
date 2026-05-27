package com.nhom2.learnenglish.feature.articles;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity;
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity;
import com.nhom2.learnenglish.core.data.model.DictionaryResult;
import com.nhom2.learnenglish.core.data.repository.ArticleRepository;
import com.nhom2.learnenglish.core.data.repository.DictionaryRepository;
import com.nhom2.learnenglish.core.data.repository.WordRepository;
import com.nhom2.learnenglish.core.network.RetrofitClient;
import com.nhom2.learnenglish.core.network.dictionary.DictionaryApi;
import com.nhom2.learnenglish.feature.dictionary.DictionaryViewModel;
import com.nhom2.learnenglish.core.util.AppExecutors;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.bumptech.glide.Glide;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nhom2.learnenglish.feature.wordsets.WordSetSelectionAdapter;

public class ArticleDetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvContent, tvAuthor, tvInfo, tvToolbarTitle;
    private ArticleRepository articleRepository;
    private ImageView ivDetailImage;

    // 1. KHAI BÁO THÊM VIEWMODEL Ở ĐÂY
    private DictionaryViewModel dictionaryViewModel;
    // THÊM 2 BIẾN NÀY ĐỂ LƯU VỊ TRÍ TỪ ĐANG ĐƯỢC CHỌN
    private int selectedStart = -1;
    private int selectedEnd = -1;
    // Đặt ở trên cùng class ArticleDetailActivity
    private List<WordSetEntity> availableWordSets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        tvTitle = findViewById(R.id.tv_detail_title);
        tvContent = findViewById(R.id.tv_detail_content);
        tvAuthor = findViewById(R.id.tv_detail_author);
        tvInfo = findViewById(R.id.tv_detail_info);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        ivDetailImage = findViewById(R.id.iv_detail_image);

        setupData();
        setupToolbar();
    }

    private void setupData() {
        // Khởi tạo ArticleRepository cũ của bạn
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        articleRepository = new ArticleRepository(AppExecutors.Companion.getInstance(), db.articleDao());

        // 2. KHỞI TẠO DICTIONARY VIEWMODEL QUA FACTORY (Do ViewModel có tham số truyền vào)
        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                // Khởi tạo WordRepository với đầy đủ 5 Dao theo cấu trúc dự án
                WordRepository wordRepo = new WordRepository(
                        db.wordDao(),
                        db.wordSetDao(),
                        db.wordSrsDao(),
                        db.userWordSetDao(),
                        db.wordSetCrossDao(),
                        AppExecutors.Companion.getInstance()
                );

                // Khởi tạo API mạng và DictionaryRepository
                DictionaryApi dictionaryApi = RetrofitClient.INSTANCE.getInstance().create(DictionaryApi.class);
                DictionaryRepository dictRepo = new DictionaryRepository(dictionaryApi);

                return (T) new DictionaryViewModel(dictRepo, wordRepo);
            }
        };
        dictionaryViewModel = new ViewModelProvider(this, factory).get(DictionaryViewModel.class);

        // 3. ĐĂNG KÝ LẮNG NGHE (OBSERVE) DỮ LIỆU TỪ VIEWMODEL
        setupViewModelObservers();

        // Nhận ID bài báo và load dữ liệu
        long articleId = getIntent().getLongExtra("article_id", -1);
        if (articleId != -1) {
            loadArticleDetail(articleId);
        }
    }

    // 4. HÀM LẮNG NGHE KẾT QUẢ TỪ API MẠNG VÀ DATABASE
    private void setupViewModelObservers() {
        // Lắng nghe kết quả tra từ
        dictionaryViewModel.getTranslationResult().observe(this, result -> {
            if (result != null) {
                showTranslationBottomSheet(result);
            } else {
                Toast.makeText(this, "Không tìm thấy từ này trong từ điển", Toast.LENGTH_SHORT).show();
            }
        });
        // Lắng nghe danh sách Word Set
        dictionaryViewModel.getWordSets().observe(this, sets -> {
            if (sets != null) {
                availableWordSets.clear();
                availableWordSets.addAll(sets);
            }
        });

        // Lắng nghe trạng thái khi bấm lưu từ vựng (Thành công hay Trùng lặp)
        dictionaryViewModel.getSaveStatus().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
        // lấy data
        dictionaryViewModel.loadWordSets();
    }

    private void loadArticleDetail(long id) {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                ArticleEntity article = articleRepository.getById(id);

                if (article != null) {
                    runOnUiThread(() -> {
                        tvTitle.setText(article.getTitle());

                        // 5. THAY ĐỔI: Biến đổi nội dung bài báo thành các từ có thể click
                        makeContentClickable(article.getContent());
                        //img
                        if (article.getImage() != null && !article.getImage().isEmpty()) {
                            Glide.with(ArticleDetailActivity.this)
                                    .load(article.getImage())
                                    .into(ivDetailImage);
                        }

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

    // 6. THUẬT TOÁN TÁCH TỪ VÀ GẮN SỰ KIỆN CLICK
    // 6. THUẬT TOÁN TÁCH TỪ VÀ GẮN SỰ KIỆN CLICK (CÓ HIGHLIGHT TÔ XANH)
    private void makeContentClickable(String content) {
        SpannableString spannableString = new SpannableString(content);
        Matcher matcher = Pattern.compile("[a-zA-Z]+").matcher(content);

        while (matcher.find()) {
            final int start = matcher.start();
            final int end = matcher.end();
            final String word = matcher.group();

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    // 1. Cập nhật vị trí từ đang được click
                    selectedStart = start;
                    selectedEnd = end;

                    // 2. Ép TextView vẽ lại giao diện để hiển thị màu mới
                    widget.invalidate();

                    // 3. Gọi lệnh dịch
                    dictionaryViewModel.translateWord(word.trim().toLowerCase());
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false); // Không gạch chân chữ

                    // KIỂM TRA: Nếu từ này đang được click thì tô xanh nó
                    if (start == selectedStart && end == selectedEnd) {
                        ds.setColor(Color.parseColor("#4CAF50")); // Màu chữ xanh lá
                        ds.bgColor = Color.parseColor("#E8F5E9"); // (Tùy chọn) Highlight nền màu xanh nhạt cho đẹp
                        ds.setFakeBoldText(true); // In đậm chữ lên một chút
                    } else {
                        // Nếu không được click thì trả về màu mặc định
                        ds.setColor(tvContent.getCurrentTextColor());
                        ds.bgColor = Color.TRANSPARENT; // Xóa màu nền
                        ds.setFakeBoldText(false);
                    }
                }
            };

            spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        tvContent.setText(spannableString);
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        tvContent.setHighlightColor(Color.TRANSPARENT);
    }

    // 7. HIỂN THỊ DIALOG KẾT QUẢ DỊCH
    private void showTranslationBottomSheet(DictionaryResult result) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_translation, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView tvWord = bottomSheetView.findViewById(R.id.tv_bs_word);
        TextView tvPhonetic = bottomSheetView.findViewById(R.id.tv_bs_phonetic);
        TextView tvMeaning = bottomSheetView.findViewById(R.id.tv_bs_meaning);
        Button btnSave = bottomSheetView.findViewById(R.id.btn_bs_save);

        // Ánh xạ layout chọn từ
        View layoutSelectWordSet = bottomSheetView.findViewById(R.id.layout_select_word_set);
        TextView tvSelectedWordSet = bottomSheetView.findViewById(R.id.tv_selected_word_set);

        tvWord.setText(result.getWord());
        tvPhonetic.setText(result.getPhonetic().isEmpty() ? "/.../" : result.getPhonetic());
        tvMeaning.setText(result.getVietnameseMeaning());

        // Dùng mảng 1 phần tử để lưu ID (nhằm thay đổi được giá trị bên trong hàm lambda)
        final long[] selectedSetId = {-1L};

        // Khi mở lên, mờ nút đi vì chưa chọn thư mục nào
        btnSave.setEnabled(false);
        btnSave.setAlpha(0.5f);

        // 1. SỰ KIỆN BẤM ĐỂ MỞ MODAL CHỌN
        layoutSelectWordSet.setOnClickListener(v -> {
            showWordSetSelectionDialog(tvSelectedWordSet, selectedSetId, btnSave);
        });

        // 2. SỰ KIỆN BẤM LƯU TỪ
        btnSave.setOnClickListener(v -> {
            if (selectedSetId[0] != -1L) {
                dictionaryViewModel.saveWordToSet(result, selectedSetId[0]);
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(this, "Vui lòng chọn bộ từ vựng trước", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }
    // HÀM MỚI ĐỂ HIỂN THỊ MODAL CHỌN TỪ VỰNG
    private void showWordSetSelectionDialog(TextView tvSelectedWordSet, long[] selectedSetId, Button btnSave) {
        BottomSheetDialog selectionDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_dialog_select_word_set, null);
        selectionDialog.setContentView(view);

        RecyclerView rvSelection = view.findViewById(R.id.rv_word_set_selection);
        rvSelection.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter với danh sách availableWordSets đã lấy từ Database
        WordSetSelectionAdapter adapter = new WordSetSelectionAdapter(availableWordSets, item -> {
            // Khi user nhấp vào 1 dòng trong Modal thứ 2:
            selectedSetId[0] = item.getId(); // Lưu ID lại
            tvSelectedWordSet.setText(item.getName()); // Đổi text trên UI gốc

            // Bật sáng nút Lưu
            btnSave.setEnabled(true);
            btnSave.setAlpha(1.0f);

            // Tắt Modal phụ đi
            selectionDialog.dismiss();
        });

        rvSelection.setAdapter(adapter);
        selectionDialog.show();
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

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }
}