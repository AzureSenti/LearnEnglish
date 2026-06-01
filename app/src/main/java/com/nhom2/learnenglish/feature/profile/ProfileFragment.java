package com.nhom2.learnenglish.feature.profile;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.UserEntity;
import com.nhom2.learnenglish.core.data.local.entity.word.WordSrsEntity;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.SessionManager;
import com.nhom2.learnenglish.databinding.FragmentProfileBinding;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật dữ liệu từ database mỗi khi Fragment hiển thị
        loadRealDataFromDB();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Truy vấn dữ liệu thực tế từ Database để tính toán Streak, Words Mastered và Biểu đồ SRS.
     */
    private void loadRealDataFromDB() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                if (binding == null || !isAdded()) return;
                
                SessionManager sessionManager = new SessionManager(requireContext());
                String userId = sessionManager.getCurrentUserId();
                AppDatabase db = AppDatabase.Companion.getInstance(requireContext());

                // 1. Lấy thông tin người dùng
                UserEntity user = db.userDao().getByUserId(userId);

                // 2. Lấy dữ liệu SRS của từ vựng cho Streak, Mastered count và Chart
                List<WordSrsEntity> srsRecords = db.wordSrsDao().getAllForUser(userId);
                
                List<Long> studyTimestamps = new ArrayList<>();
                int[] levelsCount = new int[7]; // Mảng đếm từ level 1 đến 7

                if (srsRecords != null) {
                    for (WordSrsEntity record : srsRecords) {
                        // Thu thập ngày ôn tập cho Streak
                        if (record.getLastReviewDate() != null) {
                            studyTimestamps.add(record.getLastReviewDate());
                        }
                        
                        // Thống kê level cho biểu đồ (L1 - L7)
                        int lv = record.getLevel();
                        if (lv >= 1 && lv <= 7) {
                            levelsCount[lv - 1]++;
                        }
                    }
                }

                // 3. Tính toán Streak thực tế bằng tiện ích đã có
                int realStreak = StreakUtils.calculateStreak(studyTimestamps);
                
                // 4. Đếm tổng số từ vựng (Words Mastered / Total Words)
                int totalWordsCount = srsRecords != null ? srsRecords.size() : 0;

                // 5. Cập nhật giao diện trên UI Thread
                if (isAdded() && binding != null) {
                    requireActivity().runOnUiThread(() -> {
                        if (binding == null) return;

                        // Set tên người dùng
                        if (user != null) {
                            binding.tvFullName.setText(user.getFullName());
                        }

                        // Set chuỗi ngày học (Streak)
                        binding.tvStreak.setText(String.valueOf(realStreak));

                        // Set tổng số từ (Words Mastered)
                        binding.tvXp.setText(String.valueOf(totalWordsCount));

                        // Set thời gian học (Mặc định 0h nếu chưa có log thời gian thật trong DB)
                        binding.tvStudyTime.setText("0h");
                        
                        // Cập nhật biểu đồ SRS với dữ liệu thật
                        updateChartUI(levelsCount);
                    });
                }
            } catch (Exception e) {
                Log.e("ProfileFragment", "Lỗi nạp dữ liệu Profile thực tế", e);
            }
        });
    }

    private void updateChartUI(int[] levelsCount) {
        if (binding == null || binding.srsChart == null) return;

        ArrayList<BarEntry> entries = new ArrayList<>();
        // Đổ dữ liệu thật từ database vào biểu đồ
        for (int i = 0; i < levelsCount.length; i++) {
            entries.add(new BarEntry(i + 1, levelsCount[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Memory Levels");
        // Màu xanh chuẩn thiết kế (#3D5CFF)
        dataSet.setColor(Color.parseColor("#3D5CFF"));
        dataSet.setValueTextColor(Color.parseColor("#858597"));
        dataSet.setValueTextSize(10f);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        BarChart srsChart = binding.srsChart;
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

        // Buộc biểu đồ cập nhật lại giao diện
        srsChart.notifyDataSetChanged();
        srsChart.invalidate();
    }
}
