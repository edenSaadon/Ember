package com.example.ember;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ember.Models.User;
import com.example.ember.Models.UserAdapter;
import com.example.ember.Models.UserContainer;
import com.example.ember.Utilities.UserManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private UserManager usersManager;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private DatabaseReference usersRef;
    private FirebaseUser currentUser;
    private BottomNavigationView bottomNavigationView;
    private Button btnPrevious;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersManager = new UserManager(new UserManager.UsersManagerCallback() {
            @Override
            public void onUsersLoaded(UserContainer usersContainer) {
                Log.d(TAG, "Users loaded successfully");
                filterAndShowUsers();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading users: " + error);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView == null) {
            Log.e(TAG, "bottomNavigationView is null");
        } else {
            Log.d(TAG, "bottomNavigationView found");
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Log.d(TAG, "Navigating to Home");
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Log.d(TAG, "Navigating to Profile");
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            } else {
                Log.d(TAG, "Unknown navigation item");
                return false;
            }
        });

        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);

        btnPrevious.setOnClickListener(v -> {
            Log.d(TAG, "Previous button clicked");
            // Add functionality for btnPrevious if needed
        });

        btnNext.setOnClickListener(v -> {
            Log.d(TAG, "Next button clicked");
            // Add functionality for btnNext if needed
        });

        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "Current user ID: " + currentUser.getUid());
            usersManager.loadUsers();
        } else {
            Log.e(TAG, "Current user is null");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterAndShowUsers() {
        if (currentUser == null) {
            Log.e(TAG, "Current user is null, cannot load data");
            return;
        }

        usersRef.child(currentUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User currentUserData = task.getResult().getValue(User.class);
                if (currentUserData != null) {
                    Log.d(TAG, "Current user data: " + currentUserData.toString());
                    List<User> filteredUsers = usersManager.filterUsersByPreferences(currentUserData);
                    userAdapter = new UserAdapter(filteredUsers);
                    recyclerView.setAdapter(userAdapter);
                } else {
                    Log.e(TAG, "Current user data not found");
                    Toast.makeText(MainActivity.this, "Current user data not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Failed to load current user data", task.getException());
                Toast.makeText(MainActivity.this, "Failed to load current user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
