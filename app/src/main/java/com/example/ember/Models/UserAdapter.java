package com.example.ember.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ember.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private OnItemClickListener listener;

    public UserAdapter(Context context, List<User> userList, OnItemClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText(user.getName());

        // וודא שה-ImageView משתמש נכון ב-Picasso כדי לטעון את התמונה
        if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
            Picasso.get().load(user.getImageUrl()).into(holder.userImage);
        } else {
            holder.userImage.setImageResource(R.drawable.ic_profile); // תמונה ברירת מחדל
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(user.getUserId()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String userId);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public ImageView userImage;
        public TextView userName;

        public UserViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.user_name);
        }
    }
}
