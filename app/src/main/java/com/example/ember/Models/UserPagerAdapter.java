package com.example.ember.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.ember.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserPagerAdapter extends PagerAdapter {

    private Context context;
    private List<User> users;

    public UserPagerAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_pager, container, false);
        User user = users.get(position);

        ImageView userImage = view.findViewById(R.id.user_image);
        TextView userName = view.findViewById(R.id.user_name);

        Picasso.get().load(user.getImageUrl()).into(userImage);
        userName.setText(user.getName());

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}