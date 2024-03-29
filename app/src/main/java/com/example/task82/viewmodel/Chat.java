package com.example.task82.viewmodel;

public class Chat {

    // chat data variables
    private  String username;
    private  String lastMessage;
    private  long lastMessageTime;

    // constructor
    public Chat(String username, String lastMessage, long lastMessageTime) {
        this.username = username;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    // ==== GETTERS AND SETTERS ====
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
