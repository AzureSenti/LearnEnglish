package com.nhom2.learnenglish.feature.profile;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.feature.grammar.GrammarRoadmapActivity;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;
import com.nhom2.learnenglish.feature.wordsets.LibraryActivity;
import com.nhom2.learnenglish.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getCurrentUserId();
        database = AppDatabase.Companion.getInstance(this);
        mockRepo = new StudyHistoryMockRepository();

        initViews();
        loadUserProfileData();
        setupMockStatistics();
        setupSrsChart();
        setupBottomNavigation();
    }

    private void initViews() {
        if (binding.rvBadges != null) {
            binding.rvBadges.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }

        if (binding.ivSettings != null) {
            binding.ivSettings.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            });
        }
    }

    private void loadUserProfileData() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                UserEntity user = database.userDao().getById(currentUserId);
                runOnUiThread(() -> {
                    if (user != null) {
                        binding.tvFullName.setText(user.getFullName());
                        binding.tvEnglishLevel.setText("English Learner");
                    }
                });
            } catch (Exception e) {
                Log.e("ProfileActivity", "Lỗi tải dữ liệu người dùng", e);
            }
        });
    }

    private void setupMockStatistics() {
        // Streak
        binding.tvStreak.setText(String.valueOf(mockRepo.getCurrentStreak()));

        // Words Mastered (XP)
        binding.tvXp.setText(String.valueOf(mockRepo.getTotalXp()));

        // Study Time
        long totalMillis = mockRepo.getTotalStudyTime();
        long hours = TimeUnit.MILLISECONDS.toHours(totalMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalMillis) % 60;
        String timeText = (hours > 0) ? (hours + "h " + minutes + "m") : (minutes + "m");
        binding.tvStudyTime.setText(timeText);
    }

    private void setupSrsChart() {
        BarChart srsChart = binding.srsChart;
        List<Integer> levelData = mockRepo.getMockSrsChartData();
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < levelData.size(); i++) {
            entries.add(new BarEntry(i + 1, levelData.get(i)));
        }

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        binding.navExplore.setOnClickListener(v -> Navigator.navigateTo(this, MainMenuActivity.class));
        binding.navLibrary.setOnClickListener(v -> Navigator.navigateTo(this, LibraryActivity.class));
        binding.navLearn.setOnClickListener(v -> Navigator.navigateTo(this, GrammarRoadmapActivity.class));
        // Đang ở Profile nên không cần set listener cho navProfile
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tự động đồng bộ dữ liệu khi có mạng
        com.nhom2.learnenglish.core.util.NetworkSyncManager.INSTANCE.syncIfOnline(this);
    }
}
