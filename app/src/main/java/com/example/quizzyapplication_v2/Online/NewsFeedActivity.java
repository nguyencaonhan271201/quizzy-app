package com.example.quizzyapplication_v2.Online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.quizzyapplication_v2.MainActivity;
import com.example.quizzyapplication_v2.R;
import com.example.quizzyapplication_v2.SquareImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.squareup.picasso.Picasso;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class NewsFeedActivity extends AppCompatActivity {
    private ArrayList<Topic> allTopics;
    private LinearLayout postTopics;
    private LinearLayout lnloFeeds;
    public ArrayList<Feed> feedsList;
    private ScrollView feedScrollView;
    private int currentChoice; //for feed's click event
    private int currentChoice2; //for like button's click event
    private int currentChoice3; //for comment button's click event
    private long totalFeedCount = -1; //for Identifying Feed Change
    public static boolean movedOut = true;
    public static boolean toComment = false;
    public boolean toEdit = false;
    private boolean isLiked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_news_feed);

        postTopics = findViewById(R.id.postTopics);
        updateFeedTopics();
        lnloFeeds = findViewById(R.id.lnloFeeds);
        currentChoice = 11;
        feedScrollView = findViewById(R.id.feedScrollView);

        movedOut = false;

        SpaceNavigationView bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_home_24));
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_add_circle_24));
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_storefront_24));
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_person_24));
        bottom_navigation.changeCurrentItem(0);
        bottom_navigation.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent gameIntent = new Intent(getApplicationContext(), com.example.quizzyapplication_v2.Online.ChooseActivity.class);
                startActivity(gameIntent);
                finish();
                movedOut = true;
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                movedOut = itemIndex != 0;
                switch (itemIndex)
                {
                    case 0:
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
                if (itemIndex == 0)
                    loadFeeds(currentChoice);
            }
        });

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (postTopics.getChildCount() == 0)
                    updateFeedTopics();
                loadFeeds(currentChoice);
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (toComment)
            closeCommentFragment();
        else if (toEdit)
            closeEditFeedFragment();
        else
        {
            if (feedScrollView.getScrollY() == 0)
            {
                Intent startActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startActivity);
                startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
            else
            {
                //feedScrollView.fullScroll(ScrollView.FOCUS_UP);
                ValueAnimator realSmoothScrollAnimation =
                        ValueAnimator.ofInt(feedScrollView.getScrollY(), 0);
                realSmoothScrollAnimation.setDuration(500);
                realSmoothScrollAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        int scrollTo = (Integer) animation.getAnimatedValue();
                        feedScrollView.scrollTo(0, scrollTo);
                    }
                });

                realSmoothScrollAnimation.start();
            }
        }
    }

    private void updateFeedTopics() {
        allTopics = Database.getInstance().getTopicsList();
        postTopics.removeAllViews();
        for (int i = 0; i < allTopics.size(); i++) {
            RelativeLayout tmp = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.fav_topic_item, null, false);
            ImageView imgTopic = tmp.findViewById(R.id.topicImage);
            Picasso.get().load(allTopics.get(i).getImage()).error(R.drawable.ic_launcher_foreground).fit().into(imgTopic);
            tmp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < postTopics.getChildCount(); j++)
                    {
                        if (v == postTopics.getChildAt(j))
                        {
                            loadFeeds(j + 1);
                            currentChoice = j + 1;
                            break;
                        }
                    }

                }
            });
            postTopics.addView(tmp);
        }
        loadFeeds(11);
    }

    private void loadFeeds(int topicID)
    {
        if (feedsList != null)
            feedsList.clear();
        else feedsList = new ArrayList<Feed>();
        DatabaseReference feedsRef = FirebaseDatabase.getInstance().getReference("feeds");
        if (topicID == 11)
        {
            feedsRef.limitToLast(50).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        int topicID = Integer.parseInt((String)snapshot1.child("topicID").getValue());
                        String posterAva = (String)snapshot1.child("avatarPoster").getValue();
                        String feedContent = (String)snapshot1.child("feedContent").getValue();
                        String feedImage = (String)snapshot1.child("feedImage").getValue();
                        int like = 0;
                        if (snapshot1.child("like").exists())
                            like = Integer.parseInt(String.valueOf(snapshot1.child("like").getChildrenCount()));
                        String topicName = (String)snapshot1.child("topicName").getValue();
                        String usernamePoster = (String)snapshot1.child("usernamePoster").getValue();
                        long time = Long.parseLong((String)snapshot1.getKey());
                        ArrayList<Comment> comments = new ArrayList<Comment>();
                        if (snapshot1.child("comments").exists())
                        {
                            for (DataSnapshot snapshot3 : snapshot1.child("comments").getChildren())
                            {
                                long commentID = Long.parseLong((String)snapshot3.getKey());
                                String commentContent = (String)snapshot3.child("content").getValue();
                                String username = (String)snapshot3.child("username").getValue();
                                String profileImage = (String)snapshot3.child("profile").getValue();
                                int likeCount = 0;
                                if (snapshot3.child("like").exists())
                                    likeCount = Integer.parseInt(String.valueOf(snapshot3.child("like").getChildrenCount()));
                                Comment comment = new Comment(time, commentID, username, profileImage, commentContent, false, likeCount);
                                boolean isCommentLiked = false;
                                if (snapshot3.child("like").exists())
                                {
                                    for (DataSnapshot snapshot4 : snapshot3.child("like").getChildren())
                                    {
                                        if (snapshot4.getValue().equals(ThisUser.getInstance(null).getUsername()))
                                        {
                                            isCommentLiked = true;
                                            break;
                                        }
                                    }
                                }
                                comment.setLiked(isCommentLiked);
                                comments.add(comment);
                            }
                        }
                        Feed tmp = new Feed(topicID, topicName, feedContent, usernamePoster, posterAva, feedImage, like, comments, time);
                        String myUsername = ThisUser.getInstance(null).getUsername();
                        boolean isLiked = false;
                        if (snapshot1.child("like").exists())
                        {
                            for (DataSnapshot snapshot2 : snapshot1.child("like").getChildren())
                            {
                                if (snapshot2.getValue().equals(myUsername))
                                {
                                    isLiked = true;
                                    break;
                                }
                            }
                        }
                        tmp.setLiked(isLiked);
                        feedsList.add(tmp);
                    }
                    Collections.reverse(feedsList);
                    setAdapter();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            feedsRef.orderByChild("topicID").equalTo(String.valueOf(topicID)).limitToLast(50).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        int topicID = Integer.parseInt((String)snapshot1.child("topicID").getValue());
                        String posterAva = (String)snapshot1.child("avatarPoster").getValue();
                        String feedContent = (String)snapshot1.child("feedContent").getValue();
                        String feedImage = (String)snapshot1.child("feedImage").getValue();
                        int like = Integer.parseInt(String.valueOf(snapshot1.child("like").getChildrenCount()));
                        String topicName = (String)snapshot1.child("topicName").getValue();
                        String usernamePoster = (String)snapshot1.child("usernamePoster").getValue();
                        long time = Long.parseLong((String)snapshot1.getKey());
                        ArrayList<Comment> comments = new ArrayList<Comment>();
                        if (snapshot1.child("comments").exists())
                        {
                            for (DataSnapshot snapshot3 : snapshot1.child("comments").getChildren())
                            {
                                long commentID = Long.parseLong((String)snapshot3.getKey());
                                String commentContent = (String)snapshot3.child("content").getValue();
                                String username = (String)snapshot3.child("username").getValue();
                                String profileImage = (String)snapshot3.child("profile").getValue();
                                int likeCount = 0;
                                if (snapshot3.child("like").exists())
                                    likeCount = Integer.parseInt(String.valueOf(snapshot3.child("like").getChildrenCount()));
                                Comment comment = new Comment(time, commentID, username, profileImage, commentContent, false, likeCount);
                                boolean isCommentLiked = false;
                                if (snapshot3.child("like").exists())
                                {
                                    for (DataSnapshot snapshot4 : snapshot3.child("like").getChildren())
                                    {
                                        if (snapshot4.getValue().equals(ThisUser.getInstance(null).getUsername()))
                                        {
                                            isCommentLiked = true;
                                            break;
                                        }
                                    }
                                }
                                comment.setLiked(isCommentLiked);
                                comments.add(comment);
                            }
                        }
                        Feed tmp = new Feed(topicID, topicName, feedContent, usernamePoster, posterAva, feedImage, like, comments, time);
                        String myUsername = ThisUser.getInstance(null).getUsername();
                        boolean isLiked = false;
                        for (DataSnapshot snapshot2 : snapshot1.child("like").getChildren())
                        {
                            if (snapshot2.getValue().equals(myUsername))
                            {
                                isLiked = true;
                                break;
                            }
                        }
                        tmp.setLiked(isLiked);
                        feedsList.add(tmp);
                    }
                    Collections.reverse(feedsList);
                    setAdapter();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    private void setAdapter()
    {
        lnloFeeds.removeAllViews();
        for (int i = 0; i < feedsList.size(); i++)
        {
            LinearLayout tmp = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.feed_layout, null, false);
            SquareImageView imgPoster = tmp.findViewById(R.id.imgPoster);
            TextView txtPostUsername = tmp.findViewById(R.id.txtPostUsername);
            TextView txtPostTopic = tmp.findViewById(R.id.txtPostTopic);
            TextView txtPostContent = tmp.findViewById(R.id.txtPostContent);
            ImageView imgPost = tmp.findViewById(R.id.imgPost);
            TextView txtLikeCount = tmp.findViewById(R.id.txtLikeCount);
            TextView txtCommentCount = tmp.findViewById(R.id.txtCommentCount);
            TextView txtFeedTime = tmp.findViewById(R.id.txtFeedTime);
            Feed f = feedsList.get(i);

            Picasso.get().load(f.getAvatarPoster()).error(R.drawable.default_ava).into(imgPoster);
            if (!TextUtils.isEmpty(f.getFeedImage()))
            {
                imgPost.setVisibility(View.VISIBLE);
                Picasso.get().load(f.getFeedImage()).error(R.drawable.ic_launcher_foreground).into(imgPost);
            }
            else imgPost.setVisibility(View.GONE);
            txtPostUsername.setText(f.getUsernamePoster());
            txtPostTopic.setText(f.getTopicName());
            txtPostContent.setText(f.getFeedContent());
            txtLikeCount.setText(String.valueOf(f.getLikeCount()) + " " + getResources().getString(R.string.likeCount));
            ArrayList<Comment> tmpArray = f.getComments();
            txtCommentCount.setText(String.valueOf(tmpArray.size()) + " " + getResources().getString(R.string.comment));
            long timeDifference = System.currentTimeMillis() - f.getTimestamp();
            long aWeek = 86400000 * 7;
            if (timeDifference < 60000)
            {
                int getSeconds = (int)timeDifference/1000;
                txtFeedTime.setText(String.valueOf(getSeconds) + " " + getResources().getString(R.string.seconds));
            }
            else if (timeDifference < 3600000)
            {
                int getMinutes = (int)timeDifference / 60000;
                txtFeedTime.setText(String.valueOf(getMinutes) + " " + getResources().getString(R.string.minute));
            }
            else if (timeDifference < 86400000)
            {
                int getHours = (int)timeDifference / 3600000;
                txtFeedTime.setText(String.valueOf(getHours) + " " + getResources().getString(R.string.hour));
            }
            else if (timeDifference < aWeek)
            {
                long getDay = timeDifference / 86400000;
                txtFeedTime.setText(String.valueOf(getDay) + " " + getResources().getString(R.string.days));
            }
            else
            {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String dateString = formatter.format(new Date(f.getTimestamp()));
                txtFeedTime.setText(dateString);
            }
            SquareImageView btnLike = tmp.findViewById(R.id.btnLike);
            SquareImageView btnComment = tmp.findViewById(R.id.btnComment);
            isLiked = f.isLiked();
            if (isLiked)
                btnLike.setBackgroundResource(R.drawable.liked);
            else
                btnLike.setBackgroundResource(R.drawable.not_liked);

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout feedChildGet = null;
                    for (int j = 0; j < lnloFeeds.getChildCount(); j++)
                    {
                        LinearLayout feedChild =(LinearLayout)lnloFeeds.getChildAt(j);
                        LinearLayout tmpLnlo = (LinearLayout)feedChild.getChildAt(0);
                        LinearLayout getSmallerChild = (LinearLayout)tmpLnlo.getChildAt(3);
                        for (int k = 0; k < getSmallerChild.getChildCount(); k++)
                        {
                            if (getSmallerChild.getChildAt(k) == v)
                            {
                                currentChoice2 = j;
                                feedChildGet = feedChild;
                                break;
                            }
                        }
                    }
                    isLiked = feedsList.get(currentChoice2).isLiked();
                    if (isLiked)
                    {
                        ((SquareImageView)feedChildGet.findViewById(R.id.btnLike)).setBackgroundResource(R.drawable.not_liked);
                        ((TextView)feedChildGet.findViewById(R.id.txtLikeCount)).setText(String.valueOf(feedsList.get(currentChoice2).getLikeCount() - 1) + " " + getResources().getString(R.string.likeCount));
                        Database.getInstance().unlikePost(feedsList.get(currentChoice2));
                        feedsList.get(currentChoice2).setLikeCount(feedsList.get(currentChoice2).getLikeCount() - 1);
                    }
                    else
                    {
                        ((SquareImageView)feedChildGet.findViewById(R.id.btnLike)).setBackgroundResource(R.drawable.liked);
                        ((TextView)feedChildGet.findViewById(R.id.txtLikeCount)).setText(String.valueOf(feedsList.get(currentChoice2).getLikeCount() + 1) + " " + getResources().getString(R.string.likeCount));
                        Database.getInstance().likePost(feedsList.get(currentChoice2));
                        feedsList.get(currentChoice2).setLikeCount(feedsList.get(currentChoice2).getLikeCount() + 1);
                    }
                    isLiked = !isLiked;
                    feedsList.get(currentChoice2).setLiked(isLiked);
                }
            });

            btnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < lnloFeeds.getChildCount(); j++)
                    {
                        LinearLayout feedChild =(LinearLayout)lnloFeeds.getChildAt(j);
                        LinearLayout tmpLnlo = (LinearLayout)feedChild.getChildAt(0);
                        LinearLayout getSmallerChild = (LinearLayout)tmpLnlo.getChildAt(3);
                        for (int k = 0; k < getSmallerChild.getChildCount(); k++)
                        {
                            if (getSmallerChild.getChildAt(k) == v)
                            {
                                currentChoice3 = j;
                                break;
                            }
                        }
                    }
                    openCommentFragment();
                }
            });

            txtCommentCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < lnloFeeds.getChildCount(); j++)
                    {
                        LinearLayout feedChild =(LinearLayout)lnloFeeds.getChildAt(j);
                        LinearLayout tmpLnlo = (LinearLayout)feedChild.getChildAt(0);
                        RelativeLayout getSmallerChild = (RelativeLayout)tmpLnlo.getChildAt(4);
                        for (int k = 0; k < getSmallerChild.getChildCount(); k++)
                        {
                            if (getSmallerChild.getChildAt(k) == v)
                            {
                                currentChoice3 = j;
                                break;
                            }
                        }
                    }
                    openCommentFragment();
                }
            });

            tmp.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    for (int j = 0; j < lnloFeeds.getChildCount(); j++)
                    {
                        if (v == lnloFeeds.getChildAt(j))
                        {
                            if (feedsList.get(j).getUsernamePoster().equals(ThisUser.getInstance(null).getUsername()))
                            {
                                openEditFeedFragment(j);
                            }
                            break;
                        }
                    }
                    return true;
                }
            });
            lnloFeeds.addView(tmp);
        }
    }

    private void openCommentFragment() {
        toComment = true;
        CommentFragment commentFragment = new CommentFragment(currentChoice3, feedsList.get(currentChoice3));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.add(R.id.commentFragment,
                commentFragment).addToBackStack(null).commit();
    }


    public void closeCommentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CommentFragment commentFragment = (CommentFragment) fragmentManager
                .findFragmentById(R.id.commentFragment);
        if (commentFragment != null) {
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(commentFragment).commit();
        }
        toComment = false;
        loadFeeds(currentChoice);
    }

    private void openEditFeedFragment(int index) {
        toEdit = true;
        EditFeedFragment selectionFragment = new EditFeedFragment(feedsList.get(index));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.add(R.id.editFeedFragment,
                selectionFragment).addToBackStack(null).commit();
    }

    public void closeEditFeedFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        EditFeedFragment simpleFragment = (EditFeedFragment) fragmentManager
                .findFragmentById(R.id.editFeedFragment);
        if (simpleFragment != null) {
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(simpleFragment).commit();
        }
        toEdit = false;
        loadFeeds(currentChoice);
    }
}