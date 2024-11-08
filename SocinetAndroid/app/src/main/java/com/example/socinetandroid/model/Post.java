package com.example.socinetandroid.model;

import java.util.Date;
import java.util.Map;

public class Post {
    long id;
    User user;
    String caption;
    String imageUrl;
    String videoUrl;
    Post sharedPost;
    int numberOfComments;
    Map<String, Integer> numberOfReactions;
    boolean isActive;
    Date createdAt;
    Date updatedAt;
    String selfReaction;

    public Post(long id, User user, String caption, String imageUrl, String videoUrl, Post sharedPost, int numberOfComments, Map<String, Integer> numberOfReactions, boolean isActive, Date createdAt, Date updatedAt, String selfReaction) {
        this.id = id;
        this.user = user;
        this.caption = caption;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.sharedPost = sharedPost;
        this.numberOfComments = numberOfComments;
        this.numberOfReactions = numberOfReactions;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.selfReaction = selfReaction;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", user=" + user +
                ", caption='" + caption + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", sharedPost=" + sharedPost +
                ", numberOfComments=" + numberOfComments +
                ", numberOfReactions=" + numberOfReactions +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", selfReaction='" + selfReaction + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Post getSharedPost() {
        return sharedPost;
    }

    public void setSharedPost(Post sharedPost) {
        this.sharedPost = sharedPost;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public Map<String, Integer> getNumberOfReactions() {
        return numberOfReactions;
    }

    public void setNumberOfReactions(Map<String, Integer> numberOfReactions) {
        this.numberOfReactions = numberOfReactions;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSelfReaction() {
        return selfReaction;
    }

    public void setSelfReaction(String selfReaction) {
        this.selfReaction = selfReaction;
    }
}
