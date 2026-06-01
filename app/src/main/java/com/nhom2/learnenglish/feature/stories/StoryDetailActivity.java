package com.nhom2.learnenglish.feature.stories;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.StoryEntity;
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity;
import com.nhom2.learnenglish.core.data.model.DictionaryResult;
import com.nhom2.learnenglish.core.data.model.DictionaryViewModel;
import com.nhom2.learnenglish.core.data.repository.DictionaryRepository;
import com.nhom2.learnenglish.core.data.repository.StoryRepository;
import com.nhom2.learnenglish.core.data.repository.WordRepository;
import com.nhom2.learnenglish.core.network.RetrofitClient;
import com.nhom2.learnenglish.core.network.dictionary.DictionaryApi;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.feature.wordsets.WordSetSelectionAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class StoryDetailActivity extends AppCompatActivity {

    private ImageView ivBack, ivCover;
    private TextView tvTitle, tvAuthor, tvContent;
    private StoryRepository storyRepository;

    // --- CÁC BIẾN CHO TÍNH NĂNG DỊCH TỪ ---
    private DictionaryViewModel dictionaryViewModel;
    private int selectedStart = -1;
    private int selectedEnd = -1;
    private List<WordSetEntity> availableWordSets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);

        ivBack = findViewById(R.id.iv_back);
        ivCover = findViewById(R.id.iv_story_cover);
        tvTitle = findViewById(R.id.tv_story_title);
        tvAuthor = findViewById(R.id.tv_story_author);
        tvContent = findViewById(R.id.tv_story_content);

        storyRepository = new StoryRepository(
                AppExecutors.Companion.getInstance(),
                AppDatabase.Companion.getInstance(this).storyDao()
        );

        ivBack.setOnClickListener(v -> finish());

        // Khởi tạo ViewModel và Observers cho chức năng tra từ
        setupDictionaryViewModel();
        setupViewModelObservers();

        long storyId = getIntent().getLongExtra("story_id", -1);
        if (storyId != -1) {
            loadStoryDetail(storyId);
        }
    }

    private void loadStoryDetail(long storyId) {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            StoryEntity story = storyRepository.getById(storyId);
            if (story != null) {
                runOnUiThread(() -> {
                    tvTitle.setText(story.getTitle());

                    // THAY VÌ SET TEXT BÌNH THƯỜNG, DÙNG HÀM TẠO CHỮ CLICKABLE ĐỂ TRA TỪ
                    makeContentClickable(story.getContent());

                    if (story.getAuthor() != null && !story.getAuthor().isEmpty()) {
                        tvAuthor.setText("By " + story.getAuthor());
                    } else {
                        tvAuthor.setText("Unknown Author");
                    }

                    if (story.getImage() != null && !story.getImage().isEmpty()) {
                        Glide.with(this)
                                .load(story.getImage())
                                .placeholder(android.R.color.darker_gray)
                                .into(ivCover);
                    } else {
                        ivCover.setImageResource(android.R.color.darker_gray);
                    }
                });
            }
        });
    }

    // =========================================================================
    // KHU VỰC 2: KHỞI TẠO VIEWMODEL VÀ LẮNG NGHE SỰ KIỆN (TRANSLATION)
    // =========================================================================
    private void setupDictionaryViewModel() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);

        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                WordRepository wordRepo = new WordRepository(
                        db.wordDao(),
                        db.wordSetDao(),
                        db.wordSrsDao(),
                        db.userWordSetDao(),
                        db.wordSetCrossDao(),
                        db.deletedSyncItemDao(),
                        AppExecutors.Companion.getInstance()
                );

                DictionaryApi dictionaryApi = RetrofitClient.INSTANCE.getInstance().create(DictionaryApi.class);
                DictionaryRepository dictRepo = new DictionaryRepository(dictionaryApi);

                return (T) new DictionaryViewModel(dictRepo, wordRepo);
            }
        };
        dictionaryViewModel = new ViewModelProvider(this, factory).get(DictionaryViewModel.class);
    }

    private void setupViewModelObservers() {
        dictionaryViewModel.getTranslationResult().observe(this, result -> {
            if (result != null
                    && result.getVietnameseMeaning() != null
                    && !result.getVietnameseMeaning().isEmpty()
                    && !result.getVietnameseMeaning().toLowerCase().contains("không thể dịch từ này")
                    && !result.getVietnameseMeaning().toLowerCase().contains("không tìm thấy")) {

                showTranslationBottomSheet(result);
            } else {
                Toasty.error(this, "Không tìm thấy nghĩa của từ này hoặc lỗi mạng!", Toast.LENGTH_LONG).show();
            }
        });

        dictionaryViewModel.getWordSets().observe(this, sets -> {
            if (sets != null) {
                availableWordSets.clear();
                availableWordSets.addAll(sets);
            }
        });

        dictionaryViewModel.getSaveStatus().observe(this, message -> {
            if (message != null) {
                if (message.equals("Lưu từ vựng thành công!")) {
                    Toasty.success(this, message, Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.warning(this, message, Toast.LENGTH_SHORT, true).show();
                }
            }
        });

        dictionaryViewModel.loadWordSets();
    }

    // =========================================================================
    // KHU VỰC 3: THUẬT TOÁN TÁCH TỪ VÀ GẮN SỰ KIỆN CLICK
    // =========================================================================
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
                    selectedStart = start;
                    selectedEnd = end;
                    widget.invalidate();
                    dictionaryViewModel.translateWord(word.trim().toLowerCase());
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);

                    if (start == selectedStart && end == selectedEnd) {
                        ds.setColor(Color.parseColor("#4CAF50"));
                        ds.bgColor = Color.parseColor("#E8F5E9");
                        ds.setFakeBoldText(true);
                    } else {
                        ds.setColor(tvContent.getCurrentTextColor());
                        ds.bgColor = Color.TRANSPARENT;
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

    // =========================================================================
    // KHU VỰC 4: TÍNH NĂNG GIAO DIỆN TRA TỪ VÀ LƯU (BOTTOM SHEETS)
    // =========================================================================
    private void showTranslationBottomSheet(DictionaryResult result) {
        if (result == null
                || result.getVietnameseMeaning() == null
                || result.getVietnameseMeaning().isEmpty()
                || result.getVietnameseMeaning().toLowerCase().contains("không thể dịch")
                || result.getVietnameseMeaning().toLowerCase().contains("không tìm thấy")) {
            Toasty.error(this, "Dữ liệu từ vựng không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_translation, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView tvWord = bottomSheetView.findViewById(R.id.tv_bs_word);
        TextView tvPhonetic = bottomSheetView.findViewById(R.id.tv_bs_phonetic);
        TextView tvMeaning = bottomSheetView.findViewById(R.id.tv_bs_meaning);
        Button btnSave = bottomSheetView.findViewById(R.id.btn_bs_save);
        View layoutSelectWordSet = bottomSheetView.findViewById(R.id.layout_select_word_set);
        TextView tvSelectedWordSet = bottomSheetView.findViewById(R.id.tv_selected_word_set);

        tvWord.setText(result.getWord());
        tvPhonetic.setText(result.getPhonetic().isEmpty() ? "/.../" : result.getPhonetic());
        tvMeaning.setText(result.getVietnameseMeaning());

        final String[] selectedSetId = {""};
        btnSave.setEnabled(false);
        btnSave.setAlpha(0.5f);

        layoutSelectWordSet.setOnClickListener(v -> {
            showWordSetSelectionDialog(tvSelectedWordSet, selectedSetId, btnSave);
        });

        btnSave.setOnClickListener(v -> {
            if (!selectedSetId[0].isEmpty()) {
                dictionaryViewModel.saveWordToSet(result, selectedSetId[0]);
                bottomSheetDialog.dismiss();
            } else {
                Toasty.warning(this, "Vui lòng chọn bộ từ vựng trước", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }

    private void showWordSetSelectionDialog(TextView tvSelectedWordSet, String[] selectedSetId, Button btnSave) {
        BottomSheetDialog selectionDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_dialog_select_word_set, null);
        selectionDialog.setContentView(view);

        RecyclerView rvSelection = view.findViewById(R.id.rv_word_set_selection);
        rvSelection.setLayoutManager(new LinearLayoutManager(this));

        WordSetSelectionAdapter adapter = new WordSetSelectionAdapter(availableWordSets, item -> {
            selectedSetId[0] = item.getId();
            tvSelectedWordSet.setText(item.getName());

            btnSave.setEnabled(true);
            btnSave.setAlpha(1.0f);
            selectionDialog.dismiss();
        });

        rvSelection.setAdapter(adapter);
        selectionDialog.show();
    }
}