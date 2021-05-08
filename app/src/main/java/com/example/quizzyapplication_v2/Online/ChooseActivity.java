package com.example.quizzyapplication_v2.Online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.example.quizzyapplication_v2.MainActivity;
import com.example.quizzyapplication_v2.Offline.Topic;
import com.example.quizzyapplication_v2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.util.ArrayList;
import java.util.List;

public class ChooseActivity extends AppCompatActivity {

    private GridView gv_topics;
    private ArrayList<com.example.quizzyapplication_v2.Online.Topic> topics = new ArrayList<com.example.quizzyapplication_v2.Online.Topic>();
    private ChooseAdapter adapter;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_choose2);
        database = FirebaseDatabase.getInstance();
        topics = Database.getInstance().getTopicsList();
        initComponents();
        SpaceNavigationView bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_home_24));
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_add_circle_24));
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_storefront_24));
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_person_24));
        bottom_navigation.setCentreButtonSelectable(true);
        bottom_navigation.setCentreButtonSelected();
        bottom_navigation.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
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
                        Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(profileIntent);
                        finish();
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent feedIntent = new Intent(getApplicationContext(), NewsFeedActivity.class);
        startActivity(feedIntent);
        finish();
    }

    private int getCurrentPosition;
    private void initComponents() {
        adapter = new ChooseAdapter(this, R.layout.choose_grid_item_2, topics);

        gv_topics = this.findViewById(R.id.gv_online_topics);
        gv_topics.setAdapter(adapter);
        gv_topics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                com.example.quizzyapplication_v2.Online.Topic tp = topics.get(position);
                ActivityOptionsCompat activityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(com.example.quizzyapplication_v2.Online.ChooseActivity.this,
                                view, "imageTransition1");
                Intent roomIntent = new Intent(getApplicationContext(), TopicRoomActivity.class);
                roomIntent.putExtra("topicID", tp.getId());
                roomIntent.putExtra("topicName", tp.getName());
                roomIntent.putExtra("topicImage", tp.getImage());
                startActivity(roomIntent, activityOptionsCompat.toBundle());
            }
        });

        gv_topics.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                getCurrentPosition = position;
                new AlertDialog.Builder(com.example.quizzyapplication_v2.Online.ChooseActivity.this)
                        .setTitle("Confirmation")
                        .setMessage(getResources().getString(R.string.addToFavorite) + " " + topics.get(position) + "?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Database.getInstance().addFavoriteTopic(topics.get(getCurrentPosition));
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .show();
                return true;
            }
        });
    }
}