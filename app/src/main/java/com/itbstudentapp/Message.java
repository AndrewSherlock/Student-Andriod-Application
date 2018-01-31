package com.itbstudentapp;

public class Message {

    public String sender;
    public String message;
    public long currentTime;

    public Message(){} // must be done for firebase

    public Message(String sender, String message, long currentTime)
    {
        this.sender = sender;
        this.message = message;
        this.currentTime = currentTime;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
}
