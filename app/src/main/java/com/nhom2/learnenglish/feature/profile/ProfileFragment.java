package com.nhom2.learnenglish.feature.profile;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.nhom2.learnenglish.feature.profile.mock.StudyHistoryMockRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProfileFragment extends Fragment {

    private TextView tvStreak, tvXp, tvStudyTime, tvFullName;
    private BarChart srsChart;
    private StudyHistoryMockRepository mockRepo;

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

        mockRepo = new StudyHistoryMockRepository();

        setupStatistics();
        setupSrsChart();
    }

    private void setupStatistics() {
        if (mockRepo == null) return;

        tvStreak.setText(String.valueOf(mockRepo.getCurrentStreak()));
        tvXp.setText(String.valueOf(mockRepo.getTotalXp()));
        
        if (tvFullName != null) {
            tvFullName.setText("Local User");
        }

        long totalMillis = mockRepo.getTotalStudyTime();
        long hours = TimeUnit.MILLISECONDS.toHours(totalMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalMillis) % 60;

        String timeFormatted = (hours > 0) ? (hours + "h " + minutes + "m") : (minutes + "m");
        tvStudyTime.setText(timeFormatted);
    }

    private void setupSrsChart() {
        if (srsChart == null || mockRepo == null) return;

        List<Integer> levelData = mockRepo.getMockSrsChartData();
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < levelData.size(); i++) {
            entries.add(new BarEntry(i + 1, levelData.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Memory Levels");
        // Màu xanh chủ đạo của design (#3D5CFF)
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
}
