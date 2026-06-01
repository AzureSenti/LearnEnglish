package com.nhom2.learnenglish.feature.profile;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.UserEntity;
import com.nhom2.learnenglish.core.data.local.entity.word.WordSrsEntity;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView tvStreak, tvXp, tvStudyTime, tvFullName;
    private BarChart srsChart;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ View
        tvStreak = view.findViewById(R.id.tvStreak);
        tvXp = view.findViewById(R.id.tvXp);
        tvStudyTime = view.findViewById(R.id.tvStudyTime);
        tvFullName = view.findViewById(R.id.tv_full_name);
        srsChart = view.findViewById(R.id.srsChart);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật dữ liệu từ database mỗi khi Fragment hiển thị
        loadRealDataFromDB();
    }

    /**
     * Truy vấn dữ liệu thực tế từ Database để tính toán Streak, Words Mastered và Biểu đồ SRS.
     */
    private void loadRealDataFromDB() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                if (!isAdded()) return;
                
                SessionManager sessionManager = new SessionManager(requireContext());
                String userId = sessionManager.getCurrentUserId();
                AppDatabase db = AppDatabase.Companion.getInstance(requireContext());

                // 1. Lấy thông tin người dùng
                UserEntity user = db.userDao().getByUserId(userId);

                // 2. Lấy dữ liệu SRS của từ vựng cho Streak, Mastered count và Chart
                List<WordSrsEntity> srsRecords = db.wordSrsDao().getAllForUser(userId);
                
                List<Long> studyTimestamps = new ArrayList<>();
                int masteredCount = 0;
                int[] levelsCount = new int[7]; // Mảng đếm từ level 1 đến 7

                if (srsRecords != null) {
                    for (WordSrsEntity record : srsRecords) {
                        // Thu thập ngày ôn tập cho Streak
                        if (record.getLastReviewDate() != null) {
                            studyTimestamps.add(record.getLastReviewDate());
                        }
                        
                        // Level >= 7 được coi là Mastered
                        if (record.getLevel() >= 7) {
                            masteredCount++;
                        }
                        
                        // Thống kê level cho biểu đồ
                        int lv = record.getLevel();
                        if (lv >= 1 && lv <= 7) {
                            levelsCount[lv - 1]++;
                        }
                    }
                }

                // 3. Tính toán Streak thực tế
                int realStreak = StreakUtils.calculateStreak(studyTimestamps);
                final int finalMasteredCount = masteredCount;

                // 4. Cập nhật giao diện trên UI Thread
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        if (user != null && tvFullName != null) {
                            tvFullName.setText(user.getFullName());
                        }
                        if (tvStreak != null) {
                            tvStreak.setText(String.valueOf(realStreak));
                        }
                        if (tvXp != null) {
                            tvXp.setText(String.valueOf(finalMasteredCount));
                        }
                        // Hiện tại chưa có hệ thống log thời gian học nên mặc định 0h
                        if (tvStudyTime != null) {
                            tvStudyTime.setText("0h");
                        }
                        
                        updateChartUI(levelsCount);
                    });
                }
            } catch (Exception e) {
                Log.e("ProfileFragment", "Lỗi nạp dữ liệu Profile", e);
            }
        });
    }

    private void updateChartUI(int[] levelsCount) {
        if (srsChart == null) return;

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < levelsCount.length; i++) {
            entries.add(new BarEntry(i + 1, levelsCount[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Memory Levels");
        // Màu xanh chuẩn thiết kế (#3D5CFF)
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

        // Thông báo dữ liệu đã thay đổi và vẽ lại
        srsChart.notifyDataSetChanged();
        srsChart.invalidate();
    }
}
