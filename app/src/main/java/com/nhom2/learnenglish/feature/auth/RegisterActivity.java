package com.nhom2.learnenglish.feature.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.feature.onboarding.OnboardingActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        EditText inputUsername = findViewById(R.id.input_username);
        EditText inputEmail = findViewById(R.id.input_email_signup);
        EditText inputPassword = findViewById(R.id.input_password_signup);
        TextView errorEmail = findViewById(R.id.text_email_error);
        Button btnSignUp = findViewById(R.id.button_signup);
        TextView txtLogin = findViewById(R.id.link_login);

        errorEmail.setVisibility(View.GONE);
        inputEmail.setText("");

        btnSignUp.setOnClickListener(v -> attemptRegister(
                inputUsername.getText() != null ? inputUsername.getText().toString().trim() : "",
                inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "",
                inputPassword.getText() != null ? inputPassword.getText().toString() : ""));
        txtLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegister(String username, String email, String password) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.error_fill_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!email.contains("@")) {
            Toast.makeText(this, R.string.error_email_invalid, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 8) {
            Toast.makeText(this, R.string.error_password_short, Toast.LENGTH_SHORT).show();
            return;
        }

        openOnboarding();
    }

    private void openOnboarding() {
        Intent intent = new Intent(this, OnboardingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
