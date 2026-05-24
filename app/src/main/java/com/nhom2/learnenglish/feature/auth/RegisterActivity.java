package com.nhom2.learnenglish.feature.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.UserEntity;
import com.nhom2.learnenglish.core.data.repository.UserRepository;
import com.nhom2.learnenglish.core.network.Auth.AuthApi;
import com.nhom2.learnenglish.core.network.RetrofitClient;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.SessionManager;
import com.nhom2.learnenglish.feature.onboarding.OnboardingActivity;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;

public class RegisterActivity extends AppCompatActivity {
    
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        setupData();

        EditText inputUsername = findViewById(R.id.input_username);
        EditText inputEmail = findViewById(R.id.input_email_signup);
        EditText inputPassword = findViewById(R.id.input_password_signup);
        TextView errorEmail = findViewById(R.id.text_email_error);
        MaterialButton btnSignUp = findViewById(R.id.button_signup);
        MaterialButton txtLogin = findViewById(R.id.link_login);

        if (errorEmail != null) errorEmail.setVisibility(View.GONE);
        if (inputEmail != null) inputEmail.setText("");

        btnSignUp.setOnClickListener(v -> attemptRegister(
                inputUsername.getText() != null ? inputUsername.getText().toString().trim() : "",
                inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "",
                inputPassword.getText() != null ? inputPassword.getText().toString() : "",
                btnSignUp));
        txtLogin.setOnClickListener(v -> finish());
    }

    private void setupData() {
        SessionManager sessionManager = new SessionManager(this);
        AppDatabase db = AppDatabase.Companion.getInstance(this);
        AuthApi authApi = RetrofitClient.INSTANCE.getInstance().create(AuthApi.class);

        userRepository = new UserRepository(
                AppExecutors.Companion.getInstance(),
                db.userDao(),
                authApi,
                sessionManager
        );
    }

    private void attemptRegister(String username, String email, String password, MaterialButton btnSignUp) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.error_fill_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, R.string.error_email_invalid, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 8) {
            Toast.makeText(this, R.string.error_password_short, Toast.LENGTH_SHORT).show();
            return;
        }

        btnSignUp.setEnabled(false);
        btnSignUp.setText("ĐANG XỬ LÝ...");

        AppExecutors.Companion.getInstance().getNetworkIO().execute(() -> {
            try {
                // GIẢ LẬP: Chờ 2 giây để xem UI
                Thread.sleep(2000);

                // Tạm thời comment vì UserRepository chưa có hàm register
                /*
                UserEntity user = (UserEntity) BuildersKt.runBlocking(
                        EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> userRepository.register(email, password, username, true, (Continuation<? super UserEntity>) continuation)
                );
                */

                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "GIẢ LẬP: Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    openOnboarding();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    btnSignUp.setEnabled(true);
                    btnSignUp.setText(R.string.signup_action);
                    Toast.makeText(RegisterActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void openOnboarding() {
        Intent intent = new Intent(this, OnboardingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
