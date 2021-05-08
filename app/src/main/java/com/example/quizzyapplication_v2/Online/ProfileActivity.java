package com.example.quizzyapplication_v2.Online;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.quizzyapplication_v2.R;
import com.example.quizzyapplication_v2.SquareImageView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtNoTopics;
    private LinearLayout favTopicsView;
    private LinearLayout statisticTopics;
    private TextView txtUsername;
    private TextView txtLevel;
    private TextView txtExp;
    private TextView txtCoin;
    private SquareImageView imgProfile;
    private ArrayList<com.example.quizzyapplication_v2.Online.Topic> favTopics;
    private ThisUser instance;
    private BarChart profileBarChart;
    private RadarChart profileRadarChart;
    private ProgressDialog progressDialog;
    private int getPosition2;
    private int getPosition = -1;
    private boolean toStatistic = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);

        instance = ThisUser.getInstance(this);
        txtNoTopics = findViewById(R.id.txtNoTopics);
        favTopicsView = findViewById(R.id.favoriteTopics);
        favTopics = instance.getFavoriteTopics();
        txtUsername = findViewById(R.id.txtProfileUsername);
        txtLevel = findViewById(R.id.txtProfileLevel);
        txtExp = findViewById(R.id.txtProfileExp);
        txtCoin = findViewById(R.id.txtProfileCoin);
        imgProfile = findViewById(R.id.imgProfile);
        statisticTopics = findViewById(R.id.statisticTopics);
        profileBarChart = findViewById(R.id.profileBarChart);
        profileRadarChart = findViewById(R.id.profileRadarChart);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        loadInformation();
        updateStatisticTopics();

        SpaceNavigationView bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_home_24));
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_add_circle_24));
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_storefront_24));
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_person_24));
        bottom_navigation.changeCurrentItem(3);
        bottom_navigation.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent gameIntent = new Intent(getApplicationContext(), com.example.quizzyapplication_v2.Online.ChooseActivity.class);
                startActivity(gameIntent);
                finish();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex)
                {
                    case 0:
                        Intent feedIntent = new Intent(getApplicationContext(), NewsFeedActivity.class);
                        startActivity(feedIntent);
                        finish();
                        break;
                    case 1:
                        Intent createFeed = new Intent(getApplicationContext(), CreateFeedActivity.class);
                        startActivity(createFeed);
                        finish();
                        break;
                    case 2:
                        Intent storeIntent = new Intent(getApplicationContext(), PowerStoreActivity.class);
                        startActivity(storeIntent);
                        finish();
                        break;
                    case 3:
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });
    }

    private ArrayList<com.example.quizzyapplication_v2.Online.Topic> allTopics;

    private void updateStatisticTopics() {
        allTopics = Database.getInstance().getTopicsList();
        statisticTopics.removeAllViews();
        for (int i = 0; i < allTopics.size(); i++) {
            RelativeLayout tmp = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.fav_topic_item, null, false);
            ImageView imgTopic = tmp.findViewById(R.id.topicImage);
            Picasso.get().load(allTopics.get(i).getImage()).error(R.drawable.ic_launcher_foreground).fit().into(imgTopic);
            tmp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < statisticTopics.getChildCount(); j++)
                    {
                        if (v == statisticTopics.getChildAt(j))
                        {
                            getPosition2 = j;
                            displayFragment();
                            break;
                        }
                    }

                }
            });
            statisticTopics.addView(tmp);
        }
        RelativeLayout tmp2 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.fav_topic_item2, null, false);
        tmp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPosition2 = allTopics.size();
                displayFragment();
            }
        });
        LinearLayout lnloGeneral = findViewById(R.id.lnloGeneral);
        lnloGeneral.removeAllViews();
        lnloGeneral.addView(tmp2);
    }

    private void loadInformation() {
        Picasso.get().load(instance.getProfileImage()).error(R.drawable.default_ava)
                .into(imgProfile);
        txtUsername.setText(instance.getUsername());
        txtLevel.setText(getResources().getString(R.string.level) + ": " + String.valueOf(instance.getLevel()));
        txtExp.setText("Exp: " + String.valueOf(instance.getExp()));
        txtCoin.setText(String.valueOf(instance.getCoin()));

        updateFavoriteTopics();
    }


    private void updateFavoriteTopics() {
        if (favTopics.size() == 0)
        {
            favTopicsView.setVisibility(View.GONE);
            txtNoTopics.setVisibility(View.VISIBLE);
            ((HorizontalScrollView)findViewById(R.id.scvFavTopic)).setVisibility(View.GONE);
            ((RelativeLayout)findViewById(R.id.rtloFavTop)).invalidate();
        }
        else {
            ((HorizontalScrollView)findViewById(R.id.scvFavTopic)).setVisibility(View.VISIBLE);
            favTopicsView.setVisibility(View.VISIBLE);
            txtNoTopics.setVisibility(View.INVISIBLE);
            ((RelativeLayout)findViewById(R.id.rtloFavTop)).invalidate();
            favTopicsView.removeAllViews();
            for (int i = 0; i < favTopics.size(); i++) {
                RelativeLayout tmp = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.fav_topic_item, null, false);
                ImageView imgTopic = tmp.findViewById(R.id.topicImage);
                Picasso.get().load(favTopics.get(i).getImage()).error(R.drawable.ic_launcher_foreground).fit().into(imgTopic);
                tmp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int j = 0; j < favTopicsView.getChildCount(); j++)
                        {
                            if (v == favTopicsView.getChildAt(j))
                            {
                                getPosition = j;
                                break;
                            }
                        }
                        ActivityOptionsCompat activityOptionsCompat =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileActivity.this,
                                        v.findViewById(R.id.topicImage), "imageTransition1");
                        Intent roomIntent = new Intent(getApplicationContext(), TopicRoomActivity.class);
                        roomIntent.putExtra("topicID", favTopics.get(getPosition).getId());
                        roomIntent.putExtra("topicName", favTopics.get(getPosition).getName());
                        roomIntent.putExtra("topicImage", favTopics.get(getPosition).getImage());
                        startActivity(roomIntent, activityOptionsCompat.toBundle());
                    }
                });
                tmp.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        for (int j = 0; j < favTopicsView.getChildCount(); j++)
                        {
                            if (v == favTopicsView.getChildAt(j))
                            {
                                getPosition = j;
                                break;
                            }
                        }
                        new AlertDialog.Builder(ProfileActivity.this)
                                .setTitle("Delete confirmation")
                                .setMessage(getResources().getString(R.string.deleteFromFavorite) + " " + favTopics.get(getPosition).getName() + "?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        deleteFromFavorite();
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.no), null)
                                .show();
                        return true;
                    }
                });
                favTopicsView.addView(tmp);
            }
        }
    }

    private void deleteFromFavorite()
    {
        Database.getInstance().deleteFavoriteTopic(favTopics.get(getPosition));
        favTopics = ThisUser.getInstance(null).getFavoriteTopics();
        loadInformation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String currentTime = String.valueOf(System.currentTimeMillis());
            final StorageReference storageRef =  FirebaseStorage.getInstance().getReference().child("/profiles_images/" + currentTime);
            progressDialog.show();
            storageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String profileURL = uri.toString();
                            instance.updateProfileImage(profileURL);
                            loadInformation();
                            progressDialog.hide();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (toStatistic)
            closeFragment();
        else
        {
            Intent feedIntent = new Intent(getApplicationContext(), NewsFeedActivity.class);
            startActivity(feedIntent);
            finish();
        }
    }

    public void SignOut(View view) {
        Database.getInstance().signOut();
        SharedPreferences loginInfo = getSharedPreferences("loginUsername", MODE_PRIVATE);
        loginInfo.edit().putString("username", "").apply();
        loginInfo.edit().putString("password", "").apply();
        Intent startActivity = new Intent(getApplicationContext(), StartActivity.class);
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        startActivity(startActivity);
        finish();
    }

    public void changeProfileImage(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Profile Image"), 1);
    }

    public void displayFragment()
    {
        StatisticFragment selectionFragment = new StatisticFragment(getPosition2);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.add(R.id.statistic_fragment,
                selectionFragment).addToBackStack(null).commit();
        toStatistic = true;
    }

    public void closeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        StatisticFragment simpleFragment = (StatisticFragment) fragmentManager
                .findFragmentById(R.id.statistic_fragment);
        if (simpleFragment != null) {
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(simpleFragment).commit();
        }
        toStatistic = false;
    }
}