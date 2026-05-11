package com.example.vocabulary_learning_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViewById(R.id.button_settings).setOnClickListener(
                v -> startActivity(new Intent(this, StatisticsActivity.class)));
    }
}
