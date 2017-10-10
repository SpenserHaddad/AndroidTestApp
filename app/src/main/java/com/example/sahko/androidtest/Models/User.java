package com.example.sahko.androidtest.Models;

/**
 * Created by sahko on 10/10/2017.
 */

public class User {
    private String full_name;
    private String profile_picture;

    private String uid;

    public User() {

    }

    public User(String full_name, String profile_picture, String uid) {
        this.full_name = full_name;
        this.profile_picture = profile_picture;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }
}
