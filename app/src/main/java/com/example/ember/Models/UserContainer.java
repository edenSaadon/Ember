package com.example.ember.Models;

import java.util.HashMap;

public class UserContainer {
    private HashMap<String, User> usersMap;

    public UserContainer() {
        this.usersMap = new HashMap<>();
    }

    public HashMap<String, User> getUsersMap() {
        return usersMap;
    }

    public void setUsersMap(HashMap<String, User> usersMap) {
        this.usersMap = usersMap;
    }

    public void addUser(String userId, User user) {
        usersMap.put(userId, user);
    }

    public User getUser(String userId) {
        return usersMap.get(userId);
    }
}
