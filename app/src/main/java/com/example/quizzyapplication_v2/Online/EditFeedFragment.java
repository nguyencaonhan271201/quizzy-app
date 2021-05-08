package com.example.quizzyapplication_v2.Online;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizzyapplication_v2.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class EditFeedFragment extends Fragment {

    private Feed f;
    private ProgressDialog progressDialog;
    private String imageURL;
    private ImageView imgEditPost;
    private RelativeLayout imgEditPostly;
    private ImageButton btnPostImage;
    private TextView txtFeedContent;
    private boolean isImageChanged = false;

    public EditFeedFragment(Feed get) {
        f = get;
    }

    public static EditFeedFragment newInstance(Feed get) {
        EditFeedFragment fragment = new EditFeedFragment(get);
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
                inflater.inflate(R.layout.fragment_edit_feed, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Uploading...");

        ImageButton btnPost = rootView.findViewById(R.id.btnEditFeed);
        ImageButton btnDelete = rootView.findViewById(R.id.btnDeleteFeed);
        Button btnBack = rootView.findViewById(R.id.btnBack);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtFeedContent.getText().toString().equals(""))
                {
                    Toast.makeText(getActivity(), getResources().getString(R.string.blankContent), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    f.setFeedContent(txtFeedContent.getText().toString());
                    if (isImageChanged)
                    {
                        progressDialog.show();
                        String currentTime = String.valueOf(System.currentTimeMillis());
                        final StorageReference storageRef =  FirebaseStorage.getInstance().getReference().child("/feeds/" + currentTime);
                        storageRef.putFile(Uri.parse(imageURL)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        progressDialog.hide();
                                        imageURL = uri.toString();
                                        f.setFeedImage(imageURL);
                                        Database.getInstance().editPost(f);
                                        ((NewsFeedActivity)getActivity()).closeEditFeedFragment();
                                    }
                                });
                            }
                        });
                    }
                    else
                    {
                        progressDialog.show();
                        f.setFeedImage(imageURL);
                        Database.getInstance().editPost(f);
                        progressDialog.hide();
                        ((NewsFeedActivity)getActivity()).closeEditFeedFragment();
                    }
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database.getInstance().deletePost(f);
                ((NewsFeedActivity)getActivity()).closeEditFeedFragment();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewsFeedActivity)getActivity()).closeEditFeedFragment();
            }
        });


        imgEditPostly = rootView.findViewById(R.id.imgEditPostly);
        imgEditPost = imgEditPostly.findViewById(R.id.imgEditPost);
        btnPostImage = rootView.findViewById(R.id.btnPostImage);

        btnPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFeed(v);
            }
        });


        ImageButton btnChangeImage = imgEditPostly.findViewById(R.id.btnEditChangeImage);
        ImageButton btnDeleteImage = imgEditPostly.findViewById(R.id.btnEditDeleteImage);

        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFeed(v);
            }
        });

        btnDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageURL = "";
                imgEditPostly.setVisibility(View.INVISIBLE);
                btnPostImage.setVisibility(View.VISIBLE);
            }
        });

        txtFeedContent = rootView.findViewById(R.id.txtEditFeedContent);

        txtFeedContent.setText(f.getFeedContent());
        imageURL = f.getFeedImage();
        if (!imageURL.equals(""))
        {
            imgEditPostly.setVisibility(View.VISIBLE);
            Picasso.get().load(f.getFeedImage()).error(R.drawable.ic_launcher_foreground).into(imgEditPost);
        }
        else btnPostImage.setVisibility(View.VISIBLE);

        return rootView;
    }

    public void chooseImageFeed(View view)
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Feed Image"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            imageURL = imageUri.toString();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(imageURL));
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgEditPost.setImageBitmap(bitmap);
            imgEditPost.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            imgEditPost.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            imgEditPost.requestLayout();
            btnPostImage.setVisibility(View.INVISIBLE);
            imgEditPostly.setVisibility(View.VISIBLE);
            isImageChanged = true;
        }
    }
}