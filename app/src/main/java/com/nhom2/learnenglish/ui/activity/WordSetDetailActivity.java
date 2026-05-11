package com.nhom2.learnenglish.ui.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.model.Word;
import com.nhom2.learnenglish.ui.adapter.WordAdapter;

import java.util.ArrayList;
import java.util.List;

public class WordSetDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_set_detail);

        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        ImageView ivBack = findViewById(R.id.iv_back);
        TextView tvTitle = findViewById(R.id.tv_title);
        
        // Nhận dữ liệu từ intent nếu có (sau này dùng DB sẽ truyền ID hoặc Title)
        String title = getIntent().getStringExtra("SET_TITLE");
        if (title != null) {
            tvTitle.setText(title);
        }

        if (ivBack != null) {
            ivBack.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }
    }

    private void setupRecyclerView() {
        RecyclerView rvWords = findViewById(R.id.rv_words);
        if (rvWords != null) {
            rvWords.setLayoutManager(new LinearLayoutManager(this));
            
            List<Word> dummyWords = getDummyWords();
            WordAdapter adapter = new WordAdapter(dummyWords);
            rvWords.setAdapter(adapter);
        }
    }

    private List<Word> getDummyWords() {
        List<Word> list = new ArrayList<>();
        list.add(new Word("Resilient", "/rɪˈzɪliənt/", "Lv. 4"));
        list.add(new Word("Ubiquitous", "/juːˈbɪkwɪtəs/", "Lv. 2"));
        list.add(new Word("Ephemeral", "/ɪˈfemərəl/", "Lv. 1"));
        list.add(new Word("Paradigm", "/ˈpærədaɪm/", "Lv. 5"));
        list.add(new Word("Meticulous", "/məˈtɪkjələs/", "Lv. 3"));
        return list;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}