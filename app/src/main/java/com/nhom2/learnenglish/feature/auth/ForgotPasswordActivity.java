package com.nhom2.learnenglish.feature.auth;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.repository.UserRepository;
import com.nhom2.learnenglish.core.network.Auth.AuthApi;
import com.nhom2.learnenglish.core.network.RetrofitClient;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.SessionManager;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText inputEmail;
    private MaterialButton buttonReset;
    private MaterialButton buttonBack;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        inputEmail = findViewById(R.id.input_email_forgot);
        buttonReset = findViewById(R.id.button_reset_password);
        buttonBack = findViewById(R.id.button_back_to_login);

        setupData();

        buttonReset.setOnClickListener(v -> performReset());
        buttonBack.setOnClickListener(v -> finish());
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

    private void performReset() {
        String email = inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "";

        if (email.isEmpty() || !email.contains("@")) {
            Toast.makeText(this, R.string.error_email_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        buttonReset.setEnabled(false);
        buttonReset.setText("ĐANG XỬ LÝ...");

        AppExecutors.Companion.getInstance().getNetworkIO().execute(() -> {
            try {
                kotlinx.coroutines.BuildersKt.runBlocking(
                        kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> userRepository.resetPassword(email, continuation)
                );

                runOnUiThread(() -> {
                    Toast.makeText(ForgotPasswordActivity.this, "Vui lòng kiểm tra email để đặt lại mật khẩu.", Toast.LENGTH_LONG).show();
                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    buttonReset.setEnabled(true);
                    buttonReset.setText("Gửi yêu cầu");
                    Toast.makeText(ForgotPasswordActivity.this, "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
