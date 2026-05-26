package com.nhom2.learnenglish.feature.wordsets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity;
import com.nhom2.learnenglish.core.data.local.mockdata.MockDataImport;
import com.nhom2.learnenglish.core.data.repository.WordRepository;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.feature.grammar.GrammarRoadmapActivity;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;
import com.nhom2.learnenglish.feature.profile.ProfileActivity;

import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    private WordRepository wordRepository;
    private WordSetAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        setupData();
        setupBackNavigation();
        setupBottomNavigation();
        setupRecyclerView();
    }

    private void setupData() {
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        wordRepository = new WordRepository(
                db.wordDao(),
                db.wordSetDao(),
                db.wordSrsDao(),
                db.userWordSetDao(),
                db.wordSetCrossDao(),
                AppExecutors.Companion.getInstance()
        );

        MockDataImport.INSTANCE.importIfNeeded(this, this::loadWordSetData);
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigator.navigateTo(LibraryActivity.this, MainMenuActivity.class);
            }
        });
    }

    private void setupBottomNavigation() {
        // Điều hướng sang Explore
        LinearLayout navExplore = findViewById(R.id.nav_explore);
        if (navExplore != null) {
            navExplore.setOnClickListener(v -> Navigator.navigateTo(this, MainMenuActivity.class));
        }

        // Điều hướng sang Learn
        LinearLayout navLearn = findViewById(R.id.nav_learn);
        if (navLearn != null) {
            navLearn.setOnClickListener(v -> Navigator.navigateTo(this, GrammarRoadmapActivity.class));
        }

        // Điều hướng sang Profile
        LinearLayout navProfile = findViewById(R.id.nav_profile);
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> Navigator.navigateTo(this, ProfileActivity.class));
        }
    }

    private void setupRecyclerView() {
        RecyclerView rvWordSets = findViewById(R.id.rv_word_sets);
        if (rvWordSets != null) {
            rvWordSets.setLayoutManager(new GridLayoutManager(this, 2));
            adapter = new WordSetAdapter(item -> {
                Intent intent = new Intent(this, WordSetDetailActivity.class);
                intent.putExtra("SET_ID", item.getId());
                intent.putExtra("SET_TITLE", item.getName());
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
            rvWordSets.setAdapter(adapter);
        }
    }

    private void loadWordSetData() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                List<WordSetEntity> list = wordRepository.getAllSets();
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
