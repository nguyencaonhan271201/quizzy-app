package com.example.quizzyapplication_v2.Online;

public class Statistic {
    private int topicID;
    private String username;
    private int status; //0 for win, 1 for tie, 2 for lose
    private int point;
    private int correctQuest;
    private int totalQuest;

    public Statistic(int topicID, String username, int status, int point, int correctQuest, int totalQuest) {
        this.topicID = topicID;
        this.username = username;
        this.status = status;
        this.point = point;
        this.correctQuest = correctQuest;
        this.totalQuest = totalQuest;
    }

    public int getTopicID() {
        return topicID;
    }

    public void setTopicID(int topicID) {
        this.topicID = topicID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getCorrectQuest() {
        return correctQuest;
    }

    public void setCorrectQuest(int correctQuest) {
        this.correctQuest = correctQuest;
    }

    public int getTotalQuest() {
        return totalQuest;
    }

    public void setTotalQuest(int totalQuest) {
        this.totalQuest = totalQuest;
    }

    public double getRatio()
    {
        return (double)correctQuest / totalQuest;
    }
}
