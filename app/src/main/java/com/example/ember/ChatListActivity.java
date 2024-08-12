
package com.example.ember;

import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ember.Models.ChatListAdapter;
import com.example.ember.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private TextView noMatchesTextView;
    private ChatListAdapter chatListAdapter;
    private List<User> chatUsers;
    private DatabaseReference matchesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        noMatchesTextView = findViewById(R.id.no_matches_text_view);
        chatUsers = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(this, chatUsers);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatListAdapter);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        matchesRef = FirebaseDatabase.getInstance().getReference("Matches");

        loadChatUsers(currentUserId);
    }

    private void loadChatUsers(String currentUserId) {
        matchesRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String chatId = snapshot.getKey();
                    String[] userIds = chatId.split("_");
                    String matchedUserId = userIds[0].equals(currentUserId) ? userIds[1] : userIds[0];
                    loadUser(matchedUserId);
                }

                if (chatUsers.isEmpty()) {
                    noMatchesTextView.setVisibility(View.VISIBLE);
                    chatRecyclerView.setVisibility(View.GONE);
                } else {
                    noMatchesTextView.setVisibility(View.GONE);
                    chatRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void loadUser(String userId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    chatUsers.add(user);
                    chatListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}