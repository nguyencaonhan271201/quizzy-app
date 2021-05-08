package com.example.quizzyapplication_v2.Online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.quizzyapplication_v2.BuildConfig;
import com.example.quizzyapplication_v2.R;
import com.example.quizzyapplication_v2.SquareImageView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Level;

public class PostGameActivity extends AppCompatActivity {
    private User player1;
    private User player2;
    private TextView txtPostResult;
    private SquareImageView imgPlayer1;
    private SquareImageView imgPlayer2;
    private TextView player1Name;
    private TextView player2Name;
    private TextView player1Point;
    private TextView player2Point;
    private TextView myBonus;
    private TextView txtTopic;
    private boolean isPlayer1;
    private int point1;
    private int point2;
    private int bonusVal;
    private FirebaseDatabase database;
    private int roomID;
    private View screenShotView;
    private ProgressDialog progressDialog;
    private String topicName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_post_game2);

        txtPostResult = findViewById(R.id.txtPostResult);
        imgPlayer1 = findViewById(R.id.imgPostPlayer1);
        imgPlayer2 = findViewById(R.id.imgPostPlayer2);
        player1Name = findViewById(R.id.txtPostPlayer1);
        player2Name = findViewById(R.id.txtPostPlayer2);
        player1Point = findViewById(R.id.txtPostPlayer1Point);
        player2Point = findViewById(R.id.txtPostPlayer2Point);
        myBonus = findViewById(R.id.txtPointPost);
        txtTopic = findViewById(R.id.txtPostTopic);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        player1 = getIntent().getParcelableExtra("player1");
        player2 = getIntent().getParcelableExtra("player2");
        isPlayer1 = getIntent().getBooleanExtra("isPlayer1", true);
        Picasso.get().load(player1.getProfileImage()).error(R.drawable.default_ava)
                .into(imgPlayer1);
        Picasso.get().load(player2.getProfileImage()).error(R.drawable.default_ava)
                .into(imgPlayer2);
        player1Name.setText(player1.getUsername());
        player2Name.setText(player2.getUsername());
        point1 = getIntent().getIntExtra("player1Point", 0);
        point2 = getIntent().getIntExtra("player2Point", 0);
        player1Point.setText(String.valueOf(point1));
        player2Point.setText(String.valueOf(point2));
        topicName = getIntent().getStringExtra("topicName");
        txtTopic.setText(getResources().getString(R.string.topic) + ": " + topicName);
        roomID = getIntent().getIntExtra("roomID", 0);
        int topicID = getIntent().getIntExtra("topicID", 0);
        int correctQuest = getIntent().getIntExtra("correctQuest", 0);
        int totalQuest = getIntent().getIntExtra("totalQuest", 0);
        int status = -1;
        if (isPlayer1)
            status = point1 > point2? 0 : point1 == point2 ? 1 : 2;
        else
            status = point2 > point1? 0 : point2 == point1 ? 1 : 2;
        Database.getInstance().addStatistic(topicID, correctQuest, totalQuest, status, isPlayer1? point1 : point2);

        database = FirebaseDatabase.getInstance();
        screenShotView = getWindow().getDecorView();

        if (isPlayer1)
        {
            if (point1 > point2)
            {
                txtPostResult.setText("YOU WIN");
                bonusVal = point1 + 100;
                player1Name.setTextColor(Color.parseColor("#657346"));
                player1Point.setTextColor(Color.parseColor("#657346"));
                player2Name.setTextColor(Color.parseColor("#B85738"));
                player2Point.setTextColor(Color.parseColor("#B85738"));
            }
            else if (point1 == point2)
            {
                txtPostResult.setText("TIE");
                bonusVal = point1 + 50;
                player1Name.setTextColor(Color.parseColor("#657346"));
                player1Point.setTextColor(Color.parseColor("#657346"));
                player2Name.setTextColor(Color.parseColor("#657346"));
                player2Point.setTextColor(Color.parseColor("#657346"));
            }
            else
            {
                txtPostResult.setText("YOU LOSE");
                bonusVal = point1;
                player1Name.setTextColor(Color.parseColor("#B85738"));
                player1Point.setTextColor(Color.parseColor("#B85738"));
                player2Name.setTextColor(Color.parseColor("#657346"));
                player2Point.setTextColor(Color.parseColor("#657346"));
            }
            ThisUser me = ThisUser.getInstance(getApplicationContext());
            me.setExp(me.getExp() + bonusVal);
            if (me.getExp() > 1000)
            {
                me.setLevel(me.getLevel() + 1);
                me.setExp(me.getExp() - 1000);
                openLevelUpFragment(me.getLevel());
                CountDownTimer timer = new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        closeLevelUpFragment();
                    }
                }.start();
            }
            if (point1 > 0)
                me.setCoin(me.getCoin() + (int)Math.ceil(bonusVal / 10));
            closeRoom();
        }
        else
        {
            if (point2 > point1)
            {
                txtPostResult.setText("YOU WIN");
                bonusVal = point2 + 100;
                player1Name.setTextColor(Color.parseColor("#B85738"));
                player1Point.setTextColor(Color.parseColor("#B85738"));
                player2Name.setTextColor(Color.parseColor("#657346"));
                player2Point.setTextColor(Color.parseColor("#657346"));
            }
            else if (point2 == point1)
            {
                txtPostResult.setText("TIE");
                bonusVal = point2 + 50;
                player1Name.setTextColor(Color.parseColor("#657346"));
                player1Point.setTextColor(Color.parseColor("#657346"));
                player2Name.setTextColor(Color.parseColor("#657346"));
                player2Point.setTextColor(Color.parseColor("#657346"));
            }
            else
            {
                txtPostResult.setText("YOU LOSE");
                bonusVal = point2;
                player1Name.setTextColor(Color.parseColor("#657346"));
                player1Point.setTextColor(Color.parseColor("#657346"));
                player2Name.setTextColor(Color.parseColor("#B85738"));
                player2Point.setTextColor(Color.parseColor("#B85738"));
            }
            ThisUser me = ThisUser.getInstance(getApplicationContext());
            me.setExp(me.getExp() + bonusVal);
            if (me.getExp() > 1000)
            {
                me.setLevel(me.getLevel() + 1);
                me.setExp(me.getExp() - 1000);
                openLevelUpFragment(me.getLevel());
                CountDownTimer timer = new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        closeLevelUpFragment();
                    }
                }.start();
            }
            if (point2 > 0)
                me.setCoin(me.getCoin() + (int)Math.ceil(bonusVal / 10));
        }
        Database.getInstance().updatePostGameResult();
        myBonus.setText(String.valueOf(bonusVal));
        ((TextView)findViewById(R.id.txtPostCoin)).setText(String.valueOf((int)Math.ceil(bonusVal / 10)));
    }

    private void closeRoom() {
        DatabaseReference roomRef = database.getReference("rooms");
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int getCurrentRoomCount = Integer.parseInt(String.valueOf(snapshot.child("rooms_count").getValue()));
                getCurrentRoomCount--;
                database.getReference("rooms/rooms_count").setValue(String.valueOf(getCurrentRoomCount));
                database.getReference("rooms/" + String.valueOf(roomID)).removeValue();
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

    public void BackToStart(View view) {
        Intent intent = new Intent(getApplicationContext(), com.example.quizzyapplication_v2.Online.ChooseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void Share(View view) {
        takeScreenshotAndShare(this, screenShotView);
    }

    public static void takeScreenshotAndShare(final Context context, View view)
    {
        try
        {
            File mPath = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "screenshot.png");

            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            FileOutputStream outStream = new FileOutputStream(mPath);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outStream);
            outStream.flush();
            outStream.close();

            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            //Uri pictureUri = Uri.fromFile(mPath);
            Uri pictureUri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    mPath);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(shareIntent, "Share kết quả"));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void ShareToFeed(View view) {
        takeScreenshotAndShareToQuizzy(this, screenShotView);
    }

    public void takeScreenshotAndShareToQuizzy(final Context context, View view)
    {
        try
        {
            progressDialog.show();
            File mPath = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "screenshot.png");

            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            FileOutputStream outStream = new FileOutputStream(mPath);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outStream);
            outStream.flush();
            outStream.close();

            Uri pictureUri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    mPath);
            String currentTime = String.valueOf(System.currentTimeMillis());
            final StorageReference storageRef =  FirebaseStorage.getInstance().getReference().child("/feeds/" + currentTime);
            storageRef.putFile(pictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String feedImageURL = uri.toString();
                            String feedContent = "";
                            if (point1 == point2)
                            {
                                feedContent = player1.getUsername() + " " +
                                        getResources().getString(R.string.tie) + " " +
                                        player2.getUsername() + " ";
                            }
                            else if (point1 > point2)
                            {
                                feedContent = player1.getUsername() + " " +
                                        getResources().getString(R.string.win) + " " +
                                        player2.getUsername() + " ";
                            }
                            else
                            {
                                feedContent = player2.getUsername() + " " +
                                        getResources().getString(R.string.win) + " " +
                                        player1.getUsername() + " ";
                            }
                            feedContent += getResources().getString(R.string.matchInTopic) + " " + topicName;
                            Database.getInstance().updatePost(topicName, feedImageURL, feedContent);
                            progressDialog.hide();
                            Intent feedIntent = new Intent(getApplicationContext(), NewsFeedActivity.class);
                            feedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            feedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(feedIntent);
                            finish();
                        }
                    });
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void openLevelUpFragment(int newLevel) {
        LevelUpFragment levelUpFragment = new LevelUpFragment(newLevel);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.add(R.id.levelUpFragment,
                levelUpFragment).addToBackStack(null).commit();
    }

    public void closeLevelUpFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        LevelUpFragment levelUpFragment = (LevelUpFragment) fragmentManager
                .findFragmentById(R.id.levelUpFragment);
        if (levelUpFragment != null) {
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(levelUpFragment).commit();
        }
    }
}