package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved theme preference
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("IsDarkMode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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

        TextView goToRegistration = findViewById(R.id.goToRegistration);
        goToRegistration.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        TextView forgetPassword = findViewById(R.id.forgetPassword);
        forgetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        CheckBox rememberMe = findViewById(R.id.rememberMe);
        RadioGroup roleRadioGroup = findViewById(R.id.roleRadioGroup);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            String emailInput = email.getText().toString();
            String passwordInput = password.getText().toString();

            if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.signInWithEmailAndPassword(emailInput, passwordInput)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Login Successful")
                                        .setMessage("Welcome back!")
                                        .setPositiveButton("Continue", (dialog, which) -> {
                                            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .setCancelable(false)
                                        .show();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuth != null && mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
