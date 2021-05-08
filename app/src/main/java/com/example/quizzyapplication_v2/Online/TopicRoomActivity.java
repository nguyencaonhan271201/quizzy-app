package com.example.quizzyapplication_v2.Online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.quizzyapplication_v2.R;
import com.example.quizzyapplication_v2.SquareImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TopicRoomActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference rooms;
    private ArrayList<Room> roomList;
    private TextView txtTopicRoomName;
    private SquareImageView imageTopic;
    private RelativeLayout lnloNoRoom;
    private ListView lstViewRoom;
    private RoomAdapter adapter;
    private String imageURL;
    private String topicName;
    private int topicID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        supportPostponeEnterTransition();
        postponeEnterTransition();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_topic_room);

        topicID = getIntent().getIntExtra("topicID", -1);

        txtTopicRoomName = findViewById(R.id.topicRoomName);
        imageTopic = findViewById(R.id.topicImage);
        lstViewRoom = findViewById(R.id.lstRooms);
        lnloNoRoom = findViewById(R.id.lnloNoRooms);

        imageURL = getIntent().getStringExtra("topicImage");
        topicName = getIntent().getStringExtra("topicName");

        txtTopicRoomName.setText(topicName);
        Picasso.get().load(imageURL).error(R.drawable.ic_launcher_foreground).fit().into(imageTopic);

        startPostponedEnterTransition();

        database = FirebaseDatabase.getInstance();

        addRoomsEventListener();
    }

    private void addRoomsEventListener() {
        rooms = database.getReference("rooms");
        rooms.orderByChild("topic_id").equalTo(String.valueOf(topicID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (roomList == null)
                    roomList = new ArrayList<Room>();
                else roomList.clear();
                for (DataSnapshot room : snapshot.getChildren())
                {
                    String getRoom = String.valueOf(room.getKey());
                    if (!getRoom.equals("rooms_count"))
                    {
                        if (room.hasChild("player2") && ((String)room.child("player2").getValue()).equals(""))
                        {
                            int roomID = Integer.parseInt((String)room.getKey());
                            int numberOfQuest = Integer.parseInt(String.valueOf(room.child("number_of_quest").getValue()));
                            int topic_id = Integer.parseInt(String.valueOf(room.child("topic_id").getValue()));
                            String player1 = (String)room.child("player1").getValue();
                            DatabaseReference getPlayer1Level = database.getReference("users/" + player1 + "/level");
                            Room newRoom = new Room(roomID, topic_id, numberOfQuest, player1, "");
                            newRoom.autoGetPlayer1Level();
                            roomList.add(newRoom);
                        }
                    }
                }
                if (roomList.size() == 0)
                {
                    lstViewRoom.setVisibility(View.INVISIBLE);
                    lnloNoRoom.setVisibility(View.VISIBLE);
                }
                else
                {
                    CountDownTimer tmp = new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            lstViewRoom.setVisibility(View.VISIBLE);
                            lnloNoRoom.setVisibility(View.INVISIBLE);
                            adapter = new RoomAdapter(getApplicationContext(), R.layout.room_lstview_item, roomList);
                            lstViewRoom.setAdapter(adapter);
                            lstViewRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent preGame = new Intent(getApplicationContext(), com.example.quizzyapplication_v2.Online.PreGameActivity.class);
                                    preGame.putExtra("roomID", roomList.get(i).getId());
                                    preGame.putExtra("preGameType", 1); //0 for player 1, 1 for player 2
                                    preGame.putExtra("topicID", topicID);
                                    preGame.putExtra("topicName", topicName);
                                    startActivity(preGame);
                                    finish();
                                }
                            });
                        }
                    }.start();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    public void createRoom(View view) {
        ActivityOptionsCompat activityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(com.example.quizzyapplication_v2.Online.TopicRoomActivity.this,
                        (CardView)findViewById(R.id.topicImageCard), "imageTransition1");
        Intent createRoom = new Intent(this, CreateRoomActivity.class);
        createRoom.putExtra("topicID", topicID);
        createRoom.putExtra("topicImage", imageURL);
        createRoom.putExtra("topicName", topicName);
        startActivity(createRoom, activityOptionsCompat.toBundle());
        finish();
    }
}