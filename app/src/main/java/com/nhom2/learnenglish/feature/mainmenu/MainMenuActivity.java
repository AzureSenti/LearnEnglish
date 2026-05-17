package com.nhom2.learnenglish.feature.mainmenu;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.mockdata.MockDataImport;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.feature.articles.ArticlesActivity;
import com.nhom2.learnenglish.feature.wordsets.LibraryActivity;
import com.nhom2.learnenglish.ui.activity.ProfileActivity;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupWindowInsets();
        setupNavigation();

        MockDataImport.INSTANCE.importIfNeeded(this, () -> { });
    }

    private void setupWindowInsets() {
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void setupNavigation() {
        TextView btnViewAllGoals = findViewById(R.id.btn_view_all_goals);
        if (btnViewAllGoals != null) {
            btnViewAllGoals.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(MainMenuActivity.this, LibraryActivity.class));
        }

        LinearLayout navExplore = findViewById(R.id.nav_explore);
        if (navExplore != null) {
            navExplore.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(MainMenuActivity.this, ArticlesActivity.class));
        }

        LinearLayout navLibrary = findViewById(R.id.nav_library);
        if (navLibrary != null) {
            navLibrary.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(MainMenuActivity.this, LibraryActivity.class));
        }

        LinearLayout navProfile = findViewById(R.id.nav_profile);
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(MainMenuActivity.this, ProfileActivity.class));
        }

        View profileAvatar = findViewById(R.id.iv_profile);
        if (profileAvatar != null) {
            profileAvatar.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(MainMenuActivity.this, ProfileActivity.class));
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
