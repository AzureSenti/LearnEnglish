package com.nhom2.learnenglish.feature.grammar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.model.GrammarLessonWithStatus;
import com.nhom2.learnenglish.core.data.repository.GrammarRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.core.util.SessionManager;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;
import com.nhom2.learnenglish.feature.wordsets.LibraryActivity;

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
        setupRepository();
        setupRecyclerView();
        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGrammarRoadmapData();
    }

    private void initViews() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvGrammarRoadmap = findViewById(R.id.rv_grammar_roadmap);

        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }
    }

    private void setupRepository() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        sessionManager = new SessionManager(this);
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

        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                List<GrammarLessonWithStatus> roadmapData = grammarRepository.getGrammarRoadmap(currentUserId);

                runOnUiThread(() -> grammarAdapter.updateData(roadmapData));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onLessonClick(long lessonId) {
        Bundle bundle = new Bundle();
        bundle.putLong("LESSON_ID", lessonId);
        Navigator.navigateTo(this, GrammarTheoryActivity.class, bundle);
    }

    private void setupBottomNav() {
        LinearLayout navExplore = findViewById(R.id.nav_explore);
        LinearLayout navLibrary = findViewById(R.id.nav_library);

        if (navExplore != null) {
            navExplore.setOnClickListener(v -> {
                Navigator.navigateTo(this, MainMenuActivity.class);
                finishAffinity(); // Xóa stack để về trang chủ mượt hơn
            });
        }
        if (navLibrary != null) {
            navLibrary.setOnClickListener(v -> {
                Navigator.navigateTo(this, LibraryActivity.class);
                finish();
            });
        }
    }
}