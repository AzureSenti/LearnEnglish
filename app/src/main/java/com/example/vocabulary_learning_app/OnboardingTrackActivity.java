package com.example.vocabulary_learning_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingTrackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_track);

        Intent done = new Intent(this, ProfileActivity.class);
        done.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        findViewById(R.id.button_get_started).setOnClickListener(v -> {
            startActivity(done);
            finish();
        });

        findViewById(R.id.button_skip).setOnClickListener(v -> {
            startActivity(done);
            finish();
        });
    }
}
