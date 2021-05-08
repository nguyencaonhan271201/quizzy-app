package com.example.quizzyapplication_v2.Online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.quizzyapplication_v2.R;
import com.example.quizzyapplication_v2.SquareImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shawnlin.numberpicker.NumberPicker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class CreateRoomActivity extends AppCompatActivity {
    private int topicID;
    private String topicImage;
    private SquareImageView imgTopic;
    private NumberPicker noOfQuestPicker;
    private ArrayList<Integer> questionIDs;
    private FirebaseDatabase database;
    private ArrayList<Integer> roomIDs;
    private int getCurrentRoomCount;
    private int numberOfQuest;
    private String topicName;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportPostponeEnterTransition();
        postponeEnterTransition();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating...");

        setContentView(R.layout.activity_create_room);

        topicID = getIntent().getIntExtra("topicID", 0);
        topicImage = getIntent().getStringExtra("topicImage");
        topicName = getIntent().getStringExtra("topicName");
        imgTopic = findViewById(R.id.topicImageCreate);
        noOfQuestPicker = findViewById(R.id.npNoOfQuest);

        Picasso.get().load(topicImage).error(R.drawable.ic_launcher_foreground).fit().into(imgTopic);

        startPostponedEnterTransition();

        noOfQuestPicker.setMinValue(5);
        noOfQuestPicker.setMaxValue(15);
        noOfQuestPicker.setValue(7);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    int randomNumber;
    String myName;
    public void createRoom(View view) {
        progressDialog.show();
        database = FirebaseDatabase.getInstance();
        DatabaseReference roomRef = database.getReference("rooms");
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getCurrentRoomCount = Integer.parseInt((String)snapshot.child("rooms_count").getValue());
                roomIDs = new ArrayList<Integer>();
                for (DataSnapshot room : snapshot.getChildren())
                {
                    if (!((String)room.getKey()).equals("rooms_count"))
                    {
                        roomIDs.add(Integer.parseInt((String)room.getKey()));
                    }
                }
                getCurrentRoomCount++;
                Random random = new Random();
                do {
                    randomNumber = random.nextInt(100) + 0;
                }
                while (roomIDs.indexOf(randomNumber) != -1 && roomIDs.size() != 0);

                numberOfQuest = noOfQuestPicker.getValue();

                Database.getInstance().createRoom(randomNumber, getCurrentRoomCount, topicID, numberOfQuest);

                try {
                    setQuestions();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setQuestions()
    {
        questionIDs = new ArrayList<Integer>();
        DatabaseReference questions = database.getReference("questions");
        if (topicID != 11)
        {
            questions.orderByChild("topic_id").equalTo(String.valueOf(topicID))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot question : snapshot.getChildren())
                            {
                                questionIDs.add(Integer.parseInt(question.getKey()));
                            }
                            Collections.shuffle(questionIDs);
                            /*for (int i = 0; i < numberOfQuest; i++)
                            {
                                database.getReference("rooms/" + randomNumber + "/questions").push().setValue(questionIDs.get(i));
                            }*/
                            Database.getInstance().addRoomQuestions(randomNumber, numberOfQuest, questionIDs);
                            startPreGame();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        else
        {
            questions.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                    int totalQuest = (int)snapshot1.getChildrenCount();
                    ArrayList<Integer> quesIDs = new ArrayList<Integer>();
                    Random random = new Random();
                    int randomVal;
                    for (int i = 0; i < numberOfQuest; i++)
                    {
                        do {
                            randomVal = random.nextInt(totalQuest - 1) + 0;
                        }
                        while (quesIDs.indexOf(randomVal) == -1 && quesIDs.size() != 0);
                        database.getReference("rooms/" + randomNumber + "/questions").push().setValue(randomVal);
                    }
                    startPreGame();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void startPreGame() {
        progressDialog.hide();
        Intent preGame = new Intent(this, com.example.quizzyapplication_v2.Online.PreGameActivity.class);
        preGame.putExtra("roomID", randomNumber);
        preGame.putExtra("preGameType", 0); //0 for player 1, 1 for player 2
        preGame.putExtra("topicID", topicID);
        preGame.putExtra("topicName", topicName);
        startActivity(preGame);
        finish();
    }
}
