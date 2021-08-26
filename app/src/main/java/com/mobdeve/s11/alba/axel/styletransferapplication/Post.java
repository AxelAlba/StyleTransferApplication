package com.mobdeve.s11.alba.axel.styletransferapplication;

public class Post {
    private int imageId, userImageId;
    private String caption, location, username, datePosted;
    private boolean liked;

    public Post(int imageId, String datePosted, String caption, String location, boolean liked, String username, int userImageId) {
        this.imageId = imageId;
        this.datePosted = datePosted;
        this.caption = caption;
        this.location = location;
        this.liked = liked;
        this.username = username;
        this.userImageId = userImageId;
    }

    public int getImageId() {
        return imageId;
    }

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
}

