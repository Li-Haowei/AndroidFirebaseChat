package com.example.androidfirebasechat.messages;

public class MessagesList {
    private String name, mobile, lastMessage, profilePic;

    private  int unseenMessages;

    public MessagesList(String name, String mobile, String lastMessage, String profilePic, int unseenMessages) {
        this.name = name;
        this.mobile = mobile;
        this.lastMessage = lastMessage;
        this.profilePic = profilePic;
        this.unseenMessages = unseenMessages;
    }

    public String getName() {
        return name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public void setUnseenMessages(int unseenMessages) {
        this.unseenMessages = unseenMessages;
    }
}
