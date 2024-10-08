package com.example.ember.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ember.Models.Chat;
import com.example.ember.Models.User;
import com.example.ember.R;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private GestureDetector gestureDetector;
    private FirebaseUser currentUser;
    private DatabaseReference usersRef;
    private DatabaseReference likesRef;
    private DatabaseReference dislikesRef;
    private DatabaseReference matchesRef;
    private BottomNavigationView bottomNavigationView;
    private List<User> filteredUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout frameLayout = findViewById(R.id.frameLayout);

        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        likesRef = FirebaseDatabase.getInstance().getReference("Likes");
        dislikesRef = FirebaseDatabase.getInstance().getReference("Dislikes");
        matchesRef = FirebaseDatabase.getInstance().getReference("Matches");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "Current user ID: " + currentUser.getUid());
            checkUserNotifications();  // Check for new notifications
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

        gestureDetector = new GestureDetector(this, new SwipeGestureDetector());

        frameLayout.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
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

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && currentUser.getUid() != null && user.getUserId() != null && user.getUserId().equals(currentUser.getUid())) {
                        currentUserData = user;
                        break;
                    }
                }

                if (currentUserData != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        // Skip current user
                        if (user != null && user.getUserId() != null && !user.getUserId().equals(currentUser.getUid())) {
                            if (matchesCurrentUserPreferences(user, currentUserData)) {
                                filteredUsers.add(user);
                            }
                        }
                    }
                    // Filter out users with likes, dislikes, or matches
                    filterOutLikedDislikedAndMatchedUsers(filteredUsers);
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

    private void filterOutLikedDislikedAndMatchedUsers(List<User> users) {
        String currentUserId = currentUser.getUid();

        likesRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot likesSnapshot) {
                dislikesRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dislikesSnapshot) {
                        matchesRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot matchesSnapshot) {
                                List<User> filteredList = new ArrayList<>();

                                for (User user : users) {
                                    String userId = user.getUserId();
                                    if (!likesSnapshot.hasChild(userId) &&
                                            !dislikesSnapshot.hasChild(userId) &&
                                            !matchesSnapshot.hasChild(userId)) {
                                        filteredList.add(user);
                                    }
                                }

                                filteredUsers = filteredList;
                                if (!filteredUsers.isEmpty()) {
                                    setupFrameLayout(filteredUsers.get(0));
                                } else {
                                    showNoUsersMessage();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "Error reading matches", error.toException());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error reading dislikes", error.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error reading likes", error.toException());
            }
        });
    }

    private boolean matchesCurrentUserPreferences(User user, User currentUser) {
        // Gender Preference Match (Mandatory)
        boolean genderMatch = false;

        if (currentUser.getSexualPreference().equals("Heterosexual")) {
            genderMatch = user.getGender() != null && !user.getGender().equals(currentUser.getGender())
                    && user.getSexualPreference() != null && user.getSexualPreference().equals("Heterosexual");
        } else if (currentUser.getSexualPreference().equals("Homosexual")) {
            genderMatch = user.getGender() != null && user.getGender().equals(currentUser.getGender())
                    && user.getSexualPreference() != null && user.getSexualPreference().equals("Homosexual");
        } else if (currentUser.getSexualPreference().equals("Bisexual")) {
            genderMatch = true; // Bisexual matches all genders
        }

        // Age Preference Match (Mandatory)
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

        // Distance Preference Match (Mandatory)
        boolean distanceMatch = false;
        if (currentUser.getLocationRange() >= 0) { // Allow zero distance
            double distance = calculateDistance(currentUser.getLatitude(), currentUser.getLongitude(), user.getLatitude(), user.getLongitude());
            distanceMatch = distance <= currentUser.getLocationRange();
        }

        // Only users matching gender, age, and distance criteria will be shown
        return genderMatch && ageMatch && distanceMatch;
    }

    private void showNoUsersMessage() {
        FrameLayout frameLayout = findViewById(R.id.frameLayout);
        frameLayout.removeAllViews();

        View noUsersView = LayoutInflater.from(this).inflate(R.layout.no_users_message, frameLayout, false);
        TextView noUsersMessage = noUsersView.findViewById(R.id.no_users_message);

        // Create a blended color of pink and blue
        Shader textShader = new LinearGradient(0, 0, 0, noUsersMessage.getTextSize(),
                new int[]{
                        Color.parseColor("#FF00FF"), // Pink
                        Color.parseColor("#0000FF"), // Blue
                }, null, Shader.TileMode.CLAMP);
        noUsersMessage.getPaint().setShader(textShader);

        frameLayout.addView(noUsersView);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private void setupFrameLayout(User user) {
        FrameLayout frameLayout = findViewById(R.id.frameLayout);
        frameLayout.removeAllViews();

        View userView = LayoutInflater.from(this).inflate(R.layout.item_user, frameLayout, false);

        // Make sure this TextView is not null
        TextView userName = userView.findViewById(R.id.user_name);
        ImageView userImage = userView.findViewById(R.id.user_image);
        TextView userInfo = userView.findViewById(R.id.user_info);

        // Check if userName is found
        if (userName != null) {
            userName.setText(user.getName() + ", " + user.getAge());
        } else {
            Log.e("setupFrameLayout", "userName TextView is null!");
        }

        if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
            Picasso.get().load(user.getImageUrl()).into(userImage);
        } else {
            userImage.setImageResource(R.drawable.ic_profile);
        }

        frameLayout.addView(userView);
    }

    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling detected");
            if (e1 == null || e2 == null) return false;
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        likeCurrentUser();
                    } else {
                        dislikeCurrentUser();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    private void likeCurrentUser() {
        if (!filteredUsers.isEmpty()) {
            User likedUser = filteredUsers.get(0);
            String likedUserId = likedUser.getUserId();
            String currentUserId = currentUser.getUid();

            likesRef.child(currentUserId).child(likedUserId).setValue(true);

            likesRef.child(likedUserId).child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String matchedUserName = likedUser.getName(); // Store the name for consistent notifications

                        // Check if the name is null and provide a fallback
                        if (matchedUserName == null || matchedUserName.isEmpty()) {
                            matchedUserName = "New Match"; // Fallback name if the user's name is null or empty
                        }

                        String matchedUserImageUrl = likedUser.getImageUrl(); // Get the matched user's image URL

                        // Show match notification with matched user’s details
                        showMatchNotification(matchedUserName, matchedUserImageUrl);

                        matchesRef.child(currentUserId).child(likedUserId).setValue(true);
                        matchesRef.child(likedUserId).child(currentUserId).setValue(true);

                        DatabaseReference userChatsRef = FirebaseDatabase.getInstance().getReference("UserChats");
                        userChatsRef.child(currentUserId).child(likedUserId).setValue(true);
                        userChatsRef.child(likedUserId).child(currentUserId).setValue(true);

                        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("Chats");
                        String chatId = chatsRef.push().getKey();
                        if (chatId != null) {
                            Chat newChat = new Chat(chatId, currentUserId, likedUserId, "You have a new match!", System.currentTimeMillis());
                            chatsRef.child(chatId).setValue(newChat);
                        }

                        // Send the notification with the correct user name
                        sendMatchNotification(likedUserId, matchedUserName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error checking mutual like", databaseError.toException());
                }
            });

            filteredUsers.remove(0);
            if (!filteredUsers.isEmpty()) {
                setupFrameLayout(filteredUsers.get(0));
            } else {
                showNoUsersMessage();
            }
        }
    }


    private void dislikeCurrentUser() {
        if (!filteredUsers.isEmpty()) {
            User dislikedUser = filteredUsers.get(0);
            dislikesRef.child(currentUser.getUid()).child(dislikedUser.getUserId()).setValue(true);
            filteredUsers.remove(0);
            if (!filteredUsers.isEmpty()) {
                setupFrameLayout(filteredUsers.get(0));
            } else {
                showNoUsersMessage();
            }
        }
    }

    // NOTE: When a match is made from the second user account, the name appears correctly in the first user's notification.
    // However, when the match is initiated from the first user account, the name does not always pass correctly.

    private void sendMatchNotification(String receiverUserId, String matchedUserName) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications").child(receiverUserId);

        // Create notification message
        String notificationId = notificationRef.push().getKey();
        if (notificationId != null) {
            String senderUserName = currentUser.getDisplayName();
            notificationRef.child(notificationId).setValue("You've matched with " + senderUserName + "!");
        }
    }


    private void checkUserNotifications() {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications").child(currentUser.getUid());

        notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String message = snapshot.getValue(String.class);
                    if (message != null) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                    // Delete the message after showing the notification
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load notifications", databaseError.toException());
            }
        });
    }

    private void showMatchNotification(String matchedUserName, String matchedUserImageUrl) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_match, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setView(dialogView);

        TextView matchUserNameTextView = dialogView.findViewById(R.id.user_name);
        ImageView matchImageView = dialogView.findViewById(R.id.image_view);
        Button closeButton = dialogView.findViewById(R.id.close_button);

        // Check if the matched user name is null or empty; if so, use a default message
        if (matchedUserName == null || matchedUserName.isEmpty()) {
            matchedUserName = "a new match"; // Default value
        }

        // Set the match text and user image
        matchUserNameTextView.setText("You've matched with " + matchedUserName + "!");

        if (matchedUserImageUrl != null && !matchedUserImageUrl.isEmpty()) {
            Picasso.get().load(matchedUserImageUrl).into(matchImageView);
        } else {
            matchImageView.setImageResource(R.drawable.ic_profile); // Default image if no URL
        }

        // Start heart animation
        startHeartAnimation(dialogView);

        // Play match sound
        playMatchSound();

        AlertDialog dialog = builder.create();
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }





    private void startHeartAnimation(View dialogView) {
        // Create heart animation
        ImageView heart1 = new ImageView(this);
        heart1.setImageResource(R.drawable.ic_heart);
        ImageView heart2 = new ImageView(this);
        heart2.setImageResource(R.drawable.ic_heart);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(100, 100);
        params1.addRule(RelativeLayout.CENTER_IN_PARENT);
        heart1.setLayoutParams(params1);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(100, 100);
        params2.addRule(RelativeLayout.CENTER_IN_PARENT);
        heart2.setLayoutParams(params2);

        RelativeLayout layout = dialogView.findViewById(R.id.match_layout);
        layout.addView(heart1);
        layout.addView(heart2);

        // Perform animation
        heart1.animate().translationYBy(-200).alpha(0).setDuration(2000).start();
        heart2.animate().translationYBy(-300).alpha(0).setDuration(2500).start();
    }

    private void playMatchSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.match_sound);
        mediaPlayer.start();
    }
}