package com.nhom2.learnenglish.feature.auth;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.UserEntity;
import com.nhom2.learnenglish.core.data.local.mockdata.MockDataImport;
import com.nhom2.learnenglish.core.data.repository.UserRepository;
import com.nhom2.learnenglish.core.network.auth.AuthApi;
import com.nhom2.learnenglish.core.network.RetrofitClient;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.core.util.SessionManager;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText inputEmail, inputPassword;
    private MaterialButton buttonLogin;
    private MaterialCheckBox checkRemember;
    private TextView linkForgotPassword, linkSignup, useWithoutLogin;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initViews();
        setupData();
        setupClickListeners();
    }

    private void initViews() {
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        buttonLogin = findViewById(R.id.button_login);
        checkRemember = findViewById(R.id.check_remember);
        linkForgotPassword = findViewById(R.id.link_forgot_password);
        linkSignup = findViewById(R.id.link_signup);
        useWithoutLogin = findViewById(R.id.use_without_login);
    }

    private void setupData() {
        SessionManager sessionManager = new SessionManager(this);
        AuthApi authApi = RetrofitClient.INSTANCE.getInstance().create(AuthApi.class);
        AppDatabase db = AppDatabase.Companion.getInstance(this);

        // Tạo SyncRepository cho đồng bộ dữ liệu học tập
        com.nhom2.learnenglish.core.network.sync.SyncApi syncApi = RetrofitClient.INSTANCE.getSyncApi();
        com.nhom2.learnenglish.core.data.repository.SyncRepository syncRepository =
                new com.nhom2.learnenglish.core.data.repository.SyncRepository(
                        syncApi,
                        db.wordDao(),
                        db.wordSetDao(),
                        db.wordSetCrossDao(),
                        db.wordSrsDao(),
                        db.userGrammarProgressDao(),
                        db.userWordSetDao(),
                        db.userDao(),
                        db.deletedSyncItemDao(),
                        sessionManager,
                        AppExecutors.Companion.getInstance()
                );

        userRepository = new UserRepository(
                AppExecutors.Companion.getInstance(),
                db.userDao(),
                authApi,
                sessionManager,
                syncRepository
        );

        MockDataImport.INSTANCE.importIfNeeded(this);
    }

    private void setupClickListeners() {
        // Nút Đăng nhập
        buttonLogin.setOnClickListener(v -> performLogin());

        // Nút Quên mật khẩu
        linkForgotPassword.setOnClickListener(v -> {
            Toasty.normal(this, "Chuyển sang trang Quên mật khẩu", Toast.LENGTH_SHORT).show();
            // TODO: Mở ForgotPasswordActivity
        });

        // Nút Đăng ký
        linkSignup.setOnClickListener(v -> {
            Navigator.navigateTo(this, SignUpActivity.class);
        });
        
        // Nút dùng mà không cần đăng nhập
        useWithoutLogin.setOnClickListener(v -> {
            Toasty.info(this, "Đang xử lý đăng nhập ẩn...", Toast.LENGTH_SHORT, true).show();
            activateGuestMode();
        });

    }

    private void performLogin() {
        String email = inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "";
        String password = inputPassword.getText() != null ? inputPassword.getText().toString().trim() : "";
        boolean isRememberMe = checkRemember.isChecked();



        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ Email và Mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        buttonLogin.setEnabled(false);
        buttonLogin.setText("ĐANG XỬ LÝ...");
        buttonLogin.setAlpha(0.7f);


        AppExecutors.Companion.getInstance().getNetworkIO().execute(() -> {
            try {
                UserEntity user = kotlinx.coroutines.BuildersKt.runBlocking(
                        kotlin.coroutines.EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> userRepository.login(email, password, continuation)
                );

                runOnUiThread(() -> {

                    Toast.makeText(LoginActivity.this, "Chào mừng trở lại!", Toast.LENGTH_SHORT).show();
                    Navigator.navigateTo(this, MainMenuActivity.class);
                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    buttonLogin.setEnabled(true);
                    buttonLogin.setText(R.string.login_action);
                    buttonLogin.setAlpha(1.0f);

                    Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không đúng!", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void activateGuestMode() {
        SessionManager sessionManager = new SessionManager(this);
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                userRepository.ensureLocalUserExists();

                sessionManager.activateGuestMode();

                runOnUiThread(() -> {
                    Toast.makeText(this, "Chế độ ngoại tuyến: Tiến độ sẽ lưu tại máy", Toast.LENGTH_LONG).show();
                    Navigator.navigateTo(this, MainMenuActivity.class);

                    finish();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Lỗi hệ thống: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
