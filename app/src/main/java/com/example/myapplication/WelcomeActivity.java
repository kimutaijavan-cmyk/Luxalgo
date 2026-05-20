package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WelcomeActivity extends AppCompatActivity {

    private String userRole = "";

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
        setContentView(R.layout.activity_welcome);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.welcome_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView welcomeText = findViewById(R.id.welcomeText);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance("https://kim69-4474b-default-rtdb.firebaseio.com/")
                    .getReference("Users").child(userId);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        userRole = snapshot.child("role").getValue(String.class);
                        welcomeText.setText("Welcome, " + name + "!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(WelcomeActivity.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(v -> {
            if (userRole == null || userRole.isEmpty()) {
                Toast.makeText(this, "Loading user data, please wait...", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent;
            switch (userRole) {
                case "Admin":
                    intent = new Intent(WelcomeActivity.this, AdminDashboardActivity.class);
                    break;
                case "Student":
                    intent = new Intent(WelcomeActivity.this, StudentDashboardActivity.class);
                    break;
                case "Lec":
                    intent = new Intent(WelcomeActivity.this, LecturerDashboardActivity.class);
                    break;
                default:
                    Toast.makeText(this, "Unknown role: " + userRole, Toast.LENGTH_SHORT).show();
                    return;
            }
            startActivity(intent);
            finish();
        });

        findViewById(R.id.signOutButton).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
            finish(); // Close welcome activity and go back to login (MainActivity)
        });
    }
}
