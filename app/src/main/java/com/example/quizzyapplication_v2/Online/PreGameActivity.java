package com.example.quizzyapplication_v2.Online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizzyapplication_v2.Online.Question;
import com.example.quizzyapplication_v2.R;
import com.example.quizzyapplication_v2.SquareImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PreGameActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private int getCurrentRoomCount;
    private int roomID;
    private TextView roomTitle;
    private Room mainCompeteRoom;
    private int preGameType;
    private DatabaseReference player2;
    private String player1;
    private int topicID;
    private String topicName;
    public static boolean onRoomDelete = false;
    private User player1User;
    private User player2User;
    private DatabaseReference player1Ready;
    private DatabaseReference player2Ready;
    private ArrayList<Boolean> powerChosen;
    private ArrayList<Power> type1;
    private ArrayList<Power> type2;
    private ArrayList<Power> totalPower;
    private boolean isplayer1Ready = false;
    private boolean isplayer2Ready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_pre_game2);
        onRoomDelete = false;
        roomID = getIntent().getIntExtra("roomID", 0);
        roomTitle = findViewById(R.id.txtRoomTitle);
        roomTitle.setText(getResources().getString(R.string.room) + " " + String.valueOf(roomID));
        database = FirebaseDatabase.getInstance();
        topicID = getIntent().getIntExtra("topicID", 0);
        topicName = getIntent().getStringExtra("topicName");
        //Check type of pregame
        preGameType = getIntent().getIntExtra("preGameType", 0);
        powerChosen = new ArrayList<Boolean>();
        for (int i = 0; i < 8; i++) powerChosen.add(false);
        type1 = new ArrayList<Power>();
        type2 = new ArrayList<Power>();
        initListView();
        switch (preGameType)
        {
            case 0:
                ((LinearLayout)findViewById(R.id.lnloPlayer1)).setVisibility(View.VISIBLE);
                ((LinearLayout)findViewById(R.id.lnloPlayer2)).setVisibility(View.INVISIBLE);
                ((Button)findViewById(R.id.closeRoom)).setVisibility(View.VISIBLE);
                ThisUser me = ThisUser.getInstance(getApplicationContext());
                ((TextView)findViewById(R.id.txtPlayer1Name)).setText(me.getUsername());
                ((TextView)findViewById(R.id.txtPlayer1Level)).setText(getResources().getString(R.string.level) + ": " + String.valueOf(me.getLevel()));
                ((TextView)findViewById(R.id.txtPlayer1Exp)).setText("Exp: " + String.valueOf(me.getExp()));
                Picasso.get().load(me.getProfileImage()).error(R.drawable.default_ava).into((SquareImageView)findViewById(R.id.imgPlayer1));
                player1User = new User(me.getUsername(), me.getEmail(), me.getExp(), me.getLevel(), me.getProfileImage());
                break;
            case 1:
                ((LinearLayout)findViewById(R.id.lnloPlayer1)).setVisibility(View.INVISIBLE);
                ((LinearLayout)findViewById(R.id.lnloPlayer2)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.txtWaiting)).setVisibility(View.INVISIBLE);
                ((Button)findViewById(R.id.closeRoom)).setVisibility(View.INVISIBLE);
                ThisUser me1 = ThisUser.getInstance(getApplicationContext());
                ((TextView)findViewById(R.id.txtPlayer2Name)).setText(me1.getUsername());
                ((TextView)findViewById(R.id.txtPlayer2Level)).setText(getResources().getString(R.string.level) + ": " + String.valueOf(me1.getLevel()));
                ((TextView)findViewById(R.id.txtPlayer2Exp)).setText("Exp: " + String.valueOf(me1.getExp()));
                Picasso.get().load(me1.getProfileImage()).error(R.drawable.default_ava).into((SquareImageView)findViewById(R.id.imgPlayer2));
                player2User = new User(me1.getUsername(), me1.getEmail(), me1.getExp(), me1.getLevel(), me1.getProfileImage());
                break;
        }
        createRoomObject();
    }

    private void initListView() {
        ListView lv_pregame_power_1 = findViewById(R.id.lv_pregame_power_1);
        ListView lv_pregame_power_2 = findViewById(R.id.lv_pregame_power_2);

        type1.clear();
        type2.clear();

        totalPower = ThisUser.getInstance(this).getPowersList();
        for (int i = 0; i < totalPower.size(); i++)
        {
            if (totalPower.get(i).getQuantity() > 0)
            {
                if (i < 4)
                    type1.add(totalPower.get(i));
                else
                    type2.add(totalPower.get(i));
            }
        }

        PreGamePowerAdapter adapter1 = new PreGamePowerAdapter(this, R.layout.pregame_lsv_item, type1);
        PreGamePowerAdapter adapter2 = new PreGamePowerAdapter(this, R.layout.pregame_lsv_item, type2);

        if (type1.size() == 0)
        {
            ((TextView)findViewById(R.id.noDfsPowerPre)).setVisibility(View.VISIBLE);
            lv_pregame_power_1.setVisibility(View.INVISIBLE);
        }
        else lv_pregame_power_1.setAdapter(adapter1);
        if (type2.size() == 0)
        {
            ((TextView)findViewById(R.id.noAtkPowerPre)).setVisibility(View.VISIBLE);
            lv_pregame_power_2.setVisibility(View.INVISIBLE);
        }
        else lv_pregame_power_2.setAdapter(adapter2);

        lv_pregame_power_1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView tick = (ImageView)view.findViewById(R.id.tickChosen);
                if (tick.getVisibility() == View.VISIBLE)
                {
                    powerChosen.set(type1.get(position).getId(), false);
                    tick.setVisibility(View.INVISIBLE);
                }
                else
                {
                    powerChosen.set(type1.get(position).getId(), true);
                    tick.setVisibility(View.VISIBLE);
                }
            }
        });

        lv_pregame_power_2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView tick = (ImageView)view.findViewById(R.id.tickChosen);
                if (tick.getVisibility() == View.VISIBLE)
                {
                    powerChosen.set(type2.get(position).getId(), false);
                    tick.setVisibility(View.INVISIBLE);
                }
                else
                {
                    powerChosen.set(type2.get(position).getId(), true);
                    tick.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void addRoomEventListener() {
        player2 = database.getReference("rooms/" + roomID + "/player2");
        player2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!onRoomDelete)
                {
                    if (!((String)snapshot.getValue()).equals(""))
                    {
                        switch (preGameType)
                        {
                            case 0:
                                //startGame
                                String getPlayer2 = (String)snapshot.getValue();
                                ((Button)findViewById(R.id.closeRoom)).setVisibility(View.INVISIBLE);
                                ((Button)findViewById(R.id.btnReady)).setVisibility(View.VISIBLE);
                                DatabaseReference getPlayer2Info = database.getReference("users").child(getPlayer2);
                                getPlayer2Info.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ((TextView)findViewById(R.id.txtWaiting)).setVisibility(View.INVISIBLE);
                                        ((LinearLayout)findViewById(R.id.lnloPlayer2)).setVisibility(View.VISIBLE);
                                        String player2 = (String)snapshot.getKey();
                                        int player2Level = Integer.parseInt(String.valueOf(snapshot.child("level").getValue()));
                                        int player2Exp = Integer.parseInt(String.valueOf(snapshot.child("exp").getValue()));
                                        String player2Image = (String)snapshot.child("profileImage").getValue();
                                        ((TextView)findViewById(R.id.txtPlayer2Name)).setText(player2);
                                        ((TextView)findViewById(R.id.txtPlayer2Level)).setText(getResources().getString(R.string.level) + ": " + String.valueOf(player2Level));
                                        ((TextView)findViewById(R.id.txtPlayer2Exp)).setText("Exp: " + String.valueOf(player2Exp));
                                        Picasso.get().load(player2Image).error(R.drawable.default_ava).into((SquareImageView)findViewById(R.id.imgPlayer2));
                                        mainCompeteRoom.setPlayer2(player2);
                                        player2User = new User(player2, "", player2Exp, player2Level, player2Image);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                break;
                            case 1:
                                DatabaseReference getPlayer1Info = database.getReference("users").child(player1);
                                getPlayer1Info.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ((LinearLayout)findViewById(R.id.lnloPlayer1)).setVisibility(View.VISIBLE);
                                        ((Button)findViewById(R.id.btnReady)).setVisibility(View.VISIBLE);
                                        String player1 = (String)snapshot.getKey();
                                        int player1Level = Integer.parseInt(String.valueOf(snapshot.child("level").getValue()));
                                        int player1Exp = Integer.parseInt(String.valueOf(snapshot.child("exp").getValue()));
                                        String player1Image = (String)snapshot.child("profileImage").getValue();
                                        ((TextView)findViewById(R.id.txtPlayer1Name)).setText(player1);
                                        ((TextView)findViewById(R.id.txtPlayer1Level)).setText(getResources().getString(R.string.level) + ": " + String.valueOf(player1Level));
                                        ((TextView)findViewById(R.id.txtPlayer1Exp)).setText("Exp: " + String.valueOf(player1Exp));
                                        Picasso.get().load(player1Image).error(R.drawable.default_ava).into((SquareImageView)findViewById(R.id.imgPlayer1));
                                        player1User = new User(player1, "", player1Exp, player1Level, player1Image);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void moveToGame() {
        MediaPlayer mediaPlayer;
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.start);
        mediaPlayer.start();
        CountDownTimer timer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                androidx.core.util.Pair<View, String> p1 = androidx.core.util.Pair.create(findViewById(R.id.imgCardPlayer1), "player1Transition");
                androidx.core.util.Pair<View, String> p2 = androidx.core.util.Pair.create(findViewById(R.id.imgCardPlayer2), "player2Transition");
                ActivityOptionsCompat activityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(com.example.quizzyapplication_v2.Online.PreGameActivity.this, p1, p2);
                Intent gameIntent = new Intent(getApplicationContext(), com.example.quizzyapplication_v2.Online.GameActivity.class);
                gameIntent.putExtra("room", (Parcelable)mainCompeteRoom);
                gameIntent.putExtra("player1", (Parcelable)player1User);
                gameIntent.putExtra("player2", (Parcelable)player2User);
                gameIntent.putExtra("isPlayer1", preGameType == 0);
                gameIntent.putExtra("topicID", topicID);
                gameIntent.putExtra("topicName", topicName);
                ArrayList<Integer> powerIDs = new ArrayList<Integer>();
                for (int i = 0; i < powerChosen.size(); i++)
                {
                    if (powerChosen.get(i))
                        powerIDs.add(i);
                }
                gameIntent.putExtra("powerIDs", powerIDs);
                startActivity(gameIntent, activityOptionsCompat.toBundle());
                finish();
            }
        }.start();
    }

    private void createRoomObject() {
        DatabaseReference roomRef = database.getReference("rooms/" + roomID);
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberOfQuest = Integer.parseInt(String.valueOf(snapshot.child("number_of_quest").getValue()));
                player1 = (String)snapshot.child("player1").getValue();
                int topicID = Integer.parseInt((String)snapshot.child("topic_id").getValue());
                if (preGameType == 0)
                    mainCompeteRoom = new Room(roomID, topicID, numberOfQuest, player1, "");
                else
                {
                    mainCompeteRoom = new Room(roomID, topicID, numberOfQuest, player1, ThisUser.getInstance(getApplicationContext()).getUsername());
                    DatabaseReference setRef = database.getReference("rooms/" + roomID + "/player2");
                    setRef.setValue(ThisUser.getInstance(getApplicationContext()).getUsername());
                }
                DatabaseReference questionsRef = database.getReference("rooms/" + roomID + "/questions");
                questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<com.example.quizzyapplication_v2.Online.Question> questions = new ArrayList<com.example.quizzyapplication_v2.Online.Question>();
                        for (DataSnapshot getChild : snapshot.getChildren())
                        {
                            com.example.quizzyapplication_v2.Online.Question tmp = new com.example.quizzyapplication_v2.Online.Question();
                            tmp.setId(Integer.parseInt(String.valueOf(getChild.getValue())));
                            tmp.autoRetrieveValue();
                            questions.add(tmp);
                        }
                        mainCompeteRoom.setQuestionInUse(questions);
                        addRoomEventListener();
                        addReadyEventListener();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addReadyEventListener() {
        player1Ready = database.getReference("rooms/" + roomID + "/controls/isPlayer1Start");
        player1Ready.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!onRoomDelete)
                {
                    int get = Integer.parseInt(String.valueOf(snapshot.getValue()));
                    isplayer1Ready = get == 1;
                    if (isplayer1Ready && isplayer2Ready)
                        moveToGame();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        player2Ready = database.getReference("rooms/" + roomID + "/controls/isPlayer2Start");
        player2Ready.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!onRoomDelete)
                {
                    int get = Integer.parseInt(String.valueOf(snapshot.getValue()));
                    isplayer2Ready = get == 1;
                    if (isplayer1Ready && isplayer2Ready)
                        moveToGame();
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

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getResources().getString(R.string.preventBackPreGame), Toast.LENGTH_SHORT).show();
    }

    public void CloseRoom(View view) {
        onRoomDelete = true;
        DatabaseReference roomRef = database.getReference("rooms");
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getCurrentRoomCount = Integer.parseInt(String.valueOf(snapshot.child("rooms_count").getValue()));
                getCurrentRoomCount--;
                database.getReference("rooms/rooms_count").setValue(String.valueOf(getCurrentRoomCount));
                database.getReference("rooms/" + String.valueOf(roomID)).removeValue();
                Intent chooseIntent = new Intent(getApplicationContext(), com.example.quizzyapplication_v2.Online.ChooseActivity.class);
                startActivity(chooseIntent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void Ready(View view) {
        int count = 0;
        for (int i = 0; i < powerChosen.size(); i++)
        {
            if (powerChosen.get(i) == true)
                count++;
        }
        if (count > 4)
        {
            Toast.makeText(this, getResources().getString(R.string.powerLimitExceeded), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Database.getInstance().setReady(roomID, preGameType == 0);
            ((Button)findViewById(R.id.btnReady)).setVisibility(View.INVISIBLE);
            ((LinearLayout)findViewById(R.id.lnloPowerChoose)).setVisibility(View.GONE);
        }
    }
}