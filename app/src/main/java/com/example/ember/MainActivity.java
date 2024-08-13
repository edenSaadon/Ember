package com.example.ember;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ember.Models.User;
import com.example.ember.Models.UserAdapter;
import com.example.ember.Models.Chat;
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
    private DatabaseReference likesRef;
    private DatabaseReference dislikesRef;
    private BottomNavigationView bottomNavigationView;
    private List<User> filteredUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        likesRef = FirebaseDatabase.getInstance().getReference("Likes");
        dislikesRef = FirebaseDatabase.getInstance().getReference("Dislikes");
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

        Button btnLike = findViewById(R.id.btn_like);
        Button btnDislike = findViewById(R.id.btn_dislike);

        btnLike.setOnClickListener(v -> likeCurrentUser());
        btnDislike.setOnClickListener(v -> dislikeCurrentUser());
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
                filteredUsers = new ArrayList<>();
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
                        // Exclude the current user, disliked users, and those you've already liked
                        if (user != null && !user.getUserId().equals(currentUser.getUid())) {
                            if (matchesCurrentUserPreferences(user, currentUserData)) {
                                filteredUsers.add(user);
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
        // Gender Preference Match
        boolean genderMatch = false;
        if (currentUser.getSexualPreference().equals("Heterosexual")) {
            genderMatch = !user.getGender().equals(currentUser.getGender());
        } else if (currentUser.getSexualPreference().equals("Homosexual")) {
            genderMatch = user.getGender().equals(currentUser.getGender());
        } else if (currentUser.getSexualPreference().equals("Bisexual")) {
            genderMatch = true; // Bisexual matches all genders
        }

        // Relationship Preference Match
        boolean relationshipMatch = user.getLookingFor().equals(currentUser.getLookingFor());

        // Age Preference Match
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
        boolean ageMatch = user.getAge() >= minAge && user.getAge() <= maxAge;

        // Location Preference Match
        double distance = calculateDistance(currentUser.getLatitude(), currentUser.getLongitude(), user.getLatitude(), user.getLongitude());
        boolean withinLocationRange = distance <= currentUser.getLocationRange();

        return genderMatch || relationshipMatch || ageMatch || withinLocationRange;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // convert to kilometers
    }

    private void setupRecyclerView(List<User> filteredUsers) {
        if (filteredUsers.isEmpty()) {
            Toast.makeText(this, "No users match your preferences", Toast.LENGTH_SHORT).show();
        } else {
            userAdapter = new UserAdapter(this, filteredUsers, userId -> {
                Log.d(TAG, "User clicked: " + userId);
                // You can define what to do when a user is clicked (e.g., like or dislike)
            });
            recyclerView.setAdapter(userAdapter);
        }
    }

    private void likeCurrentUser() {
        if (!filteredUsers.isEmpty()) {
            User likedUser = filteredUsers.get(0); // Get the first user in the list
            String likedUserId = likedUser.getUserId();
            String currentUserId = currentUser.getUid();

            // Add the like to the database
            likesRef.child(currentUserId).child(likedUserId).setValue(true);

            // Check if the liked user has already liked the current user
            likesRef.child(likedUserId).child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Mutual like detected
                        showMatchNotification(likedUser.getName());

                        // Add match to the Matches node
                        DatabaseReference matchesRef = FirebaseDatabase.getInstance().getReference("Matches");
                        matchesRef.child(currentUserId).child(likedUserId).setValue(true);
                        matchesRef.child(likedUserId).child(currentUserId).setValue(true);

                        // Update chat list for both users
                        DatabaseReference userChatsRef = FirebaseDatabase.getInstance().getReference("UserChats");
                        userChatsRef.child(currentUserId).child(likedUserId).setValue(true);
                        userChatsRef.child(likedUserId).child(currentUserId).setValue(true);

                        // Create a new chat
                        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("Chats");
                        String chatId = chatsRef.push().getKey();
                        if (chatId != null) {
                            Chat newChat = new Chat(chatId, currentUserId, likedUserId, "You have a new match!", System.currentTimeMillis());
                            chatsRef.child(chatId).setValue(newChat);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error checking mutual like", databaseError.toException());
                }
            });

            filteredUsers.remove(0); // Remove the user from the list after the like
            userAdapter.notifyDataSetChanged(); // Update the UI
            Log.d(TAG, "User liked: " + likedUserId);
        }
    }

    private void showMatchNotification(String matchedUserName) {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_match, null);

        // Create a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setView(dialogView);

        // Find and set the views in the dialog
        TextView matchUserName = dialogView.findViewById(R.id.match_user_name);
        Button closeButton = dialogView.findViewById(R.id.close_button);

        // Set the match text
        matchUserName.setText("You've matched with " + matchedUserName + "!");

        // Set a click listener for the close button
        AlertDialog dialog = builder.create();
        closeButton.setOnClickListener(v -> dialog.dismiss());

        // Create and show the dialog
        dialog.show();
    }

    private void dislikeCurrentUser() {
        if (!filteredUsers.isEmpty()) {
            User dislikedUser = filteredUsers.get(0); // Get the first user in the list
            dislikesRef.child(currentUser.getUid()).child(dislikedUser.getUserId()).setValue(true);
            filteredUsers.remove(0); // Remove the user from the list after the dislike
            userAdapter.notifyDataSetChanged(); // Update the UI
            Log.d(TAG, "User disliked: " + dislikedUser.getUserId());
        }
    }
}