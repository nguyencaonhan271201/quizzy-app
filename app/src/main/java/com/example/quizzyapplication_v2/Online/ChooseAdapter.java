package com.example.quizzyapplication_v2.Online;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quizzyapplication_v2.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ChooseAdapter extends ArrayAdapter<com.example.quizzyapplication_v2.Online.Topic> {
    private Context context;
    private int layoutID;
    private ArrayList<com.example.quizzyapplication_v2.Online.Topic> topics;
    public ChooseAdapter(@NonNull Context context,
                         int resource,
                         @NonNull List<com.example.quizzyapplication_v2.Online.Topic> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutID = resource;
        this.topics = (ArrayList<com.example.quizzyapplication_v2.Online.Topic>)objects;
    }

    @Override
    public int getCount() {
        return topics.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(layoutID, null, false);
        }

        com.example.quizzyapplication_v2.Online.Topic tp = topics.get(position);

        ImageView imgTopic = convertView.findViewById(R.id.topicImage);

        Picasso.get().load(tp.getImage()).error(R.drawable.ic_launcher_foreground).fit().into(imgTopic);

        return convertView;
    }
}
