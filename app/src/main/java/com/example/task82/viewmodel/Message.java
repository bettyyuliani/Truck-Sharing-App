package com.example.task82.viewmodel;

public class Message {

    // message data variables
    private String message;
    private String message_ID;
    private String message_sender;
    private long message_time;

    // constructors
    public Message(String message, String message_ID, String message_sender, long message_time) {
        this.message = message;
        this.message_ID = message_ID;
        this.message_sender = message_sender;
        this.message_time = message_time;
    }

    // empty constructor. data will be loaded in with setters
    public Message() { }

    // ==== GETTERS AND SETTERS ====
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage_ID() {
        return message_ID;
    }

    public void setMessage_ID(String message_ID) {
        this.message_ID = message_ID;
    }

    public String getMessage_sender() {
        return message_sender;
    }

    public void setMessage_sender(String message_sender) {
        this.message_sender = message_sender;
    }

    public long getMessage_time() {
        return message_time;
    }

    public void setMessage_time(long message_time) {
        this.message_time = message_time;
    }
}
