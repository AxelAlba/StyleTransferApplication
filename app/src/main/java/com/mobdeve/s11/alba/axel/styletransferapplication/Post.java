package com.mobdeve.s11.alba.axel.styletransferapplication;

import android.net.Uri;

public class Post {
    private int imageId, userImageId, numLikes;
    private String caption, location, username, datePosted;
    private boolean liked;
    private String imageURI;
    private Long timestamp;

    // Post to be stored in Firestore
    public Post(String imageURI, String username, String caption, String datePosted, int numLikes, boolean liked, Long timestamp) {
        this.imageURI = imageURI;
        this.username = username;
        this.caption = caption;
        this.datePosted = datePosted;
        this.numLikes = numLikes;
        this.liked = liked;
        this.timestamp = timestamp;
    }

    public Post(String username, String caption, String datePosted, int numLikes, boolean liked, Long timestamp) {
        this.username = username;
        this.caption = caption;
        this.datePosted = datePosted;
        this.numLikes = numLikes;
        this.liked = liked;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {

        return this.imageURI + " "+ this.username + " " + this.caption + " " + this.datePosted + " " + this.numLikes + " " + this.liked;
    }

    public int getImageId() {
        return imageId;
    }

    public Long getTimestamp() { return timestamp; }

    public String getImageURI() { return imageURI; }

    public int getNumLikes() { return numLikes; }

    public String getDatePosted() {
        return datePosted;
    }

    public String getCaption() {
        return caption;
    }

    public int getUserImageId() {
        return userImageId;
    }

    public String getUsername() {
        return username;
    }

    public String getLocation() {
        return location;
    }

    public boolean getLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setImageURI(String uri) { this.imageURI = uri; }
}

