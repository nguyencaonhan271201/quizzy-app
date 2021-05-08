package com.example.quizzyapplication_v2.Offline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quizzyapplication_v2.R;
import com.example.quizzyapplication_v2.SquareImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends ArrayAdapter<com.example.quizzyapplication_v2.Offline.Topic> {
    private Context context;
    private int layoutID;
    private ArrayList<com.example.quizzyapplication_v2.Offline.Topic> topics;
    public MyAdapter(@NonNull Context context,
                     int resource,
                     @NonNull List<com.example.quizzyapplication_v2.Offline.Topic> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutID = resource;
        this.topics = (ArrayList<com.example.quizzyapplication_v2.Offline.Topic>)objects;
        this.topics.add(new com.example.quizzyapplication_v2.Offline.Topic("DummyForAddButton", "add.png"));
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

        SquareImageView TopicImage = convertView.findViewById(R.id.topicImage);
        TextView txtChooseTopic = convertView.findViewById(R.id.txtChooseTopic);

        com.example.quizzyapplication_v2.Offline.Topic tp = topics.get(position);
        //int resID = context.getResources().getIdentifier(tp.getImage(), "drawable", "com.example.quizzy");
        //TopicImage.setImageResource(resID);
        if (tp.getImage().contains("content"))
        {
            Uri uri = Uri.parse(tp.getImage());
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                TopicImage.setImageURI(uri);
            }
            catch (SecurityException e)
            {
                QuizDbHelper.getInstance(context).errorTopicImage(tp);
                InputStream ims = null;
                try {
                    ims = context.getAssets().open("topics/topic.png");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Drawable d = Drawable.createFromStream(ims, null);
                TopicImage.setImageDrawable(d);
            }
            catch (Exception e) {
                e.printStackTrace();
                InputStream ims = null;
                try {
                    ims = context.getAssets().open("topics/topic.png");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Drawable d = Drawable.createFromStream(ims, null);
                TopicImage.setImageDrawable(d);
            }
            txtChooseTopic.setVisibility(View.INVISIBLE);
        }
        else
        {
            try {
                InputStream ims = context.getAssets().open("topics/" + tp.getImage());
                Drawable d = Drawable.createFromStream(ims, null);
                TopicImage.setImageDrawable(d);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (tp.getImage().equals("topic.png"))
            {
                txtChooseTopic.setText(tp.getName());
                txtChooseTopic.setVisibility(View.VISIBLE);
            }
            else txtChooseTopic.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
}