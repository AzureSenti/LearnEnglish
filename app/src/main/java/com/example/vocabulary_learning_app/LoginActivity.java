package com.example.vocabulary_learning_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.link_signup).setOnClickListener(
                v -> startActivity(new Intent(this, SignUpActivity.class)));

        findViewById(R.id.button_login).setOnClickListener(
                v -> {
                    startActivity(new Intent(this, OnboardingExploreActivity.class));
                    finish();
                });
    }
}
