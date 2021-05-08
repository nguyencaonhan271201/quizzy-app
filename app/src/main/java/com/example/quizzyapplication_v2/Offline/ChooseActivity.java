package com.example.quizzyapplication_v2.Offline;

import android.content.Intent;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizzyapplication_v2.R;

import java.util.ArrayList;
import java.util.List;

public class ChooseActivity extends AppCompatActivity {
    private GridView gv_topics;
    private ArrayList<com.example.quizzyapplication_v2.Offline.Topic> topics = new ArrayList<com.example.quizzyapplication_v2.Offline.Topic>();
    private QuizDbHelper db;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_choose);
        db = QuizDbHelper.getInstance(this);
        loadData();
        initComponents();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
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

    private void initComponents() {
        adapter = new MyAdapter(this, R.layout.choose_grid_item, topics);

        gv_topics = this.findViewById(R.id.gv_topics);
        gv_topics.setAdapter(adapter);
        gv_topics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < topics.size() - 1)
                {
                    Topic tp = topics.get(position);
                    ArrayList<com.example.quizzyapplication_v2.Offline.Question> getQuestion = db.getQuestions(tp.getName());
                    if (getQuestion.size() < 1)
                    {
                        Toast.makeText(ChooseActivity.this.getApplicationContext(), "Lĩnh vực này chưa có câu hỏi nào.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        ActivityOptionsCompat activityOptionsCompat =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(ChooseActivity.this,
                                        view, "imageTransition");
                        Intent startPreGame = new Intent(getApplicationContext(), PreGameActivity.class);
                        startPreGame.putExtra("topicID", tp.getId());
                        startPreGame.putExtra("topicImage", tp.getImage());
                        startPreGame.putExtra("topicName", tp.getName());
                        startActivity(startPreGame, activityOptionsCompat.toBundle());
                    }
                }
                else
                {
                    Intent startInsertTopic = new Intent(getApplicationContext(), InsertQuestionActivity.class);
                    startActivity(startInsertTopic);
                }
            }
        });
        registerForContextMenu(gv_topics);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.gv_topics) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_main, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.delete:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                if (info.position + 1 <= 11 || info.position == topics.size() - 1)
                {
                    Toast.makeText(this,
                            "Không thể xoá lĩnh vực này.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    boolean deleteStatus = db.deleteTopic(topics.get(info.position).getName());
                    if (deleteStatus)
                    {
                        loadData();
                        initComponents();
                    }
                    else Toast.makeText(this,
                            "Không thể xoá lĩnh vực " + topics.get(info.position).getName(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void loadData() {
        topics = (ArrayList<Topic>)db.getAllTopics();
    }
}