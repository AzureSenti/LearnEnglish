package com.nhom2.learnenglish;


import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.nhom2.learnenglish.core.data.local.AuthPreferences;
import com.nhom2.learnenglish.core.data.local.mockdata.MockDataImport;
import com.nhom2.learnenglish.core.util.Navigator;
import com.nhom2.learnenglish.feature.auth.LoginActivity;
import com.nhom2.learnenglish.feature.mainmenu.MainMenuActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        MockDataImport.INSTANCE.importIfNeeded(this);

        if (AuthPreferences.INSTANCE.isSessionRestored(this)) {
            Navigator.INSTANCE.navigateTo(this, MainMenuActivity.class);
        } else {
            Navigator.INSTANCE.navigateTo(this, LoginActivity.class);
        }

        finish();
    }
}