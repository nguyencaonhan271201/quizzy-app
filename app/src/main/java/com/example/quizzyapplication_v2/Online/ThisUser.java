package com.example.quizzyapplication_v2.Online;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ThisUser extends User {
    private static ThisUser instance;
    private int coin;
    private FirebaseDatabase storage;
    private DatabaseReference dbReference;
    private ArrayList<Power> powersList = new ArrayList<Power>();
    private ArrayList<com.example.quizzyapplication_v2.Online.Topic> favoriteTopics = new ArrayList<com.example.quizzyapplication_v2.Online.Topic>();
    private Context context;

    public ThisUser(String username, String email, int exp, int level, String profileImage, final Context context) {
        super(username, email, exp, level, profileImage);
        this.context = context;
        storage = FirebaseDatabase.getInstance();
        dbReference = storage.getReference("users/" + getUsername());
        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = (String)snapshot.child("email").getValue();
                int exp = Integer.parseInt(String.valueOf(snapshot.child("exp").getValue()));
                int level = Integer.parseInt(String.valueOf(snapshot.child("level").getValue()));
                int coin = Integer.parseInt(String.valueOf(snapshot.child("coin").getValue()));
                setEmail(email);
                setExp(exp);
                setLevel(level);
                setCoin(coin);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public ArrayList<Topic> getFavoriteTopics() {
        return favoriteTopics;
    }

    public void setFavoriteTopics(ArrayList<Topic> favoriteTopics) {
        this.favoriteTopics = favoriteTopics;
    }

    public ThisUser(Context context)
    {
        super();
        powersList = new ArrayList<Power>();

        int[] powerPrices = {50, 50, 50, 100, 150, 100, 100, 200};
        for (int i = 0; i < 8; i++)
        {
            Power tmp = new Power(i, null, null,
                    null, powerPrices[i]);
            powersList.add(tmp);
        }
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    static public ThisUser getInstance(Context context) {
        if (instance == null)
            instance = new ThisUser(context);
        return instance;
    }

    public ArrayList<Power> getPowersList() {
        return powersList;
    }

    public void setPowersList(ArrayList<Power> powersList) {
        this.powersList = powersList;
    }

    public void updateProfileImage(final String profileURL) {
        this.setProfileImage(profileURL);
        FirebaseDatabase.getInstance().getReference("users/" + getUsername() + "/profileImage").setValue(profileURL);
        DatabaseReference updateStatus = FirebaseDatabase.getInstance().getReference("feeds");
        updateStatus.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot feeds : snapshot.getChildren())
                {
                    String getPoster = (String)(feeds.child("usernamePoster").getValue());
                    if (getPoster.equals(getUsername()))
                    {
                        feeds.child("avatarPoster").getRef().setValue(profileURL);
                    }
                    if (feeds.child("comments").exists())
                    {
                        for (DataSnapshot snapshot1 : feeds.child("comments").getChildren())
                        {
                            if (((String)snapshot1.child("username").getValue()).equals(getUsername()))
                                snapshot1.child("profile").getRef().setValue(profileURL);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}