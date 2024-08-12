
package com.example.ember;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ListView chatListView;
    private EditText messageInput;
    private ImageButton sendButton;

    private String matchedUserId;
    private String currentUserId;
    private DatabaseReference chatsRef;

    private List<Chat> chatList;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatListView = findViewById(R.id.chat_list_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        matchedUserId = getIntent().getStringExtra("matchedUserId");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String chatId = generateChatId(currentUserId, matchedUserId);
        chatsRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatId);

        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatList);
        chatListView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> sendMessage());

        loadMessages();
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

        Chat chat = new Chat(currentUserId, matchedUserId, messageText, timestamp);
        chatsRef.push().setValue(chat);

        // Also update the Matches reference to make sure both users have the chatId
        DatabaseReference matchesRef = FirebaseDatabase.getInstance().getReference("Matches");
        matchesRef.child(currentUserId).child(generateChatId(currentUserId, matchedUserId)).setValue(true);
        matchesRef.child(matchedUserId).child(generateChatId(currentUserId, matchedUserId)).setValue(true);

        messageInput.setText("");
    }

    private void loadMessages() {
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null) {
                        chatList.add(chat);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                chatListView.setSelection(chatAdapter.getCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Chat", "Failed to load messages", databaseError.toException());
            }
        });
    }
}