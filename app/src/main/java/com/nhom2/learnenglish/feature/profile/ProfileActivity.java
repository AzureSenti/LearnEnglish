package com.nhom2.learnenglish.feature.profile;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.UserEntity;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.core.util.SessionManager;
import com.nhom2.learnenglish.databinding.ActivityProfileBinding;
import com.nhom2.learnenglish.feature.grammar.GrammarRoadmapActivity;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;
import com.nhom2.learnenglish.feature.wordsets.LibraryActivity;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private AppDatabase database;
    private SessionManager sessionManager;
    private long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getCurrentUserId();
        database = AppDatabase.Companion.getInstance(this);

        initViews();
        loadUserProfileData();
        setupBottomNavigation();
    }

    private void initViews() {
        if (binding.rvBadges != null) {
            binding.rvBadges.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }

        if (binding.ivSettings != null) {
            binding.ivSettings.setOnClickListener(v -> {
                // TODO: Xử lý cài đặt hoặc đăng xuất
            });
        }
    }

    private void loadUserProfileData() {
        // Tạm ẩn phần gọi database để test UI vì nó thuộc core module
        /*
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                UserEntity user = database.userDao().getById(currentUserId);
                int masteredCount = 0;

                runOnUiThread(() -> {
                    if (user != null) {
                        binding.tvFullName.setText(user.getFullName());
                        binding.tvUserEmail.setText(user.getEmail() != null && !user.getEmail().isEmpty() ? user.getEmail() : "Chưa cập nhật email");
                        binding.tvStreakCount.setText(String.valueOf(user.getCurrentStreak()));
                        binding.tvWordsMasteredCount.setText(String.valueOf(masteredCount));
                        binding.tvStudyTimeCount.setText("0h");
                        binding.tvEnglishLevel.setText("C1 Advanced");
                    }
                });
            } catch (Exception e) {
                Log.e("ProfileActivity", "Lỗi tải dữ liệu người dùng", e);
            }
        });
        */
        
        // Gán dữ liệu giả để kiểm tra giao diện
        binding.tvUsername.setText("Tên Người Dùng Mẫu");
        binding.tvUserEmail.setText("nguoidung@example.com");
        binding.tvUserEmail.setVisibility(android.view.View.VISIBLE);
        binding.tvStreakCount.setText("15");
        binding.tvWordsMasteredCount.setText("450");
        binding.tvStudyTimeCount.setText("12h");
        binding.tvLevel.setText("C1 Advanced");
    }

    private void setupBottomNavigation() {
        // Điều hướng sang Explore
        if (binding.navExplore != null) {
            binding.navExplore.setOnClickListener(v -> Navigator.navigateTo(ProfileActivity.this, MainMenuActivity.class));
        }

        // Điều hướng sang Library
        if (binding.navLibrary != null) {
            binding.navLibrary.setOnClickListener(v -> Navigator.navigateTo(ProfileActivity.this, LibraryActivity.class));
        }

        // Điều hướng sang Learn (Ngữ pháp)
        if (binding.navLearn != null) {
            binding.navLearn.setOnClickListener(v -> Navigator.navigateTo(ProfileActivity.this, GrammarRoadmapActivity.class));
        }

        // Đang ở Profile nên không cần set listener cho navProfile
    }
}
