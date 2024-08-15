package com.example.ember.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ember.Activities.ChatActivity;
import com.example.ember.Models.User;
import com.example.ember.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private Context context;
    private List<User> chatUsers;

    public ChatListAdapter(Context context, List<User> chatUsers) {
        this.context = context;
        this.chatUsers = chatUsers;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_list, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        User user = chatUsers.get(position);
        holder.chatUserName.setText(user.getName());

        // Load user image using Picasso
        if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
            Picasso.get().load(user.getImageUrl()).into(holder.chatUserImage);
        } else {
            holder.chatUserImage.setImageResource(R.drawable.ic_profile); // Default image
        }

        holder.itemView.setOnClickListener(v -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String matchedUserId = user.getUserId();

            String chatId = getChatId(currentUserId, matchedUserId);

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("chatId", chatId);
            intent.putExtra("matchedUserId", matchedUserId);
            context.startActivity(intent);
        });
    }

    private String getChatId(String currentUserId, String matchedUserId) {
        if (currentUserId.compareTo(matchedUserId) < 0) {
            return currentUserId + "_" + matchedUserId;
        } else {
            return matchedUserId + "_" + currentUserId;
        }
    }

    @Override
    public int getItemCount() {
        return chatUsers.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView chatUserName;
        ImageView chatUserImage; // Add ImageView for user image

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatUserName = itemView.findViewById(R.id.chat_user_name);
            chatUserImage = itemView.findViewById(R.id.chat_user_image); // Initialize ImageView
        }
    }
}
