package com.example.quizzyapplication_v2.Online;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quizzyapplication_v2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.xml.sax.DTDHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoomAdapter extends ArrayAdapter<Room> {
    private Context context;
    private int layoutID;
    private ArrayList<Room> rooms;
    public RoomAdapter(@NonNull Context context,
                       int resource,
                       @NonNull List<Room> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutID = resource;
        this.rooms = (ArrayList<Room>)objects;
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    TextView Level1;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(layoutID, null, false);
        }

        Room tp = rooms.get(position);

        TextView ID = convertView.findViewById(R.id.room_lstview_ID);
        TextView NoOfQuest = convertView.findViewById(R.id.room_lstview_numberOfQuest);
        TextView Player1 = convertView.findViewById(R.id.room_lstview_player1);
        Level1 = convertView.findViewById(R.id.room_lstview_level1);

        ID.setText(String.valueOf(tp.getId()));
        NoOfQuest.setText(context.getResources().getString(R.string.noOfQuest) + ": " + String.valueOf(tp.getNumberOfQuest()));
        Player1.setText(context.getResources().getString(R.string.player) + " 1: " + tp.getPlayer1());
        Level1.setText(context.getResources().getString(R.string.level) + ": " + String.valueOf(tp.getPlayer1Level()));

        return convertView;
    }
}
