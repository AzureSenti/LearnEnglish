package com.nhom2.learnenglish.feature.profile;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.feature.grammar.GrammarRoadmapActivity;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;
import com.nhom2.learnenglish.feature.wordsets.LibraryActivity;
import com.nhom2.learnenglish.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Nạp ProfileFragment (Giao diện đẹp và logic thực tế) vào Activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.profile_container, new ProfileFragment())
                    .commit();
        }

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        binding.navExplore.setOnClickListener(v -> Navigator.navigateTo(this, MainMenuActivity.class));
        binding.navLibrary.setOnClickListener(v -> Navigator.navigateTo(this, LibraryActivity.class));
        binding.navLearn.setOnClickListener(v -> Navigator.navigateTo(this, GrammarRoadmapActivity.class));
        // Đang ở Profile nên không cần set listener cho navProfile
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tự động đồng bộ dữ liệu khi có mạng
        com.nhom2.learnenglish.core.util.NetworkSyncManager.INSTANCE.syncIfOnline(this);
    }
}
