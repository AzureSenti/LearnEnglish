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

        // Xử lý nút Back theo chuẩn Android mới
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveToExplore();
            }
        });

        // Chuyển về màn hình Explore
        LinearLayout navExplore = findViewById(R.id.nav_explore);
        if (navExplore != null) {
            navExplore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToExplore();
                }
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