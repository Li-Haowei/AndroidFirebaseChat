package com.example.androidfirebasechat.chat;

public class ChatList {

    private String mobile, name, message, time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ChatList(String mobile, String name, String message, String timestamp) {
        this.mobile = mobile;
        this.name = name;
        this.message = message;
        this.time = timestamp;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
