package com.example.quizzyapplication_v2.Offline;

public class Record {
    private int id;
    private int topicID;
    private int point;
    private int correctQuest;
    private int totalQuest;

    public Record(){};

    public Record(int topicID, int point, int correctQuest, int totalQuest) {
        this.topicID = topicID;
        this.point = point;
        this.correctQuest = correctQuest;
        this.totalQuest = totalQuest;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTopicID() {
        return topicID;
    }

    public void setTopicID(int topicID) {
        this.topicID = topicID;
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
