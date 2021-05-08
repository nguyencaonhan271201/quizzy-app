package com.example.quizzyapplication_v2.Offline;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizzyapplication_v2.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InsertQuestionActivity extends AppCompatActivity {
    private String topicImageURL = "";
    private String questionImageURL = "";
    private EditText edtTopicName;
    private RelativeLayout rtloTopic;
    private ImageView imgTopicImage;
    private RelativeLayout rtloQuestion;
    private ImageView imgQuestImage;
    private Spinner spinnerCategory;
    private Spinner spinnerDifficulty;
    private Spinner spinnerCorrectOption;
    private EditText edtQuestContent;
    private EditText edtQuestOption1;
    private EditText edtQuestOption2;
    private EditText edtQuestOption3;
    private EditText edtQuestOption4;
    private QuizDbHelper db;
    private List<com.example.quizzyapplication_v2.Offline.Topic> topics = new ArrayList<com.example.quizzyapplication_v2.Offline.Topic>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_insert_question);

        edtTopicName = findViewById(R.id.edtTopicName);
        rtloTopic = findViewById(R.id.rtloTopic);
        imgTopicImage = findViewById(R.id.imgTopicImage);
        imgQuestImage = findViewById(R.id.imgQuestImage);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        spinnerCorrectOption = findViewById(R.id.spinnerCorrectOption);
        edtQuestContent = findViewById(R.id.edtQuestionContent);
        edtQuestOption1 = findViewById(R.id.edtQuestOption1);
        edtQuestOption2 = findViewById(R.id.edtQuestOption2);
        edtQuestOption3 = findViewById(R.id.edtQuestOption3);
        edtQuestOption4 = findViewById(R.id.edtQuestOption4);
        rtloQuestion = findViewById(R.id.rtloQuestion);

        db = QuizDbHelper.getInstance(this);
        updateTopicsForQuest();
        //Set adapters for spinners
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.difficultyItems, R.layout.spinner_checked_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(this, R.array.correctOptionItems, R.layout.spinner_checked_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCorrectOption.setAdapter(adapter);
    }

    public void chooseImageTopic(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), 1);
    }

    public void chooseImageQuestion(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            topicImageURL = uri.toString();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(topicImageURL));
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgTopicImage.setImageBitmap(bitmap);
            imgTopicImage.getLayoutParams().width = 500;
            imgTopicImage.getLayoutParams().height = 500;
            imgTopicImage.requestLayout();
            imgTopicImage.setVisibility(View.VISIBLE);
        }
        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            questionImageURL = uri.toString();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(questionImageURL));
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgQuestImage.setImageBitmap(bitmap);
            imgQuestImage.getLayoutParams().width = 500;
            imgQuestImage.getLayoutParams().height = 500;
            imgQuestImage.requestLayout();
            imgQuestImage.setVisibility(View.VISIBLE);
        }
    }

    public void insertTopic(View view) {
        Button thisBtn = (Button)view;
        Button otherBtn = (Button)findViewById(R.id.themch);
        otherBtn.setBackgroundResource(R.drawable.insert_button_border);
        thisBtn.setBackgroundResource(R.drawable.app_button_border_correct);
        rtloQuestion.setVisibility(View.INVISIBLE);
        rtloTopic.setVisibility(View.VISIBLE);
    }

    private void updateTopicsForQuest()
    {
        topics = db.getAllTopics();
        ArrayAdapter<com.example.quizzyapplication_v2.Offline.Topic> adapterCategories = new ArrayAdapter<com.example.quizzyapplication_v2.Offline.Topic>(this,
                R.layout.spinner_checked_item, topics);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategories);
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

    public void addTopic(View view) {
        for (com.example.quizzyapplication_v2.Offline.Topic topic : topics)
        {
            if (topic.getName().equals(edtTopicName.getText().toString()))
            {
                Toast.makeText(this, "Lĩnh vực " + edtTopicName.getText().toString() + " đã tồn tại.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        topicImageURL = topicImageURL.equals("")? "topic.png" : topicImageURL;
        com.example.quizzyapplication_v2.Offline.Topic newTopic = new com.example.quizzyapplication_v2.Offline.Topic(edtTopicName.getText().toString(), topicImageURL);
        db.insertTopic(newTopic);
        Toast.makeText(this, "Lĩnh vực " + edtTopicName.getText().toString() + " đã được thêm.", Toast.LENGTH_SHORT).show();
        edtTopicName.setText("");
        topicImageURL = "";
        rtloTopic.setVisibility(View.INVISIBLE);
        updateTopicsForQuest();
        Button AddTopicBtn = (Button)findViewById(R.id.themlv);
        AddTopicBtn.setBackgroundResource(R.drawable.insert_button_border);
    }

    public void insertQuestion(View view) {
        Button thisBtn = (Button)view;
        Button otherBtn = (Button)findViewById(R.id.themlv);
        otherBtn.setBackgroundResource(R.drawable.insert_button_border);
        thisBtn.setBackgroundResource(R.drawable.app_button_border_correct);
        rtloQuestion.setVisibility(View.VISIBLE);
        rtloTopic.setVisibility(View.INVISIBLE);
    }

    public boolean checkEmptyString(EditText edtText)
    {
        return edtText.getText().toString().equals("");
    }

    public void addQuestion(View view) {
        //Check for all required information
        if (checkEmptyString(edtQuestContent) || checkEmptyString(edtQuestOption1) || checkEmptyString(edtQuestOption2)
                || checkEmptyString(edtQuestOption3) || checkEmptyString(edtQuestOption4))
        {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<String> difficulties = new ArrayList<String>()
        {{
            add("Dễ");
            add("Trung bình");
            add("Khó");
        }};
        com.example.quizzyapplication_v2.Offline.Topic selectedCategory = (com.example.quizzyapplication_v2.Offline.Topic)spinnerCategory.getSelectedItem();
        com.example.quizzyapplication_v2.Offline.Question newQuest = new com.example.quizzyapplication_v2.Offline.Question(edtQuestContent.getText().toString(), edtQuestOption1.getText().toString(), edtQuestOption2.getText().toString(),
                edtQuestOption3.getText().toString(), edtQuestOption4.getText().toString(), Integer.parseInt(spinnerCorrectOption.getSelectedItem().toString()),
                selectedCategory.getId(), difficulties.indexOf(spinnerDifficulty.getSelectedItem().toString()) + 1, questionImageURL);
        db.insertQuestion(newQuest);
        Toast.makeText(this, "Câu hỏi đã được thêm.", Toast.LENGTH_SHORT).show();
        edtQuestContent.setText("");
        edtQuestOption1.setText("");
        edtQuestOption2.setText("");
        edtQuestOption3.setText("");
        edtQuestOption4.setText("");
        questionImageURL = "";
        imgQuestImage.setVisibility(View.INVISIBLE);
        rtloQuestion.setVisibility(View.INVISIBLE);
        Button AddQuestionBtn = (Button)findViewById(R.id.themch);
        AddQuestionBtn.setBackgroundResource(R.drawable.insert_button_border);
    }
}