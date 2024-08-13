package com.example.ember;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ember.Models.User;
import com.example.ember.Models.ChatListAdapter;
import com.firebase.ui.auth.AuthUI;
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

public class ChatListActivity extends AppCompatActivity {

    private static final String TAG = "ChatListActivity";
    private RecyclerView chatRecyclerView;
    private ChatListAdapter chatAdapter;
    private FirebaseUser currentUser;
    private DatabaseReference usersRef;
    private List<User> matchedUsers;
    private TextView noMatchesTextView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noMatchesTextView = findViewById(R.id.no_matches_text_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        matchedUsers = new ArrayList<>();

        if (currentUser != null) {
            loadUserChats();
        } else {
            Log.e(TAG, "Current user is null");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(ChatListActivity.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(ChatListActivity.this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.navigation_chat) {
                return true;
            } else if (itemId == R.id.action_logout) {
                performLogout();
                return true;
            } else {
                return false;
            }
        });
    }

    private void loadUserChats() {
        String currentUserId = currentUser.getUid();
        DatabaseReference userChatsRef = FirebaseDatabase.getInstance().getReference("UserChats").child(currentUserId);

        userChatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                matchedUsers.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String matchedUserId = userSnapshot.getKey();
                    usersRef.child(matchedUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User matchedUser = snapshot.getValue(User.class);
                            if (matchedUser != null) {
                                matchedUsers.add(matchedUser);
                                updateRecyclerView();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Failed to read user data", error.toException());
                        }
                    });
                }

                updateUIVisibility();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read user chats", error.toException());
            }
        });
    }

    private void updateUIVisibility() {
        if (matchedUsers.isEmpty()) {
            noMatchesTextView.setVisibility(View.VISIBLE);
            chatRecyclerView.setVisibility(View.GONE);
        } else {
            noMatchesTextView.setVisibility(View.GONE);
            chatRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updateRecyclerView() {
        if (chatAdapter == null) {
            chatAdapter = new ChatListAdapter(this, matchedUsers);
            chatRecyclerView.setAdapter(chatAdapter);
        } else {
            chatAdapter.notifyDataSetChanged();
        }
        updateUIVisibility();
    }

    private void performLogout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChatListActivity.this, "Successfully logged out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChatListActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ChatListActivity.this, "Logout failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}