package com.example.quizzyapplication_v2.Online;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Room implements Parcelable {
    private int id;
    private String player1;
    private String player2;
    private int topicID;
    private int numberOfQuest;
    private ArrayList<com.example.quizzyapplication_v2.Online.Question> questionInUse;
    private int player1Level; //For TopicRoomActivity listview
    private FirebaseDatabase database;
    private DatabaseReference roomRef;
    private int getCurrentRoomCount;

    public Room(int ID, int topicID, int numberOfQuest, String player1, String player2) {
        this.id = ID;
        this.topicID = topicID;
        this.numberOfQuest = numberOfQuest;
        this.player1 = player1;
        this.player2 = player2;
        this.questionInUse = new ArrayList<com.example.quizzyapplication_v2.Online.Question>();
        this.player1Level = 1;
    }

    @SuppressWarnings("unchecked")
    public Room(Parcel p)
    {
        this.id = p.readInt();
        this.player1 = p.readString();
        this.player2 = p.readString();
        this.topicID = p.readInt();
        this.numberOfQuest = p.readInt();
        this.questionInUse = new ArrayList<com.example.quizzyapplication_v2.Online.Question>();
        this.questionInUse = p.readArrayList(com.example.quizzyapplication_v2.Online.Question.class.getClassLoader());
        this.player1Level = p.readInt();
    }

    public void deleteRoom()
    {
        roomRef = database.getReference("rooms");
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getCurrentRoomCount = Integer.parseInt(String.valueOf(snapshot.child("rooms_count").getValue()));
                getCurrentRoomCount--;
                database.getReference("rooms/rooms_count").setValue(String.valueOf(getCurrentRoomCount));
                database.getReference("rooms/" + String.valueOf(getId())).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public int getTopicID() {
        return topicID;
    }

    public void setTopicID(int topicID) {
        this.topicID = topicID;
    }

    public int getNumberOfQuest() {
        return numberOfQuest;
    }

    public void setNumberOfQuest(int numberOfQuest) {
        this.numberOfQuest = numberOfQuest;
    }

    public ArrayList<com.example.quizzyapplication_v2.Online.Question> getQuestionInUse() {
        return questionInUse;
    }

    public void setQuestionInUse(ArrayList<com.example.quizzyapplication_v2.Online.Question> questionInUse) {
        this.questionInUse = questionInUse;
    }

    public int getPlayer1Level() {
        return player1Level;
    }

    public void setPlayer1Level(int player1Level) {
        this.player1Level = player1Level;
    }

    public void autoGetPlayer1Level()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference player1Level = database.getReference("users/" + getPlayer1() + "/level");
        player1Level.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setPlayer1Level(Integer.parseInt(String.valueOf(snapshot.getValue())));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
        public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.player1);
        parcel.writeString(this.player2);
        parcel.writeInt(this.topicID);
        parcel.writeInt(this.numberOfQuest);
        parcel.writeList(this.questionInUse);
        parcel.writeInt(this.player1Level);
    }
}
