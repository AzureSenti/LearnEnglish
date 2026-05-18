package com.nhom2.learnenglish.ui.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.util.BottomNavTab;
import com.nhom2.learnenglish.core.util.BottomNavigationHelper;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;

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
                moveToLearnHome();
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationHelper.setup(this, BottomNavTab.PROFILE);
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

    private void moveToLearnHome() {
        Navigator.INSTANCE.navigateTo(this, MainMenuActivity.class);
    }
}
