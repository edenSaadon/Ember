package com.example.ember.Models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ember.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<String> imageUrls;
    private List<Integer> imageStatus; // 0 = Disliked, 1 = Liked, 2 = Undecided

    public UserAdapter(List<String> imageUrls) {
        this.imageUrls = new ArrayList<>(imageUrls);
        this.imageStatus = new ArrayList<>(Collections.nCopies(imageUrls.size(), 2)); // Initialize with 2 (Undecided)
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        if (imageUrls.isEmpty()) {
            Toast.makeText(holder.itemView.getContext(), "No more images to show", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageUrl = imageUrls.get(position);
        Picasso.get().load(imageUrl).into(holder.userImage);

        holder.btnLike.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(), "Liked user", Toast.LENGTH_SHORT).show();
            imageStatus.set(position, 1); // Set status to liked
            moveToEndOfList(position); // Move user to end of list
            notifyItemRemoved(position); // Notify adapter that item was removed from this position
            notifyItemInserted(imageUrls.size() - 1); // Notify adapter that item was added to end
            notifyDataSetChanged(); // Refresh the list
        });

        holder.btnDislike.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(), "Disliked user", Toast.LENGTH_SHORT).show();
            removeFromList(position); // Remove user from list
            notifyItemRemoved(position); // Refresh the list
            notifyDataSetChanged(); // Refresh the list
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size() > 0 ? 1 : 0; // Display only one image at a time
    }

    private void moveToEndOfList(int position) {
        if (position < 0 || position >= imageUrls.size()) return;
        String imageUrl = imageUrls.get(position);
        int status = imageStatus.get(position);
        imageUrls.remove(position);
        imageStatus.remove(position);
        imageUrls.add(imageUrl);
        imageStatus.add(status);
    }

    private void removeFromList(int position) {
        if (position < 0 || position >= imageUrls.size()) return;
        imageUrls.remove(position);
        imageStatus.remove(position);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public ImageView userImage;
        public Button btnLike;
        public Button btnDislike;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            btnLike = itemView.findViewById(R.id.btn_like);
            btnDislike = itemView.findViewById(R.id.btn_dislike);
        }
    }
}
