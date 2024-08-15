package com.example.ember.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ember.Models.User;
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

        // טעינת תמונת המשתמש
        if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
            Picasso.get().load(user.getImageUrl()).into(holder.userImage);
        } else {
            holder.userImage.setImageResource(R.drawable.ic_profile); // תמונת ברירת מחדל
        }

        // הגדרת מידע על המשתמש שיוצג בלחיצה
        holder.userInfo.setText("Name: " + user.getName() + "\nAge: " + user.getAge() + "\nGender: " + user.getGender());

        // טיפול בלחיצה על התמונה להצגת/הסתרת המידע
        holder.userImage.setOnClickListener(v -> {
            if (holder.userInfo.getVisibility() == View.GONE) {
                holder.userInfo.setVisibility(View.VISIBLE);
            } else {
                holder.userInfo.setVisibility(View.GONE);
            }
        });

        // אפשרות ללחיצה על כל הפריט אם נדרש
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
        public TextView userInfo;

        public UserViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userInfo = itemView.findViewById(R.id.user_info); // בדוק שהטקסט מובא פה
        }
    }
}
