package com.mobdeve.s11.alba.axel.styletransferapplication;

import android.net.Uri;

public class Post {
    private int imageId, userImageId, numLikes;
    private String caption, location, username, datePosted;
    private boolean liked;
    private String imageURI;

    // Toy data constructor
    public Post(int imageId, String datePosted, String caption, String location, boolean liked, String username, int userImageId) {
        this.imageId = imageId;
        this.datePosted = datePosted;
        this.caption = caption;
        this.location = location;
        this.liked = liked;
        this.username = username;
        this.userImageId = userImageId;
    }

    // Post to be stored in Firestore
    public Post(String imageURI, String username, String caption, String datePosted, int numLikes, boolean liked) {
        this.imageURI = imageURI;
        this.username = username;
        this.caption = caption;
        this.datePosted = datePosted;
        this.numLikes = numLikes;
        this.liked = liked;
    }

    public Post(String username, String caption, String datePosted, int numLikes, boolean liked) {
        this.username = username;
        this.caption = caption;
        this.datePosted = datePosted;
        this.numLikes = numLikes;
        this.liked = liked;
    }

    public int getImageId() {
        return imageId;
    }

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

