package com.example.quizzyapplication_v2.Online;

public class Comment {
    private long feedID;
    private long commentTime;
    private String username;
    private String profileImage;
    private String content;
    private boolean isLiked;
    private int likeCount;

    public Comment(long feedID, long commentTime, String username, String profileImage, String content, boolean isLiked, int likeCount) {
        this.feedID = feedID;
        this.commentTime = commentTime;
        this.username = username;
        this.profileImage = profileImage;
        this.content = content;
        this.isLiked = isLiked;
        this.likeCount = likeCount;
    }

    public long getFeedID() {
        return feedID;
    }

    public void setFeedID(long feedID) {
        this.feedID = feedID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(long commentTime) {
        this.commentTime = commentTime;
    }
}
