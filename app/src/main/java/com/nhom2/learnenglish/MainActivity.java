package com.nhom2.learnenglish;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Điều hướng Bottom Navigation
        LinearLayout navLibrary = findViewById(R.id.nav_library);
        if (navLibrary != null) {
            navLibrary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
                    // FLAG_ACTIVITY_REORDER_TO_FRONT giúp tái sử dụng activity nếu nó đã tồn tại
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    // Loại bỏ hiệu ứng trượt mặc định để cảm giác giống chuyển Tab hơn
                    overridePendingTransition(0, 0);
                }
            });
        }
        
        // Tab hiện tại (Explore) - không cần xử lý click hoặc xử lý để scroll lên đầu
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Loại bỏ hiệu ứng khi đóng activity để mượt mà
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }
}