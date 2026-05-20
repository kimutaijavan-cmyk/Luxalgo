package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

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
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://kim69-4474b-default-rtdb.firebaseio.com/");
        usersRef = database.getReference("Users");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registration_main), (v, insets) -> {
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

        TextView backToLogin = findViewById(R.id.backToLogin);
        backToLogin.setOnClickListener(v -> {
            finish();
        });

        Spinner roleSpinner = findViewById(R.id.roleSpinner);
        String[] roles = {"Student", "Lec", "Admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        EditText regName = findViewById(R.id.reg_name);
        EditText regEmail = findViewById(R.id.reg_email);
        EditText regPassword = findViewById(R.id.reg_password);
        EditText regConfirmPassword = findViewById(R.id.reg_confirm_password);
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String name = regName.getText().toString();
            String email = regEmail.getText().toString();
            String password = regPassword.getText().toString();
            String confirmPassword = regConfirmPassword.getText().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                String role = roleSpinner.getSelectedItem().toString();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                String userId = mAuth.getCurrentUser().getUid();
                                
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("name", name);
                                userMap.put("email", email);
                                userMap.put("role", role);

                                usersRef.child(userId).setValue(userMap)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            new AlertDialog.Builder(RegistrationActivity.this)
                                                    .setTitle("Registration Successful")
                                                    .setMessage("Your account has been created. You can now log in.")
                                                    .setPositiveButton("OK", (dialog, which) -> finish())
                                                    .setCancelable(false)
                                                    .show();
                                        } else {
                                            Toast.makeText(RegistrationActivity.this, "Database error: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
