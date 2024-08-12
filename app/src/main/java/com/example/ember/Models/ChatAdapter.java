
package com.example.ember.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ember.Models.Chat;
import com.example.ember.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context context;
    private List<Chat> chatList;

    public ChatAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public int getCount() {
        return chatList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2; // שני סוגים: הודעה שנשלחה והודעה שנתקבלה
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = chatList.get(position);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (chat.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Chat chat = chatList.get(position);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (convertView == null) {
            if (chat.getSenderId().equals(currentUserId)) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
            }
        }

        TextView messageTextView = convertView.findViewById(R.id.text_message_body);
        messageTextView.setText(chat.getMessage());

        return convertView;
    }
}