package com.nhom2.learnenglish.feature.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_onboarding);

            viewPager = findViewById(R.id.view_pager_onboarding);

            OnboardingAdapter adapter = new OnboardingAdapter(this);
            viewPager.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, quay về MainActivity
            Intent intent = new Intent(this, com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Hàm này sẽ được gọi từ giao diện xml khi bấm nút Next
    public void onNextClick(android.view.View view) {
        goToNextPage();
    }

    public void goToNextPage() {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem < 2) {
            viewPager.setCurrentItem(currentItem + 1);
        }
    }

    // Hàm này sẽ được gọi từ giao diện xml khi bấm nút Get Started
    public void onGetStartedClick(android.view.View view) {
        goToMainMenu();
    }

    public void goToMainMenu() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}