package com.example.quizzyapplication_v2.Online;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Question implements Parcelable {
    private int id;
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private int answerNr;
    private int topicID;
    private String image;

    public Question() {}

    public Question(String question, String option1, String option2, String option3, String option4,
                    int answerNr, int topicID, String image) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.answerNr = answerNr;
        this.topicID = topicID;
        this.image = image;
    }

    public Question(Parcel in) {
        this.id = in.readInt();
        this.question = in.readString();
        this.option1 = in.readString();
        this.option2 = in.readString();
        this.option3 = in.readString();
        this.option4 = in.readString();
        this.answerNr = in.readInt();
        this.topicID = in.readInt();
        this.image = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public int getAnswerNr() {
        return answerNr;
    }

    public void setAnswerNr(int answerNr) {
        this.answerNr = answerNr;
    }

    public int getTopicID() {
        return topicID;
    }

    public void setTopicID(int categoryID) {
        this.topicID = categoryID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void autoRetrieveValue()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref;
        ref = database.getReference("questions/" + getId());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setId(Integer.parseInt(snapshot.getKey()));
                setQuestion((String)snapshot.child("question").getValue());
                setOption1((String)snapshot.child("option1").getValue());
                setOption2((String)snapshot.child("option2").getValue());
                setOption3((String)snapshot.child("option3").getValue());
                setOption4((String)snapshot.child("option4").getValue());
                setAnswerNr(Integer.parseInt((String)snapshot.child("answer_no").getValue()));
                setTopicID(Integer.parseInt((String)snapshot.child("topic_id").getValue()));
                setImage((String)snapshot.child("image").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(question);
        parcel.writeString(option1);
        parcel.writeString(option2);
        parcel.writeString(option3);
        parcel.writeString(option4);
        parcel.writeInt(answerNr);
        parcel.writeInt(topicID);
        parcel.writeString(image);
    }
}