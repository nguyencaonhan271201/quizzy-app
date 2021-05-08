package com.example.quizzyapplication_v2.Online;

import android.content.Context;
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
import com.example.quizzyapplication_v2.SquareImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedAdapter extends ArrayAdapter<Feed> {
    private Context context;
    private int layoutID;
    private ArrayList<Feed> feeds;
    public FeedAdapter(@NonNull Context context,
                             int resource,
                             @NonNull List<Feed> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutID = resource;
        this.feeds = (ArrayList<Feed>)objects;
    }

    @Override
    public int getCount() {
        return feeds.size();
    }

    ImageView imgPost;
    boolean isLiked;
    SquareImageView btnLike;
    TextView txtLikeCount;
    Feed f;
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(layoutID, null, false);
        }

        f = feeds.get(position);
        isLiked = f.isLiked();

        SquareImageView imgPoster = convertView.findViewById(R.id.imgPoster);
        TextView txtPostUsername = convertView.findViewById(R.id.txtPostUsername);
        TextView txtPostTopic = convertView.findViewById(R.id.txtPostTopic);
        TextView txtPostContent = convertView.findViewById(R.id.txtPostContent);
        imgPost = convertView.findViewById(R.id.imgPost);
        txtLikeCount = convertView.findViewById(R.id.txtLikeCount);
        TextView txtCommentCount = convertView.findViewById(R.id.txtCommentCount);
        TextView txtFeedTime = convertView.findViewById(R.id.txtFeedTime);

        Picasso.get().load(f.getAvatarPoster()).error(R.drawable.default_ava).into(imgPoster);
        if (!f.getFeedImage().equals(""))
        {
            imgPost.setVisibility(View.VISIBLE);
            Picasso.get().load(f.getFeedImage()).error(R.drawable.ic_launcher_foreground).into(imgPost);
        }
        else imgPost.setVisibility(View.GONE);
        txtPostUsername.setText(f.getUsernamePoster());
        txtPostTopic.setText(f.getTopicName());
        txtPostContent.setText(f.getFeedContent());
        txtLikeCount.setText(String.valueOf(f.getLikeCount()) + " " + context.getResources().getString(R.string.likeCount));
        ArrayList<Comment> tmp = f.getComments();
        txtCommentCount.setText(String.valueOf(tmp) + " " + context.getResources().getString(R.string.comment));
        long timeDifference = System.currentTimeMillis() - f.getTimestamp();
        long aWeek = 86400000 * 7;
        if (timeDifference < 60000)
        {
            int getSeconds = (int)timeDifference/1000;
            txtFeedTime.setText(String.valueOf(getSeconds) + " " + context.getResources().getString(R.string.seconds));
        }
        else if (timeDifference < 3600000)
        {
            int getMinutes = (int)timeDifference / 60000;
            txtFeedTime.setText(String.valueOf(getMinutes) + " " + context.getResources().getString(R.string.minute));
        }
        else if (timeDifference < 86400000)
        {
            int getHours = (int)timeDifference / 3600000;
            txtFeedTime.setText(String.valueOf(getHours) + " " + context.getResources().getString(R.string.hour));
        }
        else if (timeDifference < aWeek)
        {
            long getDay = timeDifference / 86400000;
            txtFeedTime.setText(String.valueOf(getDay) + " " + context.getResources().getString(R.string.days));
        }
        else
        {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String dateString = formatter.format(new Date(f.getTimestamp()));
            txtFeedTime.setText(dateString);
        }

        btnLike = convertView.findViewById(R.id.btnLike);
        SquareImageView btnComment = convertView.findViewById(R.id.btnComment);

        if (isLiked)
            btnLike.setBackgroundResource(R.drawable.liked);
        else
            btnLike.setBackgroundResource(R.drawable.not_liked);

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f = feeds.get(position);
                isLiked = f.isLiked();
                if (isLiked)
                {
                    btnLike.setBackgroundResource(R.drawable.not_liked);
                    txtLikeCount.setText(String.valueOf(f.getLikeCount() - 1) + " " + context.getResources().getString(R.string.likeCount));
                    Database.getInstance().unlikePost(f);
                    f.setLikeCount(f.getLikeCount() - 1);
                }
                else
                {
                    btnLike.setBackgroundResource(R.drawable.liked);
                    txtLikeCount.setText(String.valueOf(f.getLikeCount() + 1) + " " + context.getResources().getString(R.string.likeCount));
                    Database.getInstance().likePost(f);
                    f.setLikeCount(f.getLikeCount() + 1);
                }
                isLiked = !isLiked;
                f.setLiked(isLiked);
            }
        });

        return convertView;
    }
}
