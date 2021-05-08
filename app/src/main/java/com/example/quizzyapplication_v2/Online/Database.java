package com.example.quizzyapplication_v2.Online;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Database {
    private FirebaseDatabase database;
    private static Database instance;
    private ArrayList<Topic> topicsList;

    public Database()
    {
        database = FirebaseDatabase.getInstance();
        topicsList = new ArrayList<Topic>();
        database.getReference("topics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot topic : snapshot.getChildren()) {
                    com.example.quizzyapplication_v2.Online.Topic tmp = new com.example.quizzyapplication_v2.Online.Topic();
                    tmp.setId(Integer.parseInt(topic.getKey()));
                    tmp.setName((String) topic.child("name").getValue());
                    tmp.setImage((String) topic.child("image").getValue());
                    topicsList.add(tmp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    static public Database getInstance()
    {
        if (instance == null)
            instance = new Database();
        return instance;
    }

    public ArrayList<Topic> getTopicsList() {
        return topicsList;
    }

    public void setTopicsList(ArrayList<Topic> topicsList) {
        this.topicsList = topicsList;
    }

    public void createRoom(int roomID, int getCurrentRoomCount, int topicID, int numberOfQuest)
    {
        database.getReference("rooms/rooms_count").setValue(String.valueOf(getCurrentRoomCount));
        database.getReference("rooms/" + roomID + "/topic_id").setValue(String.valueOf(topicID));
        database.getReference("rooms/" + roomID + "/number_of_quest").setValue(numberOfQuest);
        database.getReference("rooms/" + roomID + "/player1").setValue(ThisUser.getInstance(null).getUsername());
        database.getReference("rooms/" + roomID + "/player2").setValue("");
        database.getReference("rooms/" + roomID + "/controls/isPlayer1Ready").setValue(0);
        database.getReference("rooms/" + roomID + "/controls/isPlayer2Ready").setValue(0);
        database.getReference("rooms/" + roomID + "/controls/isPlayer1Answered").setValue(0);
        database.getReference("rooms/" + roomID + "/controls/isPlayer2Answered").setValue(0);
        database.getReference("rooms/" + roomID + "/controls/player1Point").setValue(0);
        database.getReference("rooms/" + roomID + "/controls/player2Point").setValue(0);
        database.getReference("rooms/" + roomID + "/controls/isPlayer1Start").setValue(0);
        database.getReference("rooms/" + roomID + "/controls/isPlayer2Start").setValue(0);
        database.getReference("rooms/" + roomID + "/controls/player1Power").setValue(-1);
        database.getReference("rooms/" + roomID + "/controls/player2Power").setValue(-1);
    }

    public void addRoomQuestions(int roomID, int numberOfQuest, ArrayList<Integer> questionIDs)
    {
        for (int i = 0; i < numberOfQuest; i++)
        {
            database.getReference("rooms/" + roomID + "/questions").push().setValue(questionIDs.get(i));
        }
    }

    public void gamePreChangeQuest(boolean isPlayer1, int roomID)
    {
        if (isPlayer1)
        {
            database.getReference("rooms/" + roomID + "/controls/isPlayer1Ready").setValue(1);
            database.getReference("rooms/" + roomID + "/controls/isPlayer1Answered").setValue(0);
        }
        else
        {
            database.getReference("rooms/" + roomID + "/controls/isPlayer2Ready").setValue(1);
            database.getReference("rooms/" + roomID + "/controls/isPlayer2Answered").setValue(0);
        }
    }

    public void gameFinishQuest(boolean isPlayer1, int roomID, int point)
    {
        if (isPlayer1)
        {
            database.getReference("rooms/" + roomID + "/controls/player1Point").setValue(point);
            database.getReference("rooms/" + roomID + "/controls/isPlayer1Answered").setValue(1);
            database.getReference("rooms/" + roomID + "/controls/player1Power").setValue(-1);
        }
        else
        {
            database.getReference("rooms/" + roomID + "/controls/player2Point").setValue(point);
            database.getReference("rooms/" + roomID + "/controls/isPlayer2Answered").setValue(1);
            database.getReference("rooms/" + roomID + "/controls/player2Power").setValue(-1);
        }
    }

    public void updatePostGameResult()
    {
        ThisUser me = ThisUser.getInstance(null);
        database.getReference("users/" + me.getUsername() + "/level").setValue(me.getLevel());
        database.getReference("users/" + me.getUsername() + "/exp").setValue(me.getExp());
        database.getReference("users/" + me.getUsername() + "/coin").setValue(me.getCoin());
    }

    public void setReady(int roomID, boolean isPlayer1) {
        if (isPlayer1)
            database.getReference("rooms/" + roomID + "/controls/isPlayer1Start").setValue(1);
        else
            database.getReference("rooms/" + roomID + "/controls/isPlayer2Start").setValue(1);
    }

    public void signOut()
    {
        database.getReference("users/" + ThisUser.getInstance(null).getUsername() + "/isLoggedIn").setValue(0);
    }

    public void updateRoomUsePower(boolean isPlayer1, int roomID, int powerID) {
        if (isPlayer1)
            database.getReference("rooms/" + roomID + "/controls/player1Power").setValue(powerID);
        else
            database.getReference("rooms/" + roomID + "/controls/player2Power").setValue(powerID);
        int currentQuantity = ThisUser.getInstance(null).getPowersList().get(powerID).getQuantity();
        database.getReference("users/" + ThisUser.getInstance(null).getUsername() + "/powers/power" + String.valueOf(powerID)).setValue(currentQuantity - 1);
        ThisUser.getInstance(null).getPowersList().get(powerID).setQuantity(currentQuantity - 1);
    }

    public void addFavoriteTopic(Topic topic) {
        ArrayList<com.example.quizzyapplication_v2.Online.Topic> favTopics = ThisUser.getInstance(null).getFavoriteTopics();
        for (int i = 0; i < favTopics.size(); i++)
        {
            if (favTopics.get(i).getId() == topic.getId())
                return;
        }
        favTopics.add(topic);
        ThisUser.getInstance(null).setFavoriteTopics(favTopics);
        database.getReference("users/" + ThisUser.getInstance(null).getUsername() + "/favTopics").push().setValue(topic.getId());
    }

    public void deleteFavoriteTopic(Topic topic) {
        ArrayList<com.example.quizzyapplication_v2.Online.Topic> favTopics = ThisUser.getInstance(null).getFavoriteTopics();
        for (int i = 0; i < favTopics.size(); i++)
        {
            if (favTopics.get(i).getId() == topic.getId())
            {
                favTopics.remove(i);
                break;
            }
        }
        ThisUser.getInstance(null).setFavoriteTopics(favTopics);
        database.getReference("users/" + ThisUser.getInstance(null).getUsername() + "/favTopics").removeValue();
        for (int i = 0; i < favTopics.size(); i++)
            database.getReference("users/" + ThisUser.getInstance(null).getUsername() + "/favTopics").push().setValue(favTopics.get(i).getId());
    }

    public void addStatistic(int topicID, int correctQuest, int totalQuest, int status, int point) {
        String currentTime = String.valueOf(System.currentTimeMillis());
        Statistic statistic = new Statistic(topicID, ThisUser.getInstance(null).getUsername(), status,  point, correctQuest, totalQuest);
        database.getReference("statistics/" + currentTime).setValue(statistic);
    }

    public void updatePost(String feedTopic, String feedImageURL, String feedContentString) {
        String currentTime = String.valueOf(System.currentTimeMillis());
        int topicID = -1;
        for (int i = 0; i < topicsList.size(); i++)
        {
            if (topicsList.get(i).getName().equals(feedTopic))
            {
                topicID = i;
                break;
            }
        }
        database.getReference("feeds/" + currentTime + "/topicID").setValue(String.valueOf(topicID + 1));
        database.getReference("feeds/" + currentTime + "/topicName").setValue(feedTopic);
        database.getReference("feeds/" + currentTime + "/feedContent").setValue(feedContentString);
        database.getReference("feeds/" + currentTime + "/usernamePoster").setValue(ThisUser.getInstance(null).getUsername());
        database.getReference("feeds/" + currentTime + "/feedImage").setValue(feedImageURL);
        database.getReference("feeds/" + currentTime + "/avatarPoster").setValue(ThisUser.getInstance(null).getProfileImage());
    }

    public void likePost(final Feed f) {
        database.getReference("feeds/" + f.getTimestamp() + "/like/" + ThisUser.getInstance(null).getUsername()).setValue(ThisUser.getInstance(null).getUsername());
    }

    public void unlikePost(final Feed f) {
        database.getReference("feeds/" + f.getTimestamp() + "/like/" + ThisUser.getInstance(null).getUsername()).removeValue();
    }

    public void editPost(Feed f) {
        database.getReference("feeds/" + f.getTimestamp() + "/feedContent").setValue(f.getFeedContent());
        database.getReference("feeds/" + f.getTimestamp() + "/feedImage").setValue(f.getFeedImage());
    }

    public void deletePost(Feed f) {
        database.getReference("feeds/" + f.getTimestamp()).removeValue();
    }

    public void likeComment(Comment comment) {
        database.getReference("feeds/" + comment.getFeedID() + "/comments/" + comment.getCommentTime() + "/like/"
                + ThisUser.getInstance(null).getUsername()).setValue(ThisUser.getInstance(null).getUsername());
    }

    public void unlikeComment(Comment comment) {
        database.getReference("feeds/" + comment.getFeedID() + "/comments/" + comment.getCommentTime() + "/like/"
                + ThisUser.getInstance(null).getUsername()).removeValue();
    }

    public void deleteComment(Comment comment) {
        database.getReference("feeds/" + comment.getFeedID() + "/comments/" + comment.getCommentTime()).removeValue();
    }

    public void addComment(Comment comment) {
        String currentTime = String.valueOf(System.currentTimeMillis());
        String PATH = "feeds/" + comment.getFeedID() + "/comments/" + currentTime;
        database.getReference(PATH+ "/username").setValue(String.valueOf(comment.getUsername()));
        database.getReference(PATH + "/profile").setValue(comment.getProfileImage());
        database.getReference(PATH+ "/content").setValue(comment.getContent());
    }
}
