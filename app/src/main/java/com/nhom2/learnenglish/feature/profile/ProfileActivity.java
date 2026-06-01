package com.nhom2.learnenglish.feature.profile;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.nhom2.learnenglish.core.data.local.AppDatabase;
import com.nhom2.learnenglish.core.data.local.entity.UserEntity;
import com.nhom2.learnenglish.core.util.AppExecutors;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.core.util.SessionManager;
import com.nhom2.learnenglish.databinding.ActivityProfileBinding;
import com.nhom2.learnenglish.feature.grammar.GrammarRoadmapActivity;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;
import com.nhom2.learnenglish.feature.profile.mock.StudyHistoryMockRepository;
import com.nhom2.learnenglish.feature.wordsets.LibraryActivity;
import com.nhom2.learnenglish.feature.auth.LoginActivity;
import android.widget.PopupMenu;
import android.content.Intent;
import android.view.MenuItem;
import com.nhom2.learnenglish.R;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.net.Uri;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.nhom2.learnenglish.core.network.RetrofitClient;
import com.nhom2.learnenglish.core.network.user.UserApi;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProfileActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> pickMedia;

    private ActivityProfileBinding binding;
    private AppDatabase database;
    private SessionManager sessionManager;
    private String currentUserId;
    private StudyHistoryMockRepository mockRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getCurrentUserId();
        database = AppDatabase.Companion.getInstance(this);
        mockRepo = new StudyHistoryMockRepository();

        initViews();
        loadUserProfileData();
        setupMockStatistics();
        setupSrsChart();
        setupBottomNavigation();
        
        pickMedia = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                uploadAvatar(uri);
            }
        });
    }

    private void initViews() {
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (binding.rvBadges != null) {
            binding.rvBadges.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }

        if (binding.ivUserAvatar != null) {
            binding.ivUserAvatar.setOnClickListener(v -> {
                pickMedia.launch("image/*");
            });
        }

        if (binding.ivEditName != null) {
            binding.ivEditName.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            });
        }

        if (binding.ivSettings != null) {
            binding.ivSettings.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(ProfileActivity.this, binding.ivSettings);
                popupMenu.getMenuInflater().inflate(R.menu.menu_profile_settings, popupMenu.getMenu());
                
                popupMenu.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.action_edit_profile) {
                        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                        startActivity(intent);
                        return true;
                    } else if (itemId == R.id.action_change_password) {
                        Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                        startActivity(intent);
                        return true;
                    } else if (itemId == R.id.action_logout) {
                        if (sessionManager != null) {
                            sessionManager.logout();
                        }
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    return false;
                });
                
                popupMenu.show();
            });
        }
    }

    private void loadUserProfileData() {
        AppExecutors.Companion.getInstance().getDiskIO().execute(() -> {
            try {
                UserEntity user = database.userDao().getByUserId(currentUserId);
                runOnUiThread(() -> {
                    if (user != null) {
                        binding.tvFullName.setText(user.getFullName());
                        binding.tvEnglishLevel.setText("English Learner");
                        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                            Glide.with(ProfileActivity.this)
                                .load(user.getAvatarUrl())
                                .placeholder(R.drawable.ic_profile)
                                .into(binding.ivUserAvatar);
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("ProfileActivity", "Lỗi tải dữ liệu người dùng", e);
            }
        });
    }

    private void setupMockStatistics() {
        // Streak
        binding.tvStreak.setText(String.valueOf(mockRepo.getCurrentStreak()));

        // Words Mastered (XP)
        binding.tvXp.setText(String.valueOf(mockRepo.getTotalXp()));

        // Study Time
        long totalMillis = mockRepo.getTotalStudyTime();
        long hours = TimeUnit.MILLISECONDS.toHours(totalMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalMillis) % 60;
        String timeText = (hours > 0) ? (hours + "h " + minutes + "m") : (minutes + "m");
        binding.tvStudyTime.setText(timeText);
    }

    private void setupSrsChart() {
        BarChart srsChart = binding.srsChart;
        List<Integer> levelData = mockRepo.getMockSrsChartData();
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < levelData.size(); i++) {
            entries.add(new BarEntry(i + 1, levelData.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Memory Levels");
        // Màu xanh chuẩn Design (#3D5CFF)
        dataSet.setColor(Color.parseColor("#3D5CFF"));
        dataSet.setValueTextColor(Color.parseColor("#858597"));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);
        srsChart.setData(barData);

        srsChart.getDescription().setEnabled(false);
        srsChart.getLegend().setEnabled(false);
        srsChart.setFitBars(true);
        srsChart.animateY(1000);
        srsChart.setDrawGridBackground(false);

        XAxis xAxis = srsChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setTextColor(Color.parseColor("#858597"));
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "L" + (int) value;
            }
        });

        srsChart.getAxisLeft().setDrawGridLines(false);
        srsChart.getAxisLeft().setAxisMinimum(0f);
        srsChart.getAxisLeft().setTextColor(Color.parseColor("#858597"));
        srsChart.getAxisRight().setEnabled(false);

        srsChart.invalidate();
    }

    private void setupBottomNavigation() {
        if (binding.navExplore != null) {
            binding.navExplore.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(this, MainMenuActivity.class));
        }

        if (binding.navLibrary != null) {
            binding.navLibrary.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(this, LibraryActivity.class));
        }

        if (binding.navLearn != null) {
            binding.navLearn.setOnClickListener(v -> Navigator.INSTANCE.navigateTo(this, GrammarRoadmapActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tự động đồng bộ dữ liệu khi có mạng
        com.nhom2.learnenglish.core.util.NetworkSyncManager.INSTANCE.syncIfOnline(this);
        // Tải lại profile sau khi sync
        loadUserProfileData();
    }

    private void uploadAvatar(Uri imageUri) {
        Toast.makeText(this, "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();
        AppExecutors.Companion.getInstance().getNetworkIO().execute(() -> {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                File tempFile = new File(getCacheDir(), "avatar.jpg");
                FileOutputStream outputStream = new FileOutputStream(tempFile);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                inputStream.close();

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), tempFile);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", tempFile.getName(), requestFile);

                String token = "Bearer " + sessionManager.fetchAuthToken();
                UserApi userApi = RetrofitClient.INSTANCE.getInstance().create(UserApi.class);

                retrofit2.Response<com.nhom2.learnenglish.core.network.user.UploadAvatarResponse> response = userApi.uploadAvatarSync(token, body).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String newAvatarUrl = response.body().getAvatarUrl();
                    
                    UserEntity user = database.userDao().getByUserId(currentUserId);
                    if (user != null) {
                        UserEntity updatedUser = new UserEntity(
                            user.getId(),
                            user.getUserId(),
                            user.getFullName(),
                            newAvatarUrl,
                            user.getEmail(),
                            user.getCoins(),
                            user.getCurrentStreak(),
                            user.getLongestStreak(),
                            user.isSynced()
                        );
                        database.userDao().update(updatedUser);
                    }
                    
                    runOnUiThread(() -> {
                        Toast.makeText(ProfileActivity.this, "Đổi ảnh đại diện thành công!", Toast.LENGTH_SHORT).show();
                        loadUserProfileData();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Lỗi khi upload ảnh", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}
