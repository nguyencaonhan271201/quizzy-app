package com.example.quizzyapplication_v2.Online;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizzyapplication_v2.R;
import com.example.quizzyapplication_v2.SquareImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentFragment extends Fragment {
    private int feedPosition;
    private Feed feed;
    private ArrayList<Comment> commentsList;
    private LinearLayout lnloComments;
    private int currentCommentGet = -1;
    private int currentCommentDeleteGet = -1;
    private EditText txtAddComment;
    public CommentFragment(int pos, Feed f) {
        feedPosition = pos;
        feed = f;
    }

    public static CommentFragment newInstance(int pos, Feed f) {
        CommentFragment fragment = new CommentFragment(pos, f);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        final View rootView =
                inflater.inflate(R.layout.fragment_comment, container, false);

        SquareImageView imgPoster = rootView.findViewById(R.id.imgCommentPostAva);
        TextView txtPostUsername = rootView.findViewById(R.id.txtCommentPostUsername);
        TextView txtPostTopic = rootView.findViewById(R.id.txtCommentPostTopic);
        TextView txtPostContent = rootView.findViewById(R.id.txtCommentPostContent);
        ImageView imgPost = rootView.findViewById(R.id.imgCommentPost);
        TextView txtLikeCount = rootView.findViewById(R.id.txtCommentPostLikeCount);
        TextView txtFeedTime = rootView.findViewById(R.id.txtCommentPostTime);
        txtAddComment = rootView.findViewById(R.id.txtAddComment);

        Feed f = ((NewsFeedActivity)getActivity()).feedsList.get(feedPosition);

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
        txtLikeCount.setText(String.valueOf(f.getLikeCount()) + " " + getResources().getString(R.string.likeCount));
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

        commentsList = f.getComments();
        lnloComments = rootView.findViewById(R.id.lnloComments);
        loadComments();
        rootView.findViewById(R.id.btnAddComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtAddComment.getText().toString().equals(""))
                {
                    Toast.makeText(getActivity(), getResources().getString(R.string.blankContent), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Comment comment = new Comment(feed.getTimestamp(), System.currentTimeMillis(), ThisUser.getInstance(null).getUsername(),
                            ThisUser.getInstance(null).getProfileImage(),
                            txtAddComment.getText().toString(), false, 0);
                    Database.getInstance().addComment(comment);
                    txtAddComment.setText("");
                }
            }
        });

        handleCommentChangeListener();

        return rootView;
    }

    private void handleCommentChangeListener() {
        FirebaseDatabase.getInstance().getReference("feeds/" + feed.getTimestamp() + "/comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (((NewsFeedActivity)getActivity()).toComment)
                {
                    commentsList.clear();
                    if (snapshot.getValue() != null)
                    {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            long commentID = Long.parseLong((String) snapshot1.getKey());
                            String commentContent = (String) snapshot1.child("content").getValue();
                            String username = (String) snapshot1.child("username").getValue();
                            String profileImage = (String) snapshot1.child("profile").getValue();
                            int likeCount = 0;
                            if (snapshot1.child("like").exists())
                                likeCount = Integer.parseInt(String.valueOf(snapshot1.child("like").getChildrenCount()));
                            Comment comment = new Comment(feed.getTimestamp(), commentID, username, profileImage, commentContent, false, likeCount);
                            boolean isCommentLiked = false;
                            if (snapshot1.child("like").exists()) {
                                for (DataSnapshot snapshot2 : snapshot1.child("like").getChildren()) {
                                    if (snapshot2.getValue().equals(ThisUser.getInstance(null).getUsername())) {
                                        isCommentLiked = true;
                                        break;
                                    }
                                }
                            }
                            comment.setLiked(isCommentLiked);
                            commentsList.add(comment);
                        }
                    }
                    loadComments();
                    reUpdateToParent();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadComments() {
        lnloComments.removeAllViews();
        for (int i = 0; i < commentsList.size(); i++)
        {
            if (getActivity() != null)
            {
                LinearLayout tmp = (LinearLayout)LayoutInflater.from(getActivity()).inflate(R.layout.comment_layout, null, false);
                SquareImageView imgComment = tmp.findViewById(R.id.imgComment);
                TextView txtCommentUsername = tmp.findViewById(R.id.txtCommentUsername);
                TextView txtCommentContent = tmp.findViewById(R.id.txtCommentContent);
                TextView txtCommentTime = tmp.findViewById(R.id.txtCommentTime);
                TextView txtCommentLikeCount = tmp.findViewById(R.id.txtCommentLikeCount);
                SquareImageView btnCommentLike = tmp.findViewById(R.id.btnCommentLike);

                Comment getComment = commentsList.get(i);
                Picasso.get().load(getComment.getProfileImage()).error(R.drawable.default_ava).into(imgComment);
                txtCommentUsername.setText(getComment.getUsername());
                txtCommentContent.setText(getComment.getContent());
                txtCommentLikeCount.setText(String.valueOf(getComment.getLikeCount()) + " " + getResources().getString(R.string.likeCount));
                long timeDifference = System.currentTimeMillis() - getComment.getCommentTime();
                long aWeek = 86400000 * 7;
                if (timeDifference < 60000)
                {
                    int getSeconds = (int)timeDifference/1000;
                    txtCommentTime.setText(String.valueOf(getSeconds) + " " + getResources().getString(R.string.seconds));
                }
                else if (timeDifference < 3600000)
                {
                    int getMinutes = (int)timeDifference / 60000;
                    txtCommentTime.setText(String.valueOf(getMinutes) + " " + getResources().getString(R.string.minute));
                }
                else if (timeDifference < 86400000)
                {
                    int getHours = (int)timeDifference / 3600000;
                    txtCommentTime.setText(String.valueOf(getHours) + " " + getResources().getString(R.string.hour));
                }
                else if (timeDifference < aWeek)
                {
                    long getDay = timeDifference / 86400000;
                    txtCommentTime.setText(String.valueOf(getDay) + " " + getResources().getString(R.string.days));
                }
                else
                {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String dateString = formatter.format(new Date(getComment.getCommentTime()));
                    txtCommentTime.setText(dateString);
                }
                if (getComment.isLiked())
                    btnCommentLike.setBackgroundResource(R.drawable.liked);
                else
                    btnCommentLike.setBackgroundResource(R.drawable.not_liked);

                btnCommentLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout smallerLnlo = null;
                        LinearLayout contentLnlo = null;
                        for (int j = 0; j < lnloComments.getChildCount(); j++)
                        {
                            LinearLayout commentChild =(LinearLayout)lnloComments.getChildAt(j);
                            LinearLayout tmp = (LinearLayout)commentChild.getChildAt(0);
                            LinearLayout commentDivChild = (LinearLayout)tmp.getChildAt(2);
                            LinearLayout mainDivChild = (LinearLayout)tmp.getChildAt(1);
                            for (int k = 0; k < commentDivChild.getChildCount(); k++) {
                                if (commentDivChild.getChildAt(k) == v) {
                                    currentCommentGet = j;
                                    smallerLnlo = commentDivChild;
                                    contentLnlo = (LinearLayout) mainDivChild.getChildAt(2);
                                    break;
                                }
                            }
                        }
                        boolean isLiked = commentsList.get(currentCommentGet).isLiked();
                        if (isLiked)
                        {
                            ((SquareImageView)smallerLnlo.findViewById(R.id.btnCommentLike)).setBackgroundResource(R.drawable.not_liked);
                            ((TextView)contentLnlo.findViewById(R.id.txtCommentLikeCount)).setText(String.valueOf(commentsList.get(currentCommentGet).getLikeCount() - 1) + " " + getResources().getString(R.string.likeCount));
                            Database.getInstance().unlikeComment(commentsList.get(currentCommentGet));
                            commentsList.get(currentCommentGet).setLikeCount(commentsList.get(currentCommentGet).getLikeCount() - 1);
                        }
                        else
                        {
                            ((SquareImageView)smallerLnlo.findViewById(R.id.btnCommentLike)).setBackgroundResource(R.drawable.liked);
                            ((TextView)contentLnlo.findViewById(R.id.txtCommentLikeCount)).setText(String.valueOf(commentsList.get(currentCommentGet).getLikeCount() + 1) + " " + getResources().getString(R.string.likeCount));
                            Database.getInstance().likeComment(commentsList.get(currentCommentGet));
                            commentsList.get(currentCommentGet).setLikeCount(commentsList.get(currentCommentGet).getLikeCount() + 1);
                        }
                        isLiked = !isLiked;
                        commentsList.get(currentCommentGet).setLiked(isLiked);
                        reUpdateToParent();
                    }
                });

                tmp.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        for (int j = 0; j < lnloComments.getChildCount(); j++)
                        {
                            if (v == lnloComments.getChildAt(j))
                            {
                                if (commentsList.get(j).getUsername().equals(ThisUser.getInstance(null).getUsername()))
                                {
                                    currentCommentDeleteGet = j;
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle("Confirmation")
                                            .setMessage(getResources().getString(R.string.deleteCommentConfirmation))
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Database.getInstance().deleteComment(commentsList.get(currentCommentDeleteGet));
                                                }
                                            })
                                            .setNegativeButton(getResources().getString(R.string.no), null)
                                            .show();
                                }
                                break;
                            }
                        }
                        return true;
                    }
                });

                lnloComments.addView(tmp);
            }
        }
    }

    private void reUpdateToParent() {
        if (getActivity() != null)
        {
            ArrayList<Feed> get = ((NewsFeedActivity)getActivity()).feedsList;
            for (int i = 0; i < get.size(); i++)
            {
                if (get.get(i).getTimestamp() == feed.getTimestamp())
                {
                    ((NewsFeedActivity)getActivity()).feedsList.get(i).setComments(commentsList);
                    break;
                }
            }
        }
    }
}