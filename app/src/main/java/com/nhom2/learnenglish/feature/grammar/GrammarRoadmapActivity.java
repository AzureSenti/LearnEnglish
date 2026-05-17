package com.nhom2.learnenglish.feature.grammar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.model.GrammarLessonWithStatus;
import com.nhom2.learnenglish.core.data.repository.GrammarRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.SessionManager;

import java.util.List;

public class GrammarRoadmapActivity extends AppCompatActivity implements GrammarAdapter.OnItemClickListener {

    private RecyclerView rvGrammarRoadmap;
    private GrammarAdapter grammarAdapter;
    private GrammarRepository grammarRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_roadmap);

        initViews();
        setupToolbar();
        setupRepository();
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại dữ liệu mỗi khi quay lại màn hình này để cập nhật dấu tích xanh mới nhất
        loadGrammarRoadmapData();
    }

    private void initViews() {
        rvGrammarRoadmap = findViewById(R.id.rv_grammar_roadmap);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_grammar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void setupRepository() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        sessionManager = new SessionManager(this);

        // Khởi tạo các DAO tương ứng và truyền vào Repository
        grammarRepository = new GrammarRepository(
                db.grammarLessonDao(),
                db.grammarQuestionDao(),
                db.userGrammarProgressDao(),
                AppExecutors.Companion.getInstance()
        );
    }

    private void setupRecyclerView() {
        grammarAdapter = new GrammarAdapter(this);
        rvGrammarRoadmap.setLayoutManager(new LinearLayoutManager(this));
        rvGrammarRoadmap.setAdapter(grammarAdapter);
    }

    private void loadGrammarRoadmapData() {
        long currentUserId = sessionManager.getCurrentUserId();

        // Đẩy tác vụ truy vấn Database xuống Disk IO Thread ngầm
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                // Sử dụng cầu nối runBlocking để gọi hàm ngắt (suspend) của cấu trúc Kotlin từ Java
                List<GrammarLessonWithStatus> roadmapData = kotlinx.coroutines.BuildersKt.runBlocking(
                        kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> grammarRepository.getGrammarRoadmap(currentUserId, continuation)
                );

                // Sau khi lấy được dữ liệu, quay về Main Thread để cập nhật giao diện người dùng
                runOnUiThread(() -> grammarAdapter.updateData(roadmapData));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GrammarRoadmapActivity.this,
                        "Không thể tải dữ liệu lộ trình học tập", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onLessonClick(long lessonId) {
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Quay lại màn hình trước đó khi bấm nút back trên toolbar
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}