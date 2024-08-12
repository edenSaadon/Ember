package com.example.ember.Models;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ember.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private static final String TAG = "UserAdapter";
    private List<String> imageUrls;
    private List<String> userIds;
    private List<Integer> imageStatus; // 0 = Disliked, 1 = Liked, 2 = Undecided
    private OnLikeDislikeListener likeDislikeListener; // Listener for like/dislike actions

    public UserAdapter(List<String> imageUrls, List<String> userIds, OnLikeDislikeListener likeDislikeListener) {
        this.imageUrls = new ArrayList<>(imageUrls);
        this.userIds = new ArrayList<>(userIds);
        this.imageStatus = new ArrayList<>(Collections.nCopies(imageUrls.size(), 2)); // Initialize with 2 (Undecided)
        this.likeDislikeListener = likeDislikeListener; // Initialize the listener
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
        Log.d(TAG, "Loading image URL: " + imageUrl);

        if (imageUrl == null || imageUrl.isEmpty()) {
            Log.e(TAG, "Empty or null imageUrl for position: " + position);
            return;
        }

        // Load image using Picasso with error handling
        Picasso.get()
                .load(imageUrl)
                .error(R.drawable.ic_profile) // Image shown on error
                .into(holder.userImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Image loaded successfully: " + imageUrl);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Error loading image: " + imageUrl, e);
                        // If there's an error, set the placeholder image manually
                        holder.userImage.setImageResource(R.drawable.ic_profile);
                    }
                });

        holder.btnLike.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(), "Liked user", Toast.LENGTH_SHORT).show();
            imageStatus.set(position, 1); // Set status to liked
            if (likeDislikeListener != null) {
                likeDislikeListener.onLike(position); // Notify listener about the like
            }
            removeUserAt(position); // Remove the liked user from the list
        });

        holder.btnDislike.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(), "Disliked user", Toast.LENGTH_SHORT).show();
            imageStatus.set(position, 0); // Set status to disliked
            if (likeDislikeListener != null) {
                likeDislikeListener.onDislike(position); // Notify listener about the dislike
            }
            removeUserAt(position); // Remove the disliked user from the list
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size(); // Return the current size of the list
    }

    public void removeUserAt(int position) {
        if (position < 0 || position >= imageUrls.size()) return;
        imageUrls.remove(position);
        userIds.remove(position);
        imageStatus.remove(position);
        notifyItemRemoved(position);
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

    public interface OnLikeDislikeListener {
        void onLike(int position);
        void onDislike(int position);
    }
}
