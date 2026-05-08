package com.nhom2.learnenglish;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class LibraryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        // Xử lý nút Back theo chuẩn Android mới (fix warning)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Khi nhấn back thì về Explore thay vì thoát app
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
        Intent intent = new Intent(LibraryActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0); // Tắt hiệu ứng để mượt hơn
    }
}