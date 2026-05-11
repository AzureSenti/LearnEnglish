package com.example.vocabulary_learning_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent toLogin = new Intent(this, LoginActivity.class);
        toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        findViewById(R.id.link_login).setOnClickListener(v -> {
            startActivity(toLogin);
            finish();
        });

        findViewById(R.id.button_signup).setOnClickListener(v -> {
            startActivity(new Intent(this, OnboardingExploreActivity.class));
            finish();
        });
    }
}
