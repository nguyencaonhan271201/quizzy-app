package com.example.quizzyapplication_v2.Offline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.quizzyapplication_v2.BuildConfig;
import com.example.quizzyapplication_v2.MainActivity;
import com.example.quizzyapplication_v2.R;
import com.example.quizzyapplication_v2.SquareImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PostGameActivity extends AppCompatActivity {
    private SquareImageView TopicImage;
    private TextView txtPoint;
    private TextView txtCorrectQuest;
    private TextView txtRatio;
    private TextView txtChooseTopic;
    private View screenShotView;
    private QuizDbHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_post_game);
        txtPoint = findViewById(R.id.txtPointPost);
        txtCorrectQuest = findViewById(R.id.txtCorrectQuestPost);
        txtRatio = findViewById(R.id.txtRatioPost);
        txtChooseTopic = findViewById(R.id.txtChooseTopic);
        TopicImage = findViewById(R.id.topicImage);

        Intent recordIntent = getIntent();
        Record newRecord = new Record(recordIntent.getIntExtra("topicID", 0), recordIntent.getIntExtra("point", 0),
                recordIntent.getIntExtra("correctQuest", 0), recordIntent.getIntExtra("totalQuest", 0));
        db = QuizDbHelper.getInstance(this);
        db.insertRecord(newRecord);

        String imageLink = recordIntent.getStringExtra("topicImage");
        txtPoint.setText(Integer.toString(recordIntent.getIntExtra("point", 0)));
        txtCorrectQuest.setText("Số câu đúng: " + Integer.toString(recordIntent.getIntExtra("correctQuest", 0))
                + "/" + Integer.toString(recordIntent.getIntExtra("totalQuest", 0)));
        txtRatio.setText("Tỉ lệ trả lời đúng: "
                + String.format("%.2f%%", (double)100 * recordIntent.getIntExtra("correctQuest", 0)
                / recordIntent.getIntExtra("totalQuest", 0)));
        screenShotView = getWindow().getDecorView();

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
                txtChooseTopic.setText(recordIntent.getStringExtra("topicName"));
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

    public void BackToStart(View view) {
        Intent backIntent = new Intent(this, ChooseActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(backIntent);
        finish();
    }

    public void Share(View view) {
        takeScreenshotAndShare(this, screenShotView);
    }

    public static void takeScreenshotAndShare(final Context context, View view)
    {
        try
        {
            File mPath = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "screenshot.png");

            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            FileOutputStream outStream = new FileOutputStream(mPath);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outStream);
            outStream.flush();
            outStream.close();

            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            //Uri pictureUri = Uri.fromFile(mPath);
            Uri pictureUri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    mPath);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(shareIntent, "Share kết quả"));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void xemRecord(View view) {
        Intent recordChoose = new Intent(this, RecordActivity.class);
        startActivity(recordChoose);
    }
}