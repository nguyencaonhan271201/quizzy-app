package com.example.quizzyapplication_v2.Online;

import java.util.ArrayList;

public class Feed {
    private int topicID;
    private String topicName;
    private String feedContent;
    private String usernamePoster;
    private String avatarPoster;
    private String feedImage;
    private int likeCount;
    private ArrayList<Comment> comments;
    private boolean isLiked;
    private long timestamp;

    public Feed(int topicID, String topicName, String feedContent, String usernamePoster, String avatarPoster, String feedImage, int likeCount, ArrayList<Comment> comments, long timestamp) {
        this.topicID = topicID;
        this.topicName = topicName;
        this.feedContent = feedContent;
        this.usernamePoster = usernamePoster;
        this.avatarPoster = avatarPoster;
        this.feedImage = feedImage;
        this.likeCount = likeCount;
        this.isLiked = false;
        this.comments = comments;
        this.timestamp = timestamp;
    }

    public int getTopicID() {
        return topicID;
    }

    public void setTopicID(int topicID) {
        this.topicID = topicID;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getFeedContent() {
        return feedContent;
    }

    public void setFeedContent(String feedContent) {
        this.feedContent = feedContent;
    }

    public String getUsernamePoster() {
        return usernamePoster;
    }

    public void setUsernamePoster(String usernamePoster) {
        this.usernamePoster = usernamePoster;
    }

    public String getAvatarPoster() {
        return avatarPoster;
    }

    public void setAvatarPoster(String avatarPoster) {
        this.avatarPoster = avatarPoster;
    }

    public String getFeedImage() {
        return feedImage;
    }

    public void setFeedImage(String feedImage) {
        this.feedImage = feedImage;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
