package com.example.ember;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ember.Models.UserAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;
    private StorageReference imagesRef;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        storage = FirebaseStorage.getInstance();
        imagesRef = storage.getReference().child("images"); // Path to your images folder

        loadImages();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "Current user ID: " + currentUser.getUid());
        } else {
            Log.e(TAG, "Current user is null");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Log.d(TAG, "Navigating to Home");
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Log.d(TAG, "Navigating to Profile");
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            } else {
                Log.d(TAG, "Unknown navigation item");
                return false;
            }
        });
    }

    private void loadImages() {
        imagesRef.listAll().addOnSuccessListener(listResult -> {
            List<String> imageUrls = new ArrayList<>();
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrls.add(uri.toString());
                    if (imageUrls.size() == listResult.getItems().size()) {
                        setupRecyclerView(imageUrls);
                    }
                }).addOnFailureListener(e -> Log.e(TAG, "Failed to get download URL", e));
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to list images", e));
    }

    private void setupRecyclerView(List<String> imageUrls) {
        userAdapter = new UserAdapter(imageUrls);
        recyclerView.setAdapter(userAdapter);
    }
}
