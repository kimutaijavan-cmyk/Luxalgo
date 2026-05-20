package com.example.myapplication;

import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView adminName, tvTotalStudents, tvTotalAdmins, tvTotalLecturers, tvTotalCourses, tvTotalGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        adminName = findViewById(R.id.adminName);
        tvTotalStudents = findViewById(R.id.tvTotalStudents);
        tvTotalAdmins = findViewById(R.id.tvTotalAdmins);
        tvTotalLecturers = findViewById(R.id.tvTotalLecturers);
        tvTotalCourses = findViewById(R.id.tvTotalCourses);
        tvTotalGPS = findViewById(R.id.tvTotalGPS);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance("https://kim69-4474b-default-rtdb.firebaseio.com/").getReference("Users").child(userId);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        adminName.setText(name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AdminDashboardActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });

            // Fetching totals (Placeholder logic)
            DatabaseReference allUsersRef = FirebaseDatabase.getInstance("https://kim69-4474b-default-rtdb.firebaseio.com/").getReference("Users");
            allUsersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int students = 0, admins = 0, lecturers = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String role = ds.child("role").getValue(String.class);
                        if ("Student".equals(role)) students++;
                        else if ("Admin".equals(role)) admins++;
                        else if ("Lec".equals(role)) lecturers++;
                    }
                    tvTotalStudents.setText(String.valueOf(students));
                    tvTotalAdmins.setText(String.valueOf(admins));
                    tvTotalLecturers.setText(String.valueOf(lecturers));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        Button logout = findViewById(R.id.adminLogout);
        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AdminDashboardActivity.this, MainActivity.class));
            finish();
        });
    }
}