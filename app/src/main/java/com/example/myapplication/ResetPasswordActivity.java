package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("IsDarkMode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reset_password_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton themeSwitchButton = findViewById(R.id.themeSwitchButton);
        if (isDarkMode) {
            themeSwitchButton.setImageResource(R.drawable.ic_sun);
        } else {
            themeSwitchButton.setImageResource(R.drawable.ic_moon);
        }

        themeSwitchButton.setOnClickListener(v -> {
            boolean currentMode = sharedPreferences.getBoolean("IsDarkMode", false);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (currentMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("IsDarkMode", false);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("IsDarkMode", true);
            }
            editor.apply();
            recreate();
        });

        findViewById(R.id.sendResetButton).setOnClickListener(v -> {
            Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
        });

        TextView backToLogin = findViewById(R.id.backToLoginFromReset);
        backToLogin.setOnClickListener(v -> {
            finish();
        });
    }
}
