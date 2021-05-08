package com.example.quizzyapplication_v2.Online;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.quizzyapplication_v2.CustomDrawableProgressBar;
import com.example.quizzyapplication_v2.R;
import com.example.quizzyapplication_v2.SquareImageView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class GameActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private MediaPlayer mediaPlayer;
    private MediaPlayer subMediaPlayer;
    private MediaPlayer questMediaPlayer;
    private float px2dp;
    private ArrayList<Button> btnList = new ArrayList<>();
    private TextView txtQuestion;
    private CardView imgQuestionCard;
    private Button btnNextQuest;
    private List<com.example.quizzyapplication_v2.Online.Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private com.example.quizzyapplication_v2.Online.Question currentQuestion;
    private int correctOption;
    private int point;
    private boolean answered;
    private int topicID;
    private VideoView questionVideo;
    private String topicName;
    private CountDownTimer timer;
    private Room mainCompeteRoom;
    private User player1;
    private User player2;
    private boolean isPlayer1;
    private SquareImageView imgPlayer1;
    private TextView txtPlayer1Name;
    private TextView txtPlayer1Point;
    private SquareImageView imgPlayer2;
    private TextView txtPlayer2Name;
    private TextView txtPlayer2Point;
    private DatabaseReference player1ReadyRef;
    private DatabaseReference player2ReadyRef;
    private DatabaseReference player1AnsweredRef;
    private DatabaseReference player2AnsweredRef;
    private DatabaseReference player1Power;
    private DatabaseReference player2Power;
    private ArrayList<Integer> powerIDs;
    private ArrayList<Boolean> powerChosen;
    private LinearLayout lnloPower;
    private ArrayList<SquareImageView> imgPowers;
    private ArrayList<TextView> txtPowerQuantities;
    private ArrayList<RelativeLayout> rtloPowers;
    private ArrayList<Power> usablePowers;
    private int currentQuestPoint = 10;
    public static boolean onRoomFinished = false;
    private int questWaitTime;
    private int currentQuestInterval;
    private int correctQuest;
    private boolean isOpenPowerUseFragment = false;
    private int getPower1, getPower2;
    private TextView txtShortDescription;
    private ArrayList<String> powerShortDescriptions;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        supportPostponeEnterTransition();
        postponeEnterTransition();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_game2);

        onRoomFinished = false;

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.quizzy_background);
        mediaPlayer.setLooping(true);
        px2dp = getApplicationContext().getResources().getDisplayMetrics().density;

        btnList.add((Button) findViewById(R.id.btnOpt1));
        btnList.add((Button) findViewById(R.id.btnOpt2));
        btnList.add((Button) findViewById(R.id.btnOpt3));
        btnList.add((Button) findViewById(R.id.btnOpt4));

        txtQuestion = findViewById(R.id.txtQuestion);
        imgQuestionCard = findViewById(R.id.onlineQuestionImageCard);
        btnNextQuest = findViewById(R.id.btnOnlineNextQuest);

        imgPlayer1 = findViewById(R.id.imgGamePlayer1);
        txtPlayer1Name = findViewById(R.id.txtGamePlayer1);
        txtPlayer1Point = findViewById(R.id.txtGamePlayer1Point);
        imgPlayer2 = findViewById(R.id.imgGamePlayer2);
        txtPlayer2Name = findViewById(R.id.txtGamePlayer2);
        txtPlayer2Point = findViewById(R.id.txtGamePlayer2Point);
        questionVideo = findViewById(R.id.questionVideo);

        topicID = getIntent().getIntExtra("topicID", 0);
        topicName = getIntent().getStringExtra("topicName");

        database = FirebaseDatabase.getInstance();

        mainCompeteRoom = getIntent().getParcelableExtra("room");
        player1 = getIntent().getParcelableExtra("player1");
        player2 = getIntent().getParcelableExtra("player2");
        isPlayer1 = getIntent().getBooleanExtra("isPlayer1", true);

        Picasso.get().load(player1.getProfileImage()).error(R.drawable.default_ava)
                .into(imgPlayer1);
        Picasso.get().load(player2.getProfileImage()).error(R.drawable.default_ava)
                .into(imgPlayer2);

        startPostponedEnterTransition();
        txtPlayer1Name.setText(player1.getUsername());
        txtPlayer2Name.setText(player2.getUsername());

        questionCountTotal = mainCompeteRoom.getNumberOfQuest();
        questionList = mainCompeteRoom.getQuestionInUse();
        point = 0;
        questionCounter = 0;
        correctQuest = 0;
        mediaPlayer.start();

        powerShortDescriptions = new ArrayList<String>();
        powerShortDescriptions.add(getResources().getString(R.string.power0shortdes));
        powerShortDescriptions.add(getResources().getString(R.string.power1shortdes));
        powerShortDescriptions.add(getResources().getString(R.string.power2shortdes));
        powerShortDescriptions.add(getResources().getString(R.string.power3shortdes));
        txtShortDescription = findViewById(R.id.txtDescription);

        powerChosen = new ArrayList<Boolean>();
        for (int i = 0; i < 8; i++) powerChosen.add(false);
        lnloPower = findViewById(R.id.lnloGamePowers);
        usablePowers = new ArrayList<Power>();
        imgPowers = new ArrayList<SquareImageView>();
        imgPowers.add((SquareImageView)findViewById(R.id.imgPower1));
        imgPowers.add((SquareImageView)findViewById(R.id.imgPower2));
        imgPowers.add((SquareImageView)findViewById(R.id.imgPower3));
        imgPowers.add((SquareImageView)findViewById(R.id.imgPower4));
        txtPowerQuantities = new ArrayList<TextView>();
        txtPowerQuantities.add((TextView)findViewById(R.id.txtPowerQuantity1));
        txtPowerQuantities.add((TextView)findViewById(R.id.txtPowerQuantity2));
        txtPowerQuantities.add((TextView)findViewById(R.id.txtPowerQuantity3));
        txtPowerQuantities.add((TextView)findViewById(R.id.txtPowerQuantity4));
        rtloPowers = new ArrayList<RelativeLayout>();
        rtloPowers.add((RelativeLayout)findViewById(R.id.rtloPower1));
        rtloPowers.add((RelativeLayout)findViewById(R.id.rtloPower2));
        rtloPowers.add((RelativeLayout)findViewById(R.id.rtloPower3));
        rtloPowers.add((RelativeLayout)findViewById(R.id.rtloPower4));
        powerIDs = getIntent().getIntegerArrayListExtra("powerIDs");
        initiatePowers();

        initiateRoomsEvent();

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-1443920174093607/9732608536");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        CountDownTimer preGame = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                preChangeQuest();
            }
        }.start();
    }

    private void initiatePowers() {
        ArrayList<Power> allPower = ThisUser.getInstance(this).getPowersList();
        for (int i = powerIDs.size(); i < rtloPowers.size(); i++)
            rtloPowers.get(i).setVisibility(View.GONE);
        for (int i = 0; i < powerIDs.size(); i++)
        {
            usablePowers.add(allPower.get(powerIDs.get(i)));
            imgPowers.get(i).setImageBitmap(usablePowers.get(i).getIcon());
        }
        try {
            lnloPower.setVisibility(View.INVISIBLE);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void showPowers() {
        Collections.fill(powerChosen, false);
        for (int i = 0; i < powerIDs.size(); i++)
        {
            if (txtPowerQuantities.get(i).getText().toString().equals("0"))
            {
                rtloPowers.get(i).setClickable(false);
                rtloPowers.get(i).setAlpha(0.4f);
            }
            else
            {
                rtloPowers.get(i).setClickable(true);
                rtloPowers.get(i).setAlpha(1f);
            }
        }
        lnloPower.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getResources().getString(R.string.preventBackGame), Toast.LENGTH_SHORT).show();
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

    private boolean player1Ready = false;
    private boolean player2Ready = false;
    private boolean player1Answered = false;
    private boolean player2Answered = false;
    private void initiateRoomsEvent() {
        player1ReadyRef = database.getReference("rooms/" + mainCompeteRoom.getId() + "/controls/isPlayer1Ready");
        player1ReadyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !onRoomFinished)
                {
                    int get = Integer.parseInt(String.valueOf(snapshot.getValue()));
                    player1Ready = get == 1;
                    if (player1Ready && player2Ready)
                    {
                        if (btnNextQuest.getVisibility() == View.INVISIBLE)
                            showNextQuestion();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        player2ReadyRef = database.getReference("rooms/" + mainCompeteRoom.getId() + "/controls/isPlayer2Ready");
        player2ReadyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !onRoomFinished)
                {
                    int get = Integer.parseInt(String.valueOf(snapshot.getValue()));
                    player2Ready = get == 1;
                    if (player1Ready && player2Ready)
                    {
                        if (btnNextQuest.getVisibility() == View.INVISIBLE)
                            showNextQuestion();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        player1AnsweredRef = database.getReference("rooms/" + mainCompeteRoom.getId() + "/controls/isPlayer1Answered");
        player1AnsweredRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !onRoomFinished)
                {
                    int get = Integer.parseInt(String.valueOf(snapshot.getValue()));
                    player1Answered = get == 1;
                    if (player1Answered)
                    {
                        DatabaseReference getPoint = database.getReference("rooms/" + mainCompeteRoom.getId() + "/controls/player1Point");
                        getPoint.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int player1Point = Integer.parseInt(String.valueOf(snapshot.getValue()));
                                if (player1Point > Integer.parseInt(txtPlayer1Point.getText().toString()))
                                {
                                    txtPlayer1Name.setTextColor(Color.parseColor("#00FF00"));
                                    txtPlayer1Point.setTextColor(Color.parseColor("#00FF00"));
                                }
                                else
                                {
                                    txtPlayer1Name.setTextColor(Color.parseColor("#FF0000"));
                                    txtPlayer1Point.setTextColor(Color.parseColor("#FF0000"));
                                }
                                txtPlayer1Point.setText(String.valueOf(player1Point));
                                if (player2Answered && questionCounter == questionCountTotal)
                                {
                                    btnNextQuest.setVisibility(View.VISIBLE);
                                    com.example.quizzyapplication_v2.Online.GameActivity.onRoomFinished = true;
                                    com.example.quizzyapplication_v2.Online.PreGameActivity.onRoomDelete = true;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        player2AnsweredRef = database.getReference("rooms/" + mainCompeteRoom.getId() + "/controls/isPlayer2Answered");
        player2AnsweredRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !onRoomFinished)
                {
                    int get = Integer.parseInt(String.valueOf(snapshot.getValue()));
                    player2Answered = get == 1;
                    if (player2Answered) {
                        DatabaseReference getPoint = database.getReference("rooms/" + mainCompeteRoom.getId() + "/controls/player2Point");
                        getPoint.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int player2Point = Integer.parseInt(String.valueOf(snapshot.getValue()));
                                if (player2Point > Integer.parseInt(txtPlayer2Point.getText().toString()))
                                {
                                    txtPlayer2Name.setTextColor(Color.parseColor("#00FF00"));
                                    txtPlayer2Point.setTextColor(Color.parseColor("#00FF00"));
                                }
                                else
                                {
                                    txtPlayer2Name.setTextColor(Color.parseColor("#FF0000"));
                                    txtPlayer2Point.setTextColor(Color.parseColor("#FF0000"));
                                }
                                txtPlayer2Point.setText(String.valueOf(player2Point));
                                if (player1Answered && questionCounter == questionCountTotal)
                                {
                                    btnNextQuest.setVisibility(View.VISIBLE);
                                    com.example.quizzyapplication_v2.Online.GameActivity.onRoomFinished = true;
                                    com.example.quizzyapplication_v2.Online.PreGameActivity.onRoomDelete = true;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        player1Power = database.getReference("rooms/" + mainCompeteRoom.getId() + "/controls/player1Power");
        player1Power.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !onRoomFinished)
                {
                    getPower1 = Integer.parseInt(String.valueOf(snapshot.getValue()));
                    if (getPower1 != -1)
                    {
                        if (getPower1 >= 4)
                        {
                            if (!isPlayer1)
                            {
                                powerChosen.set(getPower1, true);
                                if (!isOpenPowerUseFragment)
                                {
                                    openPowerUseFragment(ThisUser.getInstance(getApplicationContext()).getPowersList().get(getPower1));
                                    CountDownTimer powerUseFragmentTimer = new CountDownTimer(2000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                        }

                                        @Override
                                        public void onFinish() {
                                            closePowerUseFragment();
                                        }
                                    }.start();
                                }
                                else
                                {
                                    CountDownTimer preWait = new CountDownTimer(1000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                        }

                                        @Override
                                        public void onFinish() {
                                            openPowerUseFragment(ThisUser.getInstance(getApplicationContext()).getPowersList().get(getPower1));
                                            CountDownTimer powerUseFragmentTimer = new CountDownTimer(2000, 1000) {
                                                @Override
                                                public void onTick(long millisUntilFinished) {

                                                }

                                                @Override
                                                public void onFinish() {
                                                    closePowerUseFragment();
                                                }
                                            }.start();
                                        }
                                    }.start();
                                }
                            }
                        }
                        else
                        {
                            if (isPlayer1)
                            {
                                powerChosen.set(getPower1, true);
                                txtShortDescription.setText(powerShortDescriptions.get(getPower1));
                                txtShortDescription.setVisibility(View.VISIBLE);
                                CountDownTimer countDown = new CountDownTimer(2000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        txtShortDescription.setVisibility(View.INVISIBLE);
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        player2Power = database.getReference("rooms/" + mainCompeteRoom.getId() + "/controls/player2Power");
        player2Power.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !onRoomFinished)
                {
                    getPower2 = Integer.parseInt(String.valueOf(snapshot.getValue()));
                    if (getPower2 != -1)
                    {
                        if (getPower2 >= 4)
                        {
                            if (isPlayer1)
                            {
                                powerChosen.set(getPower2, true);
                                if (!isOpenPowerUseFragment)
                                {
                                    openPowerUseFragment(ThisUser.getInstance(getApplicationContext()).getPowersList().get(getPower2));
                                    CountDownTimer powerUseFragmentTimer = new CountDownTimer(2000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                        }

                                        @Override
                                        public void onFinish() {
                                            closePowerUseFragment();
                                        }
                                    }.start();
                                }
                                else
                                {
                                    CountDownTimer preWait = new CountDownTimer(1000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                        }

                                        @Override
                                        public void onFinish() {
                                            openPowerUseFragment(ThisUser.getInstance(getApplicationContext()).getPowersList().get(getPower2));
                                            CountDownTimer powerUseFragmentTimer = new CountDownTimer(2000, 1000) {
                                                @Override
                                                public void onTick(long millisUntilFinished) {

                                                }

                                                @Override
                                                public void onFinish() {
                                                    closePowerUseFragment();
                                                }
                                            }.start();
                                        }
                                    }.start();
                                }
                            }
                        }
                        else
                        {
                            if (!isPlayer1)
                            {
                                powerChosen.set(getPower2, true);
                                txtShortDescription.setText(powerShortDescriptions.get(getPower2));
                                txtShortDescription.setVisibility(View.VISIBLE);
                                CountDownTimer countDown = new CountDownTimer(2000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        txtShortDescription.setVisibility(View.INVISIBLE);
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void preChangeQuest() {
        CountDownTimer timer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Database.getInstance().gamePreChangeQuest(isPlayer1, mainCompeteRoom.getId());
                txtQuestion.setText(getResources().getString(R.string.isWaiting) + (isPlayer1? " 2" : " 1"));
            }
        }.start();
    }

    private void DemNguoc(final com.example.quizzyapplication_v2.CustomDrawableProgressBar progressBar, int Max, int interval) {
        if (powerChosen.get(2))
            Max += 10; //Power loại 3
        long countDownTime = (long)Max * 1000;
        if (powerChosen.get(6) && !powerChosen.get(3)) //Bị tấn công power loại 5 và không kích hoạt power loại 4
        {
            countDownTime /= 2;
            Max /= 2;
        }
        progressBar.setMax(Max);
        progressBar.setProgress(progressBar.getMax());
        timer = new CountDownTimer(countDownTime, interval){
            public void onTick(long  millisUntilFinished) {
                progressBar.setProgress((float)millisUntilFinished/1000);
                if (powerChosen.get(6) && !powerChosen.get(3))
                {
                    if (millisUntilFinished * 2 > 13000)
                        currentQuestPoint = 20;
                    else if (millisUntilFinished * 2 > 12000)
                        currentQuestPoint = 19;
                    else if (millisUntilFinished * 2 > 11000)
                        currentQuestPoint = 18;
                    else if (millisUntilFinished * 2 > 10000)
                        currentQuestPoint = 17;
                    else if (millisUntilFinished * 2 > 8000)
                        currentQuestPoint = 16;
                    else if (millisUntilFinished * 2 > 6000)
                        currentQuestPoint = 15;
                    else if (millisUntilFinished * 2 > 4000)
                        currentQuestPoint = 14;
                    else if (millisUntilFinished * 2 > 2000)
                        currentQuestPoint = 13;
                    else if (millisUntilFinished * 2 > 1000)
                        currentQuestPoint = 12;
                    else if (millisUntilFinished > 0)
                        currentQuestPoint = 10;
                }
                else
                {
                    if (millisUntilFinished > 13000)
                        currentQuestPoint = 20;
                    else if (millisUntilFinished > 12000)
                        currentQuestPoint = 19;
                    else if (millisUntilFinished > 11000)
                        currentQuestPoint = 18;
                    else if (millisUntilFinished > 10000)
                        currentQuestPoint = 17;
                    else if (millisUntilFinished > 8000)
                        currentQuestPoint = 16;
                    else if (millisUntilFinished > 6000)
                        currentQuestPoint = 15;
                    else if (millisUntilFinished > 4000)
                        currentQuestPoint = 14;
                    else if (millisUntilFinished > 2000)
                        currentQuestPoint = 13;
                    else if (millisUntilFinished > 1000)
                        currentQuestPoint = 12;
                    else if (millisUntilFinished > 0)
                        currentQuestPoint = 10;
                }
                progressBar.invalidate();
            }

            public void onFinish() {
                progressBar.setProgress(0);
                if (!answered)
                    Database.getInstance().gameFinishQuest(isPlayer1, mainCompeteRoom.getId(), point);
                showSolution();
            }
        }.start();
    }

    private void showNextQuestion() {
        if (isPlayer1)
            database.getReference("rooms/" + mainCompeteRoom.getId() + "/controls/isPlayer1Ready").setValue(0);
        else
            database.getReference("rooms/" + mainCompeteRoom.getId() + "/controls/isPlayer2Ready").setValue(0);
        currentQuestPoint = 1;
        com.example.quizzyapplication_v2.CustomDrawableProgressBar progressBar = (com.example.quizzyapplication_v2.CustomDrawableProgressBar)findViewById(R.id.onlinegameprogressbar);
        progressBar.setProgress(progressBar.getMax());
        AnHinh(imgQuestionCard.findViewById(R.id.onlineQuestionImage));
        HideVideo(questionVideo);
        ((View)findViewById(R.id.hideVideo)).setVisibility(View.INVISIBLE);
        if (!questionList.get(questionCounter).getImage().equals("") && !questionList.get(questionCounter).getImage().contains("media_questions"))
        {
            Picasso.get().load(questionList.get(questionCounter).getImage()).error(R.drawable.ic_launcher_foreground)
                    .into(((ImageView)imgQuestionCard.findViewById(R.id.onlineQuestionImage)));
        }
        else if (questionList.get(questionCounter).getImage().contains("media_questions%2Fvideo"))
        {
            questionVideo.setVideoURI(Uri.parse(questionList.get(questionCounter).getImage()));
        }

        txtPlayer1Name.setTextColor(Color.parseColor("#F2E3BB"));
        txtPlayer2Name.setTextColor(Color.parseColor("#F2E3BB"));
        txtPlayer1Point.setTextColor(Color.parseColor("#F2E3BB"));
        txtPlayer2Point.setTextColor(Color.parseColor("#F2E3BB"));

        for (Button button : btnList)
        {
            button.setBackgroundResource(R.drawable.app_button_border);
            button.setText("");
            button.setClickable(true);
            button.setVisibility(View.INVISIBLE);
        }

        txtQuestion.setText(getResources().getString(R.string.question) + " " + String.valueOf(questionCounter + 1) + " / " + String.valueOf(questionCountTotal)
                + "\n" + getResources().getString(R.string.powerChoosingTime));
        showPowers();

        CountDownTimer powerTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                lnloPower.setVisibility(View.INVISIBLE);
                txtQuestion.setText(getResources().getString(R.string.question) + " " + String.valueOf(questionCounter + 1) + " / " + String.valueOf(questionCountTotal));

                CountDownTimer showQuest = new CountDownTimer(2200, 1000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        currentQuestion = questionList.get(questionCounter);
                        if (powerChosen.get(7) && !powerChosen.get(3))
                            txtQuestion.setText(getResources().getString(R.string.questionHidden));
                        else
                            txtQuestion.setText(currentQuestion.getQuestion());
                        Integer[] arr = new Integer[]{1, 2, 3, 4};
                        List<Integer> tmpList = Arrays.asList(arr);
                        Collections.shuffle(tmpList);
                        correctOption = tmpList.indexOf(currentQuestion.getAnswerNr()) + 1;
                        List<String> optionList = new ArrayList<String>();
                        optionList.add(currentQuestion.getOption1());
                        optionList.add(currentQuestion.getOption2());
                        optionList.add(currentQuestion.getOption3());
                        optionList.add(currentQuestion.getOption4());
                        //Show the options to corresponding button
                        for (int i = 0; i < 4; i++) {
                            btnList.get(i).setText(optionList.get(tmpList.get(i) - 1));
                        }
                        questWaitTime = 1000; //default
                        currentQuestInterval = 1000;
                        if (!currentQuestion.getImage().equals("")) {
                            if (currentQuestion.getImage().contains("media_questions%2Fvideo")) {
                                ShowVideo(questionVideo);
                                int length = mediaPlayer.getCurrentPosition();
                                mediaPlayer.pause();
                                mediaPlayer.seekTo(length);
                                questionVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        if (powerChosen.get(7) && !powerChosen.get(3))
                                            mp.setVolume(0f, 0f);
                                    }
                                });
                                questionVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer videoPlayer) {
                                        showOptionBoxes(correctOption);
                                        videoPlayer.setVolume(1f, 1f);
                                        questionVideo.setVisibility(View.VISIBLE);
                                        if (powerChosen.get(7) && !powerChosen.get(3))
                                        {
                                            txtQuestion.setText(getResources().getString(R.string.questionHidden));
                                        }

                                        if (!mediaPlayer.isPlaying())
                                            mediaPlayer.start();
                                        questionCounter++;
                                        answered = false;

                                        DemNguoc((com.example.quizzyapplication_v2.CustomDrawableProgressBar) findViewById(R.id.onlinegameprogressbar), 15, currentQuestInterval);
                                    }
                                });
                                questionVideo.start();
                                if (powerChosen.get(7) && !powerChosen.get(3))
                                {
                                    txtQuestion.setText(getResources().getString(R.string.noVideo));
                                    ((View)findViewById(R.id.hideVideo)).setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (!currentQuestion.getImage().contains("media_questions"))    //Question with image
                                {
                                    if (!(powerChosen.get(7) && !powerChosen.get(3)))
                                        HienHinh(imgQuestionCard.findViewById(R.id.onlineQuestionImage));
                                } else if (currentQuestion.getImage().contains("media_questions%2Fmusic")) //Storage at folder media_questions/music
                                {
                                    questMediaPlayer = new MediaPlayer();
                                    try {
                                        questMediaPlayer.setDataSource(currentQuestion.getImage());
                                        questMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                            @Override
                                            public void onPrepared(MediaPlayer mediaPlayer) {
                                                if (powerChosen.get(7) && !powerChosen.get(3))
                                                    txtQuestion.setText(getResources().getString(R.string.noAudio));
                                                else mediaPlayer.start();
                                            }
                                        });
                                        questMediaPlayer.prepare();
                                        questWaitTime = questMediaPlayer.getDuration() + 1000;
                                        int length = mediaPlayer.getCurrentPosition();
                                        mediaPlayer.pause();
                                        mediaPlayer.seekTo(length);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                CountDownTimer timer = new CountDownTimer(questWaitTime, 1000) {
                                    @Override
                                    public void onTick(long l) {
                                        //
                                    }

                                    @Override
                                    public void onFinish() {
                                        showOptionBoxes(correctOption);
                                        if (powerChosen.get(7) && !powerChosen.get(3))
                                            txtQuestion.setText(getResources().getString(R.string.questionHidden));

                                        if (!mediaPlayer.isPlaying())
                                            mediaPlayer.start();
                                        questionCounter++;
                                        answered = false;

                                        DemNguoc((com.example.quizzyapplication_v2.CustomDrawableProgressBar) findViewById(R.id.onlinegameprogressbar), 15, currentQuestInterval);
                                    }
                                }.start();
                            }

                        } else
                        {
                            CountDownTimer timer = new CountDownTimer(questWaitTime, 1000) {
                                @Override
                                public void onTick(long l) {
                                    //
                                }

                                @Override
                                public void onFinish() {
                                    //Show the options to corresponding button
                                    showOptionBoxes(correctOption);

                                    if (!mediaPlayer.isPlaying())
                                        mediaPlayer.start();
                                    questionCounter++;
                                    answered = false;

                                    DemNguoc((com.example.quizzyapplication_v2.CustomDrawableProgressBar)findViewById(R.id.onlinegameprogressbar), 15, currentQuestInterval);
                                }
                            }.start();
                        }
                    }
                }.start();
            }
        }.start();
    }

    private void showOptionBoxes(int correctOption)
    {
        for (Button btn : btnList)
            btn.setVisibility(View.VISIBLE);
        //Power loại 1 kết hợp power loại 7
        if (powerChosen.get(0) && (powerChosen.get(4) && !powerChosen.get(3)))
        {
            for (Button btn : btnList)
                btn.setVisibility(View.INVISIBLE);
        }
        //Chỉ có power loại 1
        else if (powerChosen.get(0))
        {
            ArrayList<Integer> tmpRandom = new ArrayList<Integer>();
            for (int i = 0; i < 4; i++)
                if (i != correctOption - 1)
                    tmpRandom.add(i);
            Collections.shuffle(tmpRandom);
            for (int i = 0; i < 2; i++)
                btnList.get(tmpRandom.get(i)).setVisibility(View.INVISIBLE);
        }
        //Case bị tấn công bởi power loại 6
        else if (powerChosen.get(4) && !powerChosen.get(3))
        {
            ArrayList<Integer> tmpRandom = new ArrayList<Integer>();
            for (int i = 0; i < 4; i++)
                tmpRandom.add(i);
            Collections.shuffle(tmpRandom);
            for (int i = 0; i < 2; i++)
                btnList.get(tmpRandom.get(i)).setVisibility(View.INVISIBLE);
        }
    }

    private void CheckAnswer(int i) {
        answered = true;
        timer.cancel();
        if (subMediaPlayer != null && subMediaPlayer.isPlaying())
            subMediaPlayer.stop();
        if (i == correctOption)
        {
            subMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.right);
            //Power loại 2
            correctQuest++;
            if (powerChosen.get(1))
                point += currentQuestPoint * 2;
            else
                point += currentQuestPoint;
        }
        else
        {
            //Power loại 2
            if (powerChosen.get(1))
                point -= 10;    //Điểm câu
            //Bị tấn công power loại 6 và không kích hoạt power loại 4
            if (powerChosen.get(5) && !powerChosen.get(3))
                point -= 10;    //Điểm câu
            subMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.wrong);
        }
        subMediaPlayer.start();
        Database.getInstance().gameFinishQuest(isPlayer1, mainCompeteRoom.getId(), point);
        showSolution();
    }

    private void showSolution() {
        for (int i = 1; i <= 4; i++)
        {
            btnList.get(i - 1).setVisibility(View.VISIBLE);
            btnList.get(i - 1).setClickable(false);
            if (i == correctOption)
                btnList.get(i - 1).setBackgroundResource(R.drawable.app_button_border_correct);
            else
                btnList.get(i - 1).setBackgroundResource(R.drawable.app_button_border_wrong);
        }
        if (questionCounter < questionCountTotal)
        {
            preChangeQuest();
        }
    }

    public void btnNextQuest_click(View view) {
        mediaPlayer.stop();
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                moveToPost();
            }
        });

        if (interstitialAd.isLoaded())
            interstitialAd.show();
        else
            moveToPost();
    }

    private void moveToPost()
    {
        androidx.core.util.Pair<View, String> p1 = androidx.core.util.Pair.create(findViewById(R.id.imgCardPlayer1), "player1Transition");
        androidx.core.util.Pair<View, String> p2 = androidx.core.util.Pair.create(findViewById(R.id.imgCardPlayer2), "player2Transition");
        ActivityOptionsCompat activityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(com.example.quizzyapplication_v2.Online.GameActivity.this, p1, p2);
        Intent postGame = new Intent(getApplicationContext(), com.example.quizzyapplication_v2.Online.PostGameActivity.class);
        postGame.putExtra("player1", (Parcelable)player1);
        postGame.putExtra("player2", (Parcelable)player2);
        postGame.putExtra("topicName", topicName);
        postGame.putExtra("player1Point", Integer.parseInt(txtPlayer1Point.getText().toString()));
        postGame.putExtra("player2Point", Integer.parseInt(txtPlayer2Point.getText().toString()));
        postGame.putExtra("isPlayer1", isPlayer1);
        postGame.putExtra("roomID", mainCompeteRoom.getId());
        postGame.putExtra("topicID", topicID);
        postGame.putExtra("correctQuest", correctQuest);
        postGame.putExtra("totalQuest", questionList.size());
        startActivity(postGame, activityOptionsCompat.toBundle());
        finish();
    }

    public void HienHinh(View view) {
        if (imgQuestionCard.getVisibility() == View.INVISIBLE) {
            imgQuestionCard.setVisibility(View.VISIBLE);

            TextView Question;
            Question = findViewById(R.id.txtQuestion);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) Question.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.addRule(RelativeLayout.BELOW,R.id.onlineQuestionImageCard);
            params.setMargins((int)(10*px2dp),(int)(20*px2dp),(int)(10*px2dp),0);
            Question.setLayoutParams(params);
        }
    }

    public void AnHinh(View view) {
        if (imgQuestionCard.getVisibility() == View.VISIBLE) {
            imgQuestionCard.setVisibility(View.INVISIBLE);

            TextView Question;
            Question = findViewById(R.id.txtQuestion);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) Question.getLayoutParams();
            params.height = (int)(200*px2dp);
            params.addRule(RelativeLayout.BELOW,R.id.onlinegameprogressbar);
            params.setMargins((int)(10*px2dp),(int)(50*px2dp),(int)(10*px2dp),0);
            Question.setLayoutParams(params);
        }
    }

    public void ShowVideo(View view)
    {
        if (view.getVisibility() == View.INVISIBLE) {
            view.setVisibility(View.VISIBLE);

            TextView Question;
            Question = findViewById(R.id.txtQuestion);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) Question.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.addRule(RelativeLayout.BELOW, view.getId());
            params.setMargins((int)(10*px2dp),(int)(20*px2dp),(int)(10*px2dp),0);
            Question.setLayoutParams(params);
        }
    }

    public void HideVideo(View view)
    {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.INVISIBLE);

            TextView Question;
            Question = findViewById(R.id.txtQuestion);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) Question.getLayoutParams();
            params.height = (int)(200*px2dp);
            params.addRule(RelativeLayout.BELOW, R.id.onlinegameprogressbar);
            params.setMargins((int)(10*px2dp),(int)(50*px2dp),(int)(10*px2dp),0);
            Question.setLayoutParams(params);
        }
    }

    public void Option1_Chosen(View view) {
        CheckAnswer(1);
    }

    public void Option2_Chosen(View view) {
        CheckAnswer(2);
    }

    public void Option3_Chosen(View view) {
        CheckAnswer(3);
    }

    public void Option4_Chosen(View view) {
        CheckAnswer(4);
    }

    public void usePower(View view) {
        int getPower = -1;
        switch (view.getId())
        {
            case R.id.rtloPower1: getPower = 0; break;
            case R.id.rtloPower2: getPower = 1; break;
            case R.id.rtloPower3: getPower = 2; break;
            case R.id.rtloPower4: getPower = 3; break;
        }
        int powerID = usablePowers.get(getPower).getId();
        txtPowerQuantities.get(getPower).setText("0");
        MediaPlayer mediaPlayer;
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.powerchosen);
        mediaPlayer.start();
        rtloPowers.get(getPower).setClickable(false);
        Database.getInstance().updateRoomUsePower(isPlayer1, mainCompeteRoom.getId(), powerID);
    }

    private void openPowerUseFragment(Power p) {
        PowerUseFragment powerUseFragment;
        if (isPlayer1)
            powerUseFragment = new PowerUseFragment(p, player2.getUsername(), player1.getUsername());
        else
            powerUseFragment = new PowerUseFragment(p, player1.getUsername(), player2.getUsername());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.add(R.id.powerUseFragment,
                powerUseFragment).addToBackStack(null).commit();
        isOpenPowerUseFragment = true;
    }

    public void closePowerUseFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PowerUseFragment powerUseFragment = (PowerUseFragment) fragmentManager
                .findFragmentById(R.id.powerUseFragment);
        if (powerUseFragment != null) {
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(powerUseFragment).commit();
        }
        isOpenPowerUseFragment = false;
    }
}