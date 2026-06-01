package com.nhom2.learnenglish.feature.auth;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.UserEntity;
import com.nhom2.learnenglish.core.data.repository.SyncRepository;
import com.nhom2.learnenglish.core.data.repository.UserRepository;
import com.nhom2.learnenglish.core.network.RetrofitClient;
import com.nhom2.learnenglish.core.network.auth.AuthApi;
import com.nhom2.learnenglish.core.network.sync.SyncApi;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.core.util.SessionManager;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;

import es.dmoral.toasty.Toasty;
import kotlinx.coroutines.BuildersKt;
import kotlin.coroutines.EmptyCoroutineContext;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText inputUsername, inputFullname, inputPassword, inputEmail;
    private MaterialButton buttonSignup;
    private TextView linkLogin;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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

        inputUsername = findViewById(R.id.input_username);
        inputFullname = findViewById(R.id.input_fullname);
        inputEmail = findViewById(R.id.input_email_signup);
        inputPassword = findViewById(R.id.input_password_signup);
        buttonSignup = findViewById(R.id.button_signup);
        linkLogin = findViewById(R.id.link_login);
    }

    private void setupData() {
        SessionManager sessionManager = new SessionManager(this);
        AuthApi authApi = RetrofitClient.INSTANCE.getInstance().create(AuthApi.class);
        AppDatabase db = AppDatabase.Companion.getInstance(this);

        SyncApi syncApi = RetrofitClient.INSTANCE.getSyncApi();
        SyncRepository syncRepository = new SyncRepository(
                syncApi,
                db.wordDao(),
                db.wordSetDao(),
                db.wordSetCrossDao(),
                db.wordSrsDao(),
                db.userGrammarProgressDao(),
                db.userWordSetDao(),
                db.userDao(),
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
    }

    private void setupClickListeners() {
        buttonSignup.setOnClickListener(v -> performSignup());

        linkLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void performSignup() {
        String username = inputUsername.getText() != null ? inputUsername.getText().toString().trim() : "";
        String fullname = inputFullname.getText() != null ? inputFullname.getText().toString().trim() : "";
        String email = inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "";
        String password = inputPassword.getText() != null ? inputPassword.getText().toString().trim() : "";

        if (username.isEmpty() || fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        buttonSignup.setEnabled(false);
        buttonSignup.setText("ĐANG XỬ LÝ...");
        buttonSignup.setAlpha(0.7f);

        AppExecutors.Companion.getInstance().getNetworkIO().execute(() -> {
            try {
                UserEntity user = BuildersKt.runBlocking(
                        EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> userRepository.register(email, password, fullname, username, continuation)
                );

                runOnUiThread(() -> {
                    Toast.makeText(SignUpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    Navigator.navigateTo(this, MainMenuActivity.class);
                    finishAffinity();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    buttonSignup.setEnabled(true);
                    buttonSignup.setText(R.string.signup_button);
                    buttonSignup.setAlpha(1.0f);
                    Toasty.error(SignUpActivity.this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_LONG, true).show();
                });
            }
        });
    }
}
