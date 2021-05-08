package com.example.quizzyapplication_v2.Offline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizzyapplication_v2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private MediaPlayer subMediaPlayer;
    boolean doubleBackToExitPressedOnce;
    private float px2dp;
    private ArrayList<Button> btnList = new ArrayList<>();
    private TextView txtQuestion;
    private CardView imgQuestionCard;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewTopic;
    private Button btnNextQuest;
    private List<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private com.example.quizzyapplication_v2.Offline.Question currentQuestion;
    private int correctOption;
    private int score;
    private boolean answered;
    private int currentTime;
    private int currentScore;
    private int topicID;
    private int currentQuestCount;

    String topicImage;
    String topicName;

    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_game);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.quizzy_background);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        px2dp = getApplicationContext().getResources().getDisplayMetrics().density;

        btnList.add((Button) findViewById(R.id.Opt1));
        btnList.add((Button) findViewById(R.id.Opt2));
        btnList.add((Button) findViewById(R.id.Opt3));
        btnList.add((Button) findViewById(R.id.Opt4));

        txtQuestion = findViewById(R.id.Question);
        imgQuestionCard = findViewById(R.id.QuestionImageCard);
        textViewScore = findViewById(R.id.txtScore);
        textViewQuestionCount = findViewById(R.id.txtQuestionCount);
        textViewTopic = findViewById(R.id.txtTopic);
        btnNextQuest = findViewById(R.id.btnNextQuest);

        Intent intent = getIntent();
        topicID = intent.getIntExtra("TopicID", 0);
        topicImage = intent.getStringExtra("TopicImage");
        questionCountTotal = intent.getIntExtra("NoOfQuest", 0);
        topicName = intent.getStringExtra("TopicName");

        textViewTopic.setText("Chủ đề: " + topicName);

        QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);
        questionList = dbHelper.getQuestions(topicName);
        Collections.shuffle(questionList);
        questionList = questionList.subList(0, questionCountTotal);
        doubleBackToExitPressedOnce = false;
        score = 0;
        currentQuestCount = 0;
        showNextQuestion();
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            mediaPlayer.stop();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click ↩ again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void showNextQuestion() {
        for (Button button : btnList)
        {
            button.setBackgroundResource(R.drawable.app_button_border);
            button.setClickable(true);
        }
        btnNextQuest.setVisibility(View.INVISIBLE);
        currentQuestion = questionList.get(questionCounter);
        txtQuestion.setText(currentQuestion.getQuestion());
        Integer[] arr = new Integer[]{1, 2, 3, 4};
        List<Integer> tmpList = Arrays.asList(arr);
        Collections.shuffle(tmpList);
        correctOption = tmpList.indexOf(currentQuestion.getAnswerNr()) + 1;
        List<String> optionList = new ArrayList<String>();
        optionList.add(currentQuestion.getOption1());
        optionList.add(currentQuestion.getOption2());
        optionList.add(currentQuestion.getOption3());
        optionList.add(currentQuestion.getOption4());
        //Show the options to corresponding button
        for (int i = 0; i < 4; i++)
        {
            btnList.get(i).setText(optionList.get(tmpList.get(i) - 1));
        }
        //Check whether the question has image to show
        if (currentQuestion.getImage().equals(""))
        {
            AnHinh(imgQuestionCard.findViewById(R.id.QuestionImage));
        }
        else
        {
            if (currentQuestion.getImage().contains("content"))
            {
                Uri uri = Uri.parse(currentQuestion.getImage());
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ((ImageView)imgQuestionCard.findViewById(R.id.QuestionImage)).setImageURI(uri);
                    HienHinh(imgQuestionCard.findViewById(R.id.QuestionImage));
                }
                catch (SecurityException ex)
                {
                    QuizDbHelper.getInstance(this).errorQuestionImage(currentQuestion);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                try {
                    InputStream ims = getAssets().open(currentQuestion.getImage());
                    Drawable d = Drawable.createFromStream(ims, null);
                    ((ImageView)imgQuestionCard.findViewById(R.id.QuestionImage)).setImageDrawable(d);
                    HienHinh(imgQuestionCard.findViewById(R.id.QuestionImage));
                } catch (IOException e) {}
            }
        }

        questionCounter++;
        textViewQuestionCount.setText("Câu hỏi: " + questionCounter + "/" + questionCountTotal);
        answered = false;

        switch (currentQuestion.getDifficulty())
        {
            case 1:
                currentTime = 10;
                currentScore = 10;
                break;
            case 2:
                currentTime = 15;
                currentScore = 20;
                break;
            case 3:
                currentTime = 20;
                currentScore = 30;
                break;
        }
        DemNguoc((com.example.quizzyapplication_v2.CustomDrawableProgressBar)findViewById(R.id.gameprogressbar), currentTime);
    }

    private void CheckAnswer(int i) {
        answered = true;
        timer.cancel();
        if (subMediaPlayer != null && subMediaPlayer.isPlaying())
            subMediaPlayer.stop();
        if (i == correctOption)
        {
            subMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.right);
            score += currentScore;
            currentQuestCount++;
            textViewScore.setText("Điểm số: " + score);
        }
        else
            subMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.wrong);
        subMediaPlayer.start();
        showSolution();
    }

    private void showSolution() {
        for (int i = 1; i <= 4; i++)
        {
            btnList.get(i - 1).setClickable(false);
            if (i == correctOption)
                btnList.get(i - 1).setBackgroundResource(R.drawable.app_button_border_correct);
            else
                btnList.get(i - 1).setBackgroundResource(R.drawable.app_button_border_wrong);
        }

        if (questionCounter < questionCountTotal)
        {
            btnNextQuest.setText(">");
        }
        else
        {
            btnNextQuest.setText("");
            btnNextQuest.setBackgroundResource(R.drawable.tick);
        }
        btnNextQuest.setVisibility(View.VISIBLE);
    }

    private void finishQuiz() {
        Intent resultIntent = new Intent(this, PostGameActivity.class);
        resultIntent.putExtra("topicID", topicID);
        resultIntent.putExtra("topicImage", topicImage);
        resultIntent.putExtra("topicName", topicName);
        resultIntent.putExtra("point", score);
        resultIntent.putExtra("correctQuest", currentQuestCount);
        resultIntent.putExtra("totalQuest", questionCountTotal);
        startActivity(resultIntent);
        mediaPlayer.stop();
        finish();
    }

    public void HienHinh(View view) {
        if (imgQuestionCard.getVisibility() == View.INVISIBLE) {
            imgQuestionCard.setVisibility(View.VISIBLE);

            TextView Question;
            Question = findViewById(R.id.Question);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) Question.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.addRule(RelativeLayout.BELOW,R.id.QuestionImageCard);
            params.setMargins((int)(10*px2dp),(int)(20*px2dp),(int)(10*px2dp),0);
            Question.setLayoutParams(params);
        }
    }

    public void AnHinh(View view) {
        if (imgQuestionCard.getVisibility() == View.VISIBLE) {
            imgQuestionCard.setVisibility(View.INVISIBLE);

            TextView Question;
            Question = findViewById(R.id.Question);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) Question.getLayoutParams();
            params.height = (int)(200*px2dp);
            params.addRule(RelativeLayout.BELOW,R.id.gameprogressbar);
            params.setMargins((int)(10*px2dp),(int)(50*px2dp),(int)(10*px2dp),0);
            Question.setLayoutParams(params);
        }
    }

    private void DemNguoc(final com.example.quizzyapplication_v2.CustomDrawableProgressBar progressBar, int Max) {
        progressBar.setMax(Max);
        progressBar.setProgress(progressBar.getMax());

        timer = new CountDownTimer((long)currentTime*1000, 1000){
            public void onTick(long  millisUntilFinished) {
                progressBar.setProgress((float)millisUntilFinished/1000);
                progressBar.invalidate();
            }

            public void onFinish() {
                progressBar.setProgress(0);
                Snackbar.make(findViewById(R.id.game_layout),"Times Up",Snackbar.LENGTH_SHORT).show();
                showSolution();
            }
        }.start();
    }

    public void btnNextQuest_click(View view) {
        if (questionCounter < questionCountTotal)
        {
            showNextQuestion();
        }
        else
        {
            finishQuiz();
        }
    }

    public void Option1_Chosen(View view) {
        CheckAnswer(1);
    }

    public void Option2_Chosen(View view) {
        CheckAnswer(2);
    }

    public void Option3_Chosen(View view) {
        CheckAnswer(3);
    }

    public void Option4_Chosen(View view) {
        CheckAnswer(4);
    }
}