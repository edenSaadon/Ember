package com.example.ember.Models;

public class Chat {
    private String chatId; // מזהה ייחודי לצ'אט
    private String senderId;
    private String receiverId;
    private String message;
    private long timestamp;

    public Chat() {
        // נדרש עבור DataSnapshot
    }

    public Chat(String chatId, String senderId, String receiverId, String message, long timestamp) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
    }

    // פונקציית החזר למזהה הצ'אט
    public String getChatId() {
        return chatId;
    }

    // פונקציית עדכון למזהה הצ'אט
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    // פונקציות החזר ועדכון לשולח
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    // פונקציות החזר ועדכון למקבל
    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    // פונקציות החזר ועדכון להודעה
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // פונקציות החזר ועדכון לזמן
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
