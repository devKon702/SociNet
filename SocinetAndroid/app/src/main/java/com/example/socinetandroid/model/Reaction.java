package com.example.socinetandroid.model;

public class Reaction {
    public static String LIKE_TYPE = "LIKE";
    public static String HAHA_TYPE = "HAHA";
    public static String LOVE_TYPE = "LOVE";
    public static String SAD_TYPE = "SAD";
    User user;
    String reactType;

    // Update
    Post post;

    public Reaction(User user, String reactType, Post post) {
        this.user = user;
        this.reactType = reactType;
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public String getReactType() {
        return reactType;
    }

    public Post getPost() {
        return post;
    }
}
