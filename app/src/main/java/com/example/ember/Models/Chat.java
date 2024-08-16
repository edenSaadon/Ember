package com.example.ember.Models;

public class Chat {
    private String chatId;
    private String senderId;
    private String receiverId;
    private String message;
    private long timestamp;

    public Chat() {
        // Required for DataSnapshot
    }

    public Chat(String chatId, String senderId, String receiverId, String message, long timestamp) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Return function to chat ID
    public String getChatId() {
        return chatId;
    }

    // Chat ID update function
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    // Return and update functions to sender
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    // Refund and update functions for recipient
    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    // Notification refund and update functions
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Time-return and update functions
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
