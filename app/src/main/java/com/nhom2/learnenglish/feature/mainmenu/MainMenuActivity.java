package com.nhom2.learnenglish.feature.mainmenu;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.mockdata.MockDataImport;
import com.nhom2.learnenglish.core.util.BottomNavTab;
import com.nhom2.learnenglish.core.util.BottomNavigationHelper;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.feature.wordsets.LibraryActivity;
import com.nhom2.learnenglish.ui.activity.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupWindowInsets();
        setupNavigation();
        setupMemoryChart();

        MockDataImport.INSTANCE.importIfNeeded(this, () -> { });
    }

    private void setupMemoryChart() {
        BarChart barChart = findViewById(R.id.memory_bar_chart);
        if (barChart == null) return;

        // 1. Dữ liệu mẫu (L1 to L7)
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 36f)); // L1
        entries.add(new BarEntry(1, 44f)); // L2
        entries.add(new BarEntry(2, 58f)); // L3
        entries.add(new BarEntry(3, 96f)); // L4 (Active)
        entries.add(new BarEntry(4, 72f)); // L5
        entries.add(new BarEntry(5, 84f)); // L6
        entries.add(new BarEntry(6, 64f)); // L7

        BarDataSet dataSet = new BarDataSet(entries, "Memory Mastery");
        
        // Thiết lập màu sắc (giả lập highlight cho L4)
        int colorInactive = ContextCompat.getColor(this, R.color.sanctuary_primary_blue_light);
        int colorActive = ContextCompat.getColor(this, R.color.sanctuary_primary_blue);
        
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            if (i == 3) colors.add(colorActive); // L4
            else colors.add(colorInactive);
        }
        dataSet.setColors(colors);
        dataSet.setDrawValues(false); // Không hiện số trên đầu cột

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f); // Độ rộng cột

        barChart.setData(data);

        // 2. Tùy chỉnh giao diện (Styling)
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setTouchEnabled(false); // Disable tương tác nếu chỉ muốn hiển thị

        // X-Axis (Các nhãn L1, L2...)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.sanctuary_text_secondary));
        final String[] labels = new String[]{"L1", "L2", "L3", "L4", "L5", "L6", "L7"};
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        // Y-Axis (Ẩn các trục và lưới)
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawLabels(false);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        barChart.invalidate(); // Refresh chart
        barChart.animateY(1000); // Hiệu ứng mọc cột
    }

    private void setupWindowInsets() {
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void setupNavigation() {
        MaterialButton btnViewAllGoals = findViewById(R.id.btn_view_all_goals);
        if (btnViewAllGoals != null) {
            btnViewAllGoals.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(MainMenuActivity.this, LibraryActivity.class));
        }

        BottomNavigationHelper.setup(this, BottomNavTab.LEARN);

        View profileAvatar = findViewById(R.id.iv_profile);
        if (profileAvatar != null) {
            profileAvatar.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(MainMenuActivity.this, ProfileActivity.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }
}
