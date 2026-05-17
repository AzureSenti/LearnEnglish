package com.nhom2.learnenglish.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;
import com.nhom2.learnenglish.feature.wordsets.LibraryActivity;
import com.nhom2.learnenglish.feature.wordsets.WordSetDetailActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupBackNavigation();
        setupBottomNavigation();
        bindSampleData();
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveToExplore();
            }
        });
    }

    private void setupBottomNavigation() {
        LinearLayout navExplore = findViewById(R.id.nav_explore);
        if (navExplore != null) {
            navExplore.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(this, MainMenuActivity.class));
        }

        LinearLayout navLibrary = findViewById(R.id.nav_library);
        if (navLibrary != null) {
            navLibrary.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(this, LibraryActivity.class));
        }

        LinearLayout navLearn = findViewById(R.id.nav_learn);
        if (navLearn != null) {
            navLearn.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(this, WordSetDetailActivity.class));
        }

        // nav_profile is active on this screen; no navigation needed.
    }

    private void NavigatorMoveToLibrary() {
        Intent intent = new Intent(this, LibraryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void bindSampleData() {
        TextView tvUsername = findViewById(R.id.tv_username);
        TextView tvLevel = findViewById(R.id.tv_level);
        ImageView ivAvatar = findViewById(R.id.iv_avatar);

        if (tvUsername != null) {
            tvUsername.setText(getString(R.string.profile_sample_name));
        }
        if (tvLevel != null) {
            tvLevel.setText(getString(R.string.profile_level));
        }
        if (ivAvatar != null) {
            // keep xml src; no runtime change required
        }
    }

    private void moveToExplore() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
