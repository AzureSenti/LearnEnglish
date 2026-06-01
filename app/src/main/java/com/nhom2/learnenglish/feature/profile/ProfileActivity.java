package com.nhom2.learnenglish.feature.profile;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.databinding.ActivityProfileBinding;
import com.nhom2.learnenglish.feature.grammar.GrammarRoadmapActivity;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;
import com.nhom2.learnenglish.feature.wordsets.LibraryActivity;

/**
 * ProfileActivity đóng vai trò là container chứa ProfileFragment.
 * Mọi logic hiển thị dữ liệu thực tế đã được chuyển sang ProfileFragment.
 */
public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Nạp ProfileFragment vào container
        // Kiểm tra id profile_container trong activity_profile.xml
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.profile_container, new ProfileFragment())
                    .commit();
        }

        // 2. Thiết lập thanh điều hướng
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        // Sử dụng camelCase cho IDs (nav_explore -> navExplore) theo chuẩn ViewBinding
        if (binding.navExplore != null) {
            binding.navExplore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigator.navigateTo(ProfileActivity.this, MainMenuActivity.class);
                    ProfileActivity.this.overridePendingTransition(0, 0);
                    ProfileActivity.this.finish();
                }
            });
        }
        if (binding.navLibrary != null) {
            binding.navLibrary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigator.navigateTo(ProfileActivity.this, LibraryActivity.class);
                    ProfileActivity.this.overridePendingTransition(0, 0);
                    ProfileActivity.this.finish();
                }
            });
        }
        if (binding.navLearn != null) {
            binding.navLearn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigator.navigateTo(ProfileActivity.this, GrammarRoadmapActivity.class);
                    ProfileActivity.this.overridePendingTransition(0, 0);
                    ProfileActivity.this.finish();
                }
            });
        }
        // Đang ở tab Profile
        if (binding.navProfile != null) {
            binding.navProfile.setOnClickListener(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tự động đồng bộ dữ liệu qua NetworkSyncManager
        try {
            com.nhom2.learnenglish.core.util.NetworkSyncManager.INSTANCE.syncIfOnline(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
