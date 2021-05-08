package com.example.quizzyapplication_v2.Offline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quizzyapplication_v2.R;
import com.example.quizzyapplication_v2.SquareImageView;
import com.shawnlin.numberpicker.NumberPicker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PreGameActivity extends AppCompatActivity {
    private String imageLink;
    private int topicID;
    private SquareImageView TopicImage;
    private NumberPicker numberOfQuest;
    private String topicName;
    private TextView txtChooseTopic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportPostponeEnterTransition();
        postponeEnterTransition();
        startPostponedEnterTransition();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_pre_game);

        TopicImage = findViewById(R.id.topicImage);
        numberOfQuest = findViewById(R.id.npNoOfQuest);
        txtChooseTopic = findViewById(R.id.txtChooseTopic);
        Intent intent = this.getIntent();
        imageLink = intent.getStringExtra("topicImage");
        topicID = intent.getIntExtra("topicID", 0);
        topicName = intent.getStringExtra("topicName");

        //Get maximum number of questions
        QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);
        ArrayList<com.example.quizzyapplication_v2.Offline.Question> questionList = dbHelper.getQuestions(topicName);
        numberOfQuest.setMinValue(1);
        numberOfQuest.setMaxValue(questionList.size());
        numberOfQuest.setValue(5);
        InputStream ims = null;

        if (imageLink.contains("content"))
        {
            Uri uri = Uri.parse(imageLink);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            TopicImage.setImageURI(uri);
            txtChooseTopic.setVisibility(View.INVISIBLE);
        }
        else
        {
            try {
                ims = getAssets().open("topics/" + imageLink);
                Drawable d = Drawable.createFromStream(ims, null);
                TopicImage.setImageDrawable(d);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imageLink.equals("topic.png"))
            {
                txtChooseTopic.setText(topicName);
                txtChooseTopic.setVisibility(View.VISIBLE);
            }
            else txtChooseTopic.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void startGame(View view) {
        Intent startGame = new Intent(this, GameActivity.class);
        startGame.putExtra("TopicID", topicID);
        startGame.putExtra("NoOfQuest", numberOfQuest.getValue());
        startGame.putExtra("TopicName", topicName);
        startGame.putExtra("TopicImage", imageLink);
        startActivity(startGame);
        finish();
    }
}