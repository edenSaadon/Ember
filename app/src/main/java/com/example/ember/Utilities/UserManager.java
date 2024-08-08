package com.example.ember.Utilities;

import com.example.ember.Models.User;
import com.example.ember.Models.UserContainer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private UserContainer usersContainer;
    private DatabaseReference usersRef;
    private UsersManagerCallback callback;

    public UserManager(UsersManagerCallback callback) {
        this.usersContainer = new UserContainer();
        this.usersRef = FirebaseDatabase.getInstance().getReference("Users");
        this.callback = callback;
    }

    public interface UsersManagerCallback {
        void onUsersLoaded(UserContainer usersContainer);
        void onError(String error);
    }

    public void loadUsers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersContainer.getUsersMap().clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        usersContainer.addUser(user.getUserId(), user);
                    }
                }
                callback.onUsersLoaded(usersContainer);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    public List<User> filterUsersByPreferences(User currentUser) {
        List<User> filteredList = new ArrayList<>();
        for (User user : usersContainer.getUsersMap().values()) {
            if (!user.getUserId().equals(currentUser.getUserId())
                    && user.getSexualPreference().equals(currentUser.getSexualPreference())
                    && user.getGender().equals(currentUser.getPartnerGender())
                    && user.getAge() >= Integer.parseInt(currentUser.getPartnerAgeRange().split("-")[0])
                    && user.getAge() <= Integer.parseInt(currentUser.getPartnerAgeRange().split("-")[1])
                    && calculateDistance(currentUser.getLatitude(), currentUser.getLongitude(), user.getLatitude(), user.getLongitude()) <= currentUser.getLocationRange()) {
                filteredList.add(user);
            }
        }
        return filteredList;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to kilometers
        return distance;
    }
}
