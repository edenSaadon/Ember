package com.example.ember;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ember.Models.User;
import com.example.ember.Models.UserAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private FirebaseUser currentUser;
    private DatabaseReference usersRef;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "Current user ID: " + currentUser.getUid());
            loadFilteredUsers();
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
            } else if (itemId == R.id.navigation_chat) {
                Log.d(TAG, "Navigating to Chats");
                Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.action_logout) {
                Log.d(TAG, "Logging out");
                performLogout();
                return true;
            } else {
                Log.d(TAG, "Unknown navigation item");
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            performLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performLogout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Successfully logged out", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Logout failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadFilteredUsers() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> filteredUsers = new ArrayList<>();
                User currentUserData = null;

                // Retrieve current user data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getUserId().equals(currentUser.getUid())) {
                        currentUserData = user;
                        break;
                    }
                }

                if (currentUserData != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        // Exclude the current user
                        if (user != null && !user.getUserId().equals(currentUser.getUid())) {
                            if (matchesCurrentUserPreferences(user, currentUserData)) {
                                filteredUsers.add(user);
                                Log.d(TAG, "User added: " + user.getName());
                            }
                        }
                    }
                    setupRecyclerView(filteredUsers);
                } else {
                    Log.e(TAG, "Current user data not found in database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read users", databaseError.toException());
            }
        });
    }

    private boolean matchesCurrentUserPreferences(User user, User currentUser) {
        // Check if at least one preference matches
        boolean genderMatch = false;
        if (currentUser.getSexualPreference().equals("Heterosexual")) {
            genderMatch = !user.getGender().equals(currentUser.getGender());
        } else if (currentUser.getSexualPreference().equals("Homosexual")) {
            genderMatch = user.getGender().equals(currentUser.getGender());
        } else if (currentUser.getSexualPreference().equals("Bisexual")) {
            genderMatch = true;
        }

        // Parse the partner age range
        int minAge = 0;
        int maxAge = Integer.MAX_VALUE;
        if (currentUser.getPartnerAgeRange() != null && currentUser.getPartnerAgeRange().contains("-")) {
            String[] ageRangeParts = currentUser.getPartnerAgeRange().split("-");
            try {
                minAge = Integer.parseInt(ageRangeParts[0].trim());
                maxAge = Integer.parseInt(ageRangeParts[1].trim());
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid age range format", e);
            }
        }

        double distance = calculateDistance(currentUser.getLatitude(), currentUser.getLongitude(), user.getLatitude(), user.getLongitude());
        boolean withinLocationRange = distance <= currentUser.getLocationRange();

        // At least one criteria must match
        boolean matches = (genderMatch || (user.getAge() >= minAge && user.getAge() <= maxAge) || withinLocationRange);

        Log.d(TAG, "User: " + user.getName() + " | Matches: " + matches + " | Distance: " + distance + " | Age: " + user.getAge());
        return matches;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to kilometers
        return distance;
    }

    private void setupRecyclerView(List<User> filteredUsers) {
        if (filteredUsers.isEmpty()) {
            Toast.makeText(this, "No users match your preferences", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "No users matched the filter criteria.");
        } else {
            List<String> imageUrls = new ArrayList<>();
            List<String> userIds = new ArrayList<>();
            for (User user : filteredUsers) {
                if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
                    Log.d(TAG, "Adding image URL: " + user.getImageUrl());
                    imageUrls.add(user.getImageUrl());
                    userIds.add(user.getUserId());
                } else {
                    Log.e(TAG, "User " + user.getName() + " has no valid image URL");
                }
            }
            userAdapter = new UserAdapter(imageUrls, userIds, new UserAdapter.OnLikeDislikeListener() {
                @Override
                public void onLike(int position) {
                    String likedUserId = userIds.get(position);
                    Log.d(TAG, "Liked user ID: " + likedUserId);
                    checkForMatches(likedUserId);
                }

                @Override
                public void onDislike(int position) {
                    Log.d(TAG, "Disliked user at position: " + position);
                }
            });
            recyclerView.setAdapter(userAdapter);
        }
    }

    private void checkForMatches(String likedUserId) {
        DatabaseReference currentUserRef = usersRef.child(currentUser.getUid());
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUserData = snapshot.getValue(User.class);
                if (currentUserData != null) {
                    currentUserData.addLikedUser(likedUserId);
                    currentUserRef.setValue(currentUserData);

                    DatabaseReference likedUserRef = usersRef.child(likedUserId);
                    likedUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User likedUser = snapshot.getValue(User.class);
                            if (likedUser != null) {
                                if (likedUser.getLikedUsers().contains(currentUser.getUid())) {
                                    Log.d(TAG, "It's a match!");
                                    createMatchForBothUsers(likedUserId);
                                    showMatchDialog(likedUser);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Failed to check for matches", error.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to update current user likes", error.toException());
            }
        });
    }

    private void createMatchForBothUsers(String matchedUserId) {
        DatabaseReference matchesRef = FirebaseDatabase.getInstance().getReference("Matches");
        String chatId = generateChatId(currentUser.getUid(), matchedUserId);
        matchesRef.child(currentUser.getUid()).child(chatId).setValue(true);
        matchesRef.child(matchedUserId).child(chatId).setValue(true);
    }

    private String generateChatId(String userId1, String userId2) {
        return userId1.compareTo(userId2) < 0 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }

    private void showMatchDialog(User matchedUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Match!")
                .setMessage("You have a new match with " + matchedUser.getName() + "!")
                .setPositiveButton("Chat now", (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    intent.putExtra("matchedUserId", matchedUser.getUserId());
                    startActivity(intent);
                })
                .setNegativeButton("Later", (dialog, which) -> dialog.dismiss())
                .show();
    }}