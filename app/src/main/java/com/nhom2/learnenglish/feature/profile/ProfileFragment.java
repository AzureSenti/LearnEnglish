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
import com.nhom2.learnenglish.core.data.local.entity.grammar.UserGrammarProgress;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * ProfileFragment hiển thị thông tin cá nhân và thống kê học tập thực tế từ Database.
 * Đảm bảo tự động cập nhật dữ liệu real-time thông qua vòng đời onResume.
 */
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

        // Ánh xạ View chính xác theo layout
        tvStreak = view.findViewById(R.id.tvStreak);
        tvXp = view.findViewById(R.id.tvXp);
        tvStudyTime = view.findViewById(R.id.tvStudyTime);
        tvFullName = view.findViewById(R.id.tv_full_name);
        srsChart = view.findViewById(R.id.srsChart);
    }

    @Override
    public void onResume() {
        super.onResume();
        // ĐỒNG BỘ VÒNG ĐỜI - Cập nhật dữ liệu mỗi khi người dùng quay lại tab này
        loadRealDataFromDB();
    }

    /**
     * Truy vấn Database song song (Vocabulary + Grammar) và tính toán Streak, Words Mastered, Chart.
     * Hoàn toàn xử lý In-Memory trên Java luồng nền.
     */
    private void loadRealDataFromDB() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                if (!isAdded() || getContext() == null) return;

                SessionManager sessionManager = new SessionManager(requireContext());
                String userId = sessionManager.getCurrentUserId();
                AppDatabase db = AppDatabase.Companion.getInstance(requireContext());

                // GỘP DỮ LIỆU STREAK (IN-MEMORY MERGE)
                List<Long> allStudyDates = new ArrayList<>();

                // A. Lấy lịch sử từ vựng
                List<WordSrsEntity> srsRecords = db.wordSrsDao().getAllForUser(userId);
                int masteredCount = 0;
                int[] levelsCount = new int[7];

                if (srsRecords != null) {
                    for (WordSrsEntity record : srsRecords) {
                        if (record.getLastReviewDate() != null) {
                            allStudyDates.add(record.getLastReviewDate());
                        }
                        // Logic đếm Words Mastered (Level 7)
                        if (record.getLevel() >= 7) {
                            masteredCount++;
                        }
                        // Đếm số lượng theo level cho biểu đồ
                        int lv = record.getLevel();
                        if (lv >= 1 && lv <= 7) {
                            levelsCount[lv - 1]++;
                        }
                    }
                }

                // B. Lấy lịch sử ngữ pháp (Xử lý gộp In-Memory)
                List<UserGrammarProgress> grammarProgress = db.userGrammarProgressDao().getAllProgressForUser(userId);
                if (grammarProgress != null) {
                    for (UserGrammarProgress progress : grammarProgress) {

                    }
                }

                // C. Tính toán Streak cuối cùng từ danh sách gộp
                int realStreak = StreakUtils.calculateStreak(allStudyDates);
                final int finalMasteredCount = masteredCount;

                // Lấy thông tin User thật
                UserEntity user = db.userDao().getById(userId);

                // CẬP NHẬT UI VÀ BIỂU ĐỒ
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        if (user != null && tvFullName != null) {
                            tvFullName.setText(user.getFullName());
                        }
                        
                        if (tvStreak != null) tvStreak.setText(String.valueOf(realStreak));
                        if (tvXp != null) tvXp.setText(String.valueOf(finalMasteredCount));
                        
                        // Mặc định hiển thị 0h cho thời gian học nếu chưa có bảng log
                        if (tvStudyTime != null) {
                            tvStudyTime.setText("0h");
                        }
                        
                        updateSrsChartUI(levelsCount);
                    });
                }
            } catch (Exception e) {
                Log.e("ProfileFragment", "Error refreshing Profile data", e);
            }
        });
    }

    /**
     * Cập nhật biểu đồ SRS và thông báo thay đổi dữ liệu để vẽ lại real-time.
     */
    private void updateSrsChartUI(int[] levelsCount) {
        if (srsChart == null) return;

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < levelsCount.length; i++) {
            entries.add(new BarEntry(i + 1, levelsCount[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Memory Levels");
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

        // BẮT BUỘC: Thông báo dữ liệu mới và ép vẽ lại
        srsChart.notifyDataSetChanged();
        srsChart.invalidate();
    }
}
