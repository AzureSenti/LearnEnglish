package com.nhom2.learnenglish.feature.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AuthPreferences;
import com.nhom2.learnenglish.feature.onboarding.OnboardingActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        CheckBox cbRemember = findViewById(R.id.check_remember);
        Button btnLogin = findViewById(R.id.button_login);
        TextView txtSignUp = findViewById(R.id.link_signup);
        TextView txtForgot = findViewById(R.id.link_forgot_password);

        btnLogin.setOnClickListener(v -> attemptLogin(cbRemember.isChecked()));
        txtSignUp.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        txtForgot.setOnClickListener(v ->
                Toast.makeText(this, R.string.toast_forgot_soon, Toast.LENGTH_SHORT).show());
    }

    private void attemptLogin(boolean remember) {
        String email = inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "";
        String password = inputPassword.getText() != null ? inputPassword.getText().toString() : "";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.error_fill_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!email.contains("@")) {
            Toast.makeText(this, R.string.error_email_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            AuthPreferences.INSTANCE.saveSession(this, remember);
            openOnboarding();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void openOnboarding() {
        try {
            Intent intent = new Intent(this, OnboardingActivity.class);
            // Thay đổi flag để debug - không clear task ngay
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to open onboarding: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
