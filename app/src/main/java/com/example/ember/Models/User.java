package com.example.ember.Models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String sexualPreference;
    private String gender;
    private int age;
    private String hobby;
    private String status;
    private String lookingFor;
    private String birthdate;
    private String partnerAgeRange;
    private String partnerLocationRange;
    private String partnerGender;
    private String aboutYourself;
    private double latitude;
    private double longitude;
    private int locationRange;
    private String imageUrl;
    private String cityName; // שדה לשם העיר
    private int imageStatus; // סטטוס התמונה (1 - לייק, 0 - דיסלייק, 2 - דיפולט)
    private List<String> likedUsers; // רשימת משתמשים שעשו לייק

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        likedUsers = new ArrayList<>(); // Initialize list
    }

    public User(String userId, String name, String email, String phone, String sexualPreference, String gender, int age, String hobby, String status, String lookingFor, String birthdate, String partnerAgeRange, String partnerLocationRange, String partnerGender, String aboutYourself, double latitude, double longitude, int locationRange, String imageUrl, String cityName, int imageStatus) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.sexualPreference = sexualPreference;
        this.gender = gender;
        this.age = age;
        this.hobby = hobby;
        this.status = status;
        this.lookingFor = lookingFor;
        this.birthdate = birthdate;
        this.partnerAgeRange = partnerAgeRange;
        this.partnerLocationRange = partnerLocationRange;
        this.partnerGender = partnerGender;
        this.aboutYourself = aboutYourself;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationRange = locationRange;
        this.imageUrl = imageUrl;
        this.cityName = cityName;
        this.imageStatus = imageStatus;
        this.likedUsers = new ArrayList<>(); // Initialize list
    }

    // Getters and setters for all fields
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSexualPreference() {
        return sexualPreference;
    }

    public void setSexualPreference(String sexualPreference) {
        this.sexualPreference = sexualPreference;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLookingFor() {
        return lookingFor;
    }

    public void setLookingFor(String lookingFor) {
        this.lookingFor = lookingFor;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getPartnerAgeRange() {
        return partnerAgeRange;
    }

    public void setPartnerAgeRange(String partnerAgeRange) {
        this.partnerAgeRange = partnerAgeRange;
    }

    public String getPartnerLocationRange() {
        return partnerLocationRange;
    }

    public void setPartnerLocationRange(String partnerLocationRange) {
        this.partnerLocationRange = partnerLocationRange;
    }

    public String getPartnerGender() {
        return partnerGender;
    }

    public void setPartnerGender(String partnerGender) {
        this.partnerGender = partnerGender;
    }

    public String getAboutYourself() {
        return aboutYourself;
    }

    public void setAboutYourself(String aboutYourself) {
        this.aboutYourself = aboutYourself;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getLocationRange() {
        return locationRange;
    }

    public void setLocationRange(int locationRange) {
        this.locationRange = locationRange;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getImageStatus() {
        return imageStatus;
    }

    public void setImageStatus(int imageStatus) {
        this.imageStatus = imageStatus;
    }

    public List<String> getLikedUsers() {
        if (likedUsers == null) {
            likedUsers = new ArrayList<>(); // Initialize if null
        }
        return likedUsers;
    }

    public void setLikedUsers(List<String> likedUsers) {
        this.likedUsers = likedUsers;
    }

    public void addLikedUser(String userId) {
        if (likedUsers == null) {
            likedUsers = new ArrayList<>(); // Initialize if null
        }
        if (!likedUsers.contains(userId)) {
            likedUsers.add(userId);
        }
    }

    // פונקציה שמחזירה את סטטוס התמונה
    public int getImageStatusForUser() {
        return imageStatus;
    }
}
