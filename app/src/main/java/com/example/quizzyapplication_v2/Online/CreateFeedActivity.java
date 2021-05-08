package com.example.quizzyapplication_v2.Online;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizzyapplication_v2.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.io.IOException;
import java.util.ArrayList;

public class CreateFeedActivity extends AppCompatActivity {

    private ImageView imgTopic;
    private Spinner topicSpin;
    private TextView feedContent;
    private Uri imageUri;
    private String topicImageURL;
    private String feedImageURL;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_create_feed);

        imgTopic = findViewById(R.id.imgPostImage);
        topicSpin = findViewById(R.id.spinnerInputPostTopic);
        feedContent = findViewById(R.id.txtInputPostContent);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        ArrayList<String> feedTopics = new ArrayList<String>();
        for (com.example.quizzyapplication_v2.Online.Topic topic : Database.getInstance().getTopicsList())
        {
            if (!topic.getName().equals("Tổng hợp"))
            {
                feedTopics.add(topic.getName());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_checked_item, feedTopics);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topicSpin.setAdapter(adapter);
        topicImageURL = "";
        feedImageURL = "";

        SpaceNavigationView bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_home_24));
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_add_circle_24));
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_storefront_24));
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_person_24));
        bottom_navigation.changeCurrentItem(1);
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
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            topicImageURL = imageUri.toString();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(topicImageURL));
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgTopic.setImageBitmap(bitmap);
            imgTopic.getLayoutParams().width = 500;
            imgTopic.getLayoutParams().height = 500;
            imgTopic.requestLayout();
            imgTopic.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent feedIntent = new Intent(getApplicationContext(), NewsFeedActivity.class);
        startActivity(feedIntent);
        finish();
    }

    public void postFeed(View view)
    {
        if (feedContent.getText().toString().equals(""))
        {
            Toast.makeText(this, getResources().getString(R.string.blankContent), Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (!topicImageURL.equals(""))
            {
                progressDialog.show();
                String currentTime = String.valueOf(System.currentTimeMillis());
                final StorageReference storageRef =  FirebaseStorage.getInstance().getReference().child("/feeds/" + currentTime);
                storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                progressDialog.hide();
                                feedImageURL = uri.toString();
                                String feedTopic = (String)topicSpin.getSelectedItem();
                                String feedContentString = feedContent.getText().toString();
                                Database.getInstance().updatePost(feedTopic, feedImageURL, feedContentString);
                                Intent feedIntent = new Intent(getApplicationContext(), NewsFeedActivity.class);
                                startActivity(feedIntent);
                                finish();
                            }
                        });
                    }
                });
            }
            else
            {
                progressDialog.show();
                String feedTopic = (String)topicSpin.getSelectedItem();
                String feedContentString = feedContent.getText().toString();
                Database.getInstance().updatePost(feedTopic, feedImageURL, feedContentString);
                Intent feedIntent = new Intent(getApplicationContext(), NewsFeedActivity.class);
                progressDialog.hide();
                startActivity(feedIntent);
                finish();
            }
        }
    }
}