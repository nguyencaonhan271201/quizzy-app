package com.example.quizzyapplication_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.example.quizzyapplication_v2.Offline.ChooseActivity;
import com.example.quizzyapplication_v2.Online.Database;
import com.example.quizzyapplication_v2.Online.LoginActivity;
import com.example.quizzyapplication_v2.Online.NewsFeedActivity;
import com.example.quizzyapplication_v2.Online.Power;
import com.example.quizzyapplication_v2.Online.StartActivity;
import com.example.quizzyapplication_v2.Online.ThisUser;
import com.example.quizzyapplication_v2.Online.Topic;
import com.example.quizzyapplication_v2.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private float px2dp;
    private boolean doubleBackToExitPressedOnce;
    private ProgressDialog progressDialog;
    private InterstitialAd interstitialAd;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        RelativeLayout layout = findViewById(R.id.main_layout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connecting...");

        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(500);
        animationDrawable.setExitFadeDuration(500);
        animationDrawable.start();

        layout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeRight() {
                offlineMode();
            }
            public void onSwipeLeft() {
                onlineMode();
            }
        });

        mAdView = findViewById(R.id.adView);
        for (int i = 0; i < 100; i++)
        {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click ↩ again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
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

    public void offlineMode() {
        Intent startChoose = new Intent(this, com.example.quizzyapplication_v2.Offline.ChooseActivity.class);
        startActivity(startChoose);
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    public void onlineMode() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected())
        {
            progressDialog.show();
            Database.getInstance();
            CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    SharedPreferences loginInfo = getSharedPreferences("loginUsername", MODE_PRIVATE);
                    String username = loginInfo.getString("username", "");
                    String password = loginInfo.getString("password", "");
                    if (!username.equals("") && !password.equals(""))
                    {
                        //Saved information is correct
                        loggingIn(username, password);
                    }
                    else
                    {
                        Intent startChoose = new Intent(getApplicationContext(), com.example.quizzyapplication_v2.Online.StartActivity.class);
                        startActivity(startChoose);
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    }
                    progressDialog.hide();
                }
            }.start();
        }
        else
            Toast.makeText(this, "Please connect to Internet!", Toast.LENGTH_SHORT).show();
    }

    public void loggingIn(String username, String password)
    {
        DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference("users/" + username + "/isLoggedIn");
        myRef1.setValue(1);
        updateUserInfo(username);
        Intent intent = new Intent(getApplicationContext(), NewsFeedActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        finish();
    }

    private void updateUserInfo(String username) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users").child(username);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ThisUser instance = ThisUser.getInstance(getApplicationContext());
                instance.setUsername((String)snapshot.getKey());
                instance.setEmail((String)snapshot.child("email").getValue());
                instance.setProfileImage((String)snapshot.child("profileImage").getValue());
                instance.setLevel(Integer.parseInt(String.valueOf(snapshot.child("level").getValue())));
                instance.setExp(Integer.parseInt(String.valueOf(snapshot.child("exp").getValue())));
                instance.setCoin(Integer.parseInt(String.valueOf(snapshot.child("coin").getValue())));
                ArrayList<com.example.quizzyapplication_v2.Online.Topic> favoriteTopics = new ArrayList<Topic>();
                if (snapshot.hasChild("favTopics"))
                {
                    for (DataSnapshot shot : snapshot.child("favTopics").getChildren())
                    {
                        com.example.quizzyapplication_v2.Online.Topic tmp = new com.example.quizzyapplication_v2.Online.Topic();
                        int getTopicID = Integer.parseInt(String.valueOf(shot.getValue()));
                        ArrayList<com.example.quizzyapplication_v2.Online.Topic> totalTopics = Database.getInstance().getTopicsList();
                        for (int i = 0; i < totalTopics.size(); i++)
                        {
                            if (totalTopics.get(i).getId() == getTopicID)
                            {
                                favoriteTopics.add(totalTopics.get(i));
                                break;
                            }
                        }
                    }
                }
                instance.setFavoriteTopics(favoriteTopics);
                ArrayList<Power> userPowersList = instance.getPowersList();
                int[] powerIcons = {R.drawable.power0, R.drawable.power1, R.drawable.power2, R.drawable.power3,
                        R.drawable.power6, R.drawable.power5, R.drawable.power4, R.drawable.power7};
                int[] powerNames = {R.string.power0name, R.string.power1name, R.string.power2name, R.string.power3name,
                        R.string.power4name, R.string.power5name, R.string.power6name, R.string.power7name};
                int[] powerDescriptions = {R.string.power0des, R.string.power1des, R.string.power2des, R.string.power3des,
                        R.string.power4des, R.string.power5des, R.string.power6des, R.string.power7des};
                for (int i = 0; i < 8; i++)
                {
                    userPowersList.get(i).setQuantity(Integer.parseInt(String.valueOf(snapshot.child("powers").child("power" + String.valueOf(i)).getValue())));
                    userPowersList.get(i).setIcon(BitmapFactory.decodeResource(getResources(), powerIcons[i]));
                    userPowersList.get(i).setName(getResources().getString(powerNames[i]));
                    userPowersList.get(i).setDescription(getResources().getString(powerDescriptions[i]));
                }
                instance.setPowersList(userPowersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}