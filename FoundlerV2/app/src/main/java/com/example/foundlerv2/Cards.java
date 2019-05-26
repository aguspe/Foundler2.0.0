package com.example.foundlerv2;

public class Cards {
    private String userId;
    private String name;
    private String profilePictureUrl;

    public  Cards (String userId, String name, String profilePictureUrl){
        this.userId = userId;
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePictureUrl(){
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
