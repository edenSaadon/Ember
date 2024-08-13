package com.example.ember;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ember.Models.Chat;
import com.example.ember.Models.ChatAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ListView chatListView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageButton exitButton;
    private TextView userNameTextView;

    private String matchedUserId;
    private String currentUserId;
    private DatabaseReference chatsRef;
    private DatabaseReference userRef;

    private List<Chat> chatList;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatListView = findViewById(R.id.chat_list_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        exitButton = findViewById(R.id.exit_button);
        userNameTextView = findViewById(R.id.user_name);
        ImageView userProfilePictureImageView = findViewById(R.id.user_profile_picture);

        matchedUserId = getIntent().getStringExtra("matchedUserId");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String chatId = generateChatId(currentUserId, matchedUserId);
        chatsRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatId);
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(matchedUserId);

        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatList);
        chatListView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> sendMessage());
        exitButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, ChatListActivity.class);
            startActivity(intent);
            finish();
        });

        loadMessages();
        loadUserData(userProfilePictureImageView);
    }

    private void loadUserData(ImageView userProfilePictureImageView) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("name").getValue(String.class);
                String profileImageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                if (userName != null) {
                    userNameTextView.setText(userName);
                }

                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Picasso.get().load(profileImageUrl).into(userProfilePictureImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String generateChatId(String userId1, String userId2) {
        return userId1.compareTo(userId2) < 0 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        long timestamp = System.currentTimeMillis();
        String chatId = generateChatId(currentUserId, matchedUserId);

        Chat chat = new Chat(chatId, currentUserId, matchedUserId, messageText, timestamp);
        chatsRef.push().setValue(chat).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Message sent successfully, update chat list for both users
                DatabaseReference currentUserChatListRef = FirebaseDatabase.getInstance().getReference("UserChats").child(currentUserId);
                DatabaseReference matchedUserChatListRef = FirebaseDatabase.getInstance().getReference("UserChats").child(matchedUserId);

                currentUserChatListRef.child(matchedUserId).setValue(true);
                matchedUserChatListRef.child(currentUserId).setValue(true);

                messageInput.setText(""); // Clear message input
            } else {
                Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMessages() {
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat != null) {
                            chatList.add(chat);
                        }
                    } catch (Exception e) {
                        Log.e("ChatActivity", "Error parsing chat data", e);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                chatListView.setSelection(chatAdapter.getCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatActivity", "Failed to load messages", databaseError.toException());
            }
        });
    }
}
