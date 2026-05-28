package com.nhom2.learnenglish.feature.profile;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.UserEntity;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.core.util.SessionManager;
import com.nhom2.learnenglish.databinding.ActivityProfileBinding;
import com.nhom2.learnenglish.feature.grammar.GrammarRoadmapActivity;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;
import com.nhom2.learnenglish.feature.profile.mock.StudyHistoryMockRepository;
import com.nhom2.learnenglish.feature.wordsets.LibraryActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private AppDatabase database;
    private SessionManager sessionManager;
    private String currentUserId;
    private StudyHistoryMockRepository mockRepo;

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
                // TODO: Xử lý cài đặt hoặc đăng xuất
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

        BarDataSet dataSet = new BarDataSet(entries, "Memory Levels");
        // Màu xanh chuẩn Design (#3D5CFF)
        dataSet.setColor(Color.parseColor("#3D5CFF"));
        dataSet.setValueTextColor(Color.parseColor("#858597"));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);
        srsChart.setData(barData);

        srsChart.getDescription().setEnabled(false);
        srsChart.getLegend().setEnabled(false);
        srsChart.setFitBars(true);
        srsChart.animateY(1000);
        srsChart.setDrawGridBackground(false);

        XAxis xAxis = srsChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setTextColor(Color.parseColor("#858597"));
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "L" + (int) value;
            }
        });

        srsChart.getAxisLeft().setDrawGridLines(false);
        srsChart.getAxisLeft().setAxisMinimum(0f);
        srsChart.getAxisLeft().setTextColor(Color.parseColor("#858597"));
        srsChart.getAxisRight().setEnabled(false);

        srsChart.invalidate();
    }

    private void setupBottomNavigation() {
        if (binding.navExplore != null) {
            binding.navExplore.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(this, MainMenuActivity.class));
        }

        if (binding.navLibrary != null) {
            binding.navLibrary.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(this, LibraryActivity.class));
        }

        if (binding.navLearn != null) {
            binding.navLearn.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(this, GrammarRoadmapActivity.class));
        }
    }
}
