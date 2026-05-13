package com.nhom2.learnenglish.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;

public class LibraryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        setupBackNavigation();
        setupBottomNavigation();
        setupWordSetNavigation();
    }

    private void setupBackNavigation() {
        // Xử lý nút Back theo chuẩn Android mới
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveToExplore();
            }
        });
    }

    private void setupBottomNavigation() {
        // Chuyển về màn hình Explore
        LinearLayout navExplore = findViewById(R.id.nav_explore);
        if (navExplore != null) {
            navExplore.setOnClickListener(v -> moveToExplore());
        }
    }

    private void setupWordSetNavigation() {
        // Gán sự kiện click cho các bộ từ vựng
        setupCardClick(R.id.card_irregular_verbs, "Irregular Verbs");
        setupCardClick(R.id.card_ielts_prep, "IELTS Prep");
        setupCardClick(R.id.card_it_vocabulary, "IT Vocabulary");
        setupCardClick(R.id.card_travel_essentials, "Travel Essentials");
        setupCardClick(R.id.card_business_idioms, "Business Idioms");
    }

    private void setupCardClick(int id, String title) {
        View card = findViewById(id);
        if (card != null) {
            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, WordSetDetailActivity.class);
                intent.putExtra("SET_TITLE", title);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }
    }

    private void moveToExplore() {
        Intent intent = new Intent(LibraryActivity.this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}