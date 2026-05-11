package com.example.vocabulary_learning_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingExploreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_explore);

        Intent next = new Intent(this, OnboardingSpacedActivity.class);

        findViewById(R.id.button_next).setOnClickListener(v -> {
            startActivity(next);
            finish();
        });

        findViewById(R.id.button_skip).setOnClickListener(v -> {
            startActivity(next);
            finish();
        });
    }
}
