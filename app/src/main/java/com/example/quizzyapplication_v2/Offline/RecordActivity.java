package com.example.quizzyapplication_v2.Offline;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.quizzyapplication_v2.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecordActivity extends AppCompatActivity {
    private Spinner spinnerCategory;
    private TextView txtRecordTopicValue;
    private TextView txtAttemptsCountValue;
    private TextView txtAverageScoreValue;
    private TextView txtAverageRatioValue;
    private TextView txtBestScoreValue;
    private TextView txtBestTrialValue;
    private QuizDbHelper db;
    private ArrayList<com.example.quizzyapplication_v2.Offline.Topic> topics = new ArrayList<com.example.quizzyapplication_v2.Offline.Topic>();
    private RadarChart radarChart;
    private BarChart barChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_record);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        txtRecordTopicValue = findViewById(R.id.txtRecordTopicValue);
        txtAttemptsCountValue = findViewById(R.id.txtAttemptsCountValue);
        txtAverageScoreValue = findViewById(R.id.txtAverageScoreValue);
        txtAverageRatioValue = findViewById(R.id.txtAverageRatioValue);
        txtBestScoreValue = findViewById(R.id.txtBestScoreValue);
        txtBestTrialValue = findViewById(R.id.txtBestTrialValue);
        radarChart = findViewById(R.id.radarChart);
        barChart = findViewById(R.id.barChart);
        db = QuizDbHelper.getInstance(this);
        loadTopics();
    }

    private void loadTopics() {
        topics = (ArrayList<Topic>)db.getAllTopics();
        topics.add(new Topic("Thống kê chung", ""));
        ArrayAdapter<com.example.quizzyapplication_v2.Offline.Topic> adapterCategories = new ArrayAdapter<com.example.quizzyapplication_v2.Offline.Topic>(this,
                android.R.layout.simple_spinner_item, topics);
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

    public void RecordStatistic(View view) {
        com.example.quizzyapplication_v2.Offline.Topic selectedCategory = (com.example.quizzyapplication_v2.Offline.Topic)spinnerCategory.getSelectedItem();
        txtRecordTopicValue.setText(selectedCategory.getName() == "Thống kê chung"? "" : selectedCategory.getName());
        ArrayList<Record> recordsList = new ArrayList<Record>();
        recordsList = db.getRecords(selectedCategory.getName() == "Thống kê chung"? -1 : selectedCategory.getId());
        int attemptCount = recordsList.size();
        int totalPoint = 0;
        int totalQuest = 0;
        double ratioSum = 0;
        double maxRatio = 0;
        int maxPoint = 0;
        for (Record record : recordsList)
        {
            totalPoint += record.getPoint();
            if (record.getPoint() > maxPoint)
                maxPoint = record.getPoint();
            ratioSum += record.getRatio();
            if (record.getRatio() > maxRatio)
                maxRatio = record.getRatio();
            totalQuest += record.getTotalQuest();
        }
        txtAttemptsCountValue.setText(Integer.toString(attemptCount));
        txtAverageScoreValue.setText(attemptCount == 0? "0" : String.format("%.2f", (double)totalPoint / attemptCount));
        txtAverageRatioValue.setText(attemptCount == 0? "0" : String.format("%.2f %%", (double)100 * ratioSum / attemptCount));
        txtBestScoreValue.setText(Integer.toString(maxPoint));
        txtBestTrialValue.setText(String.format("%.2f %%", maxRatio * 100));

        //Update barchart
        if (selectedCategory.getName().toString().equals("Thống kê chung")) {
            updateRadarChart();
        }
        else
        {
            updateBarChart(selectedCategory.getId());
        }
    }

    private void updateBarChart(int topicID) {
        radarChart.setVisibility(View.INVISIBLE);
        barChart.setVisibility(View.VISIBLE);
        ArrayList<BarEntry> topicInfos = new ArrayList<BarEntry>();
        ArrayList<LegendEntry> legendEntries = new ArrayList<LegendEntry>();

        ArrayList<Record> tmpRecordList = new ArrayList<Record>();
        tmpRecordList = db.getRecords(topicID);
        if (tmpRecordList.size() == 0)
        {
            barChart.setVisibility(View.INVISIBLE);
            return;
        }
        else if (tmpRecordList.size() <= 5)
        {
            for (int i = 0; i < tmpRecordList.size(); i++)
            {
                topicInfos.add(new BarEntry(i, 100 * (float)tmpRecordList.get(i).getRatio()));
            }
        }
        else
        {
            for (int i = tmpRecordList.size() - 5; i < tmpRecordList.size(); i++)
            {
                topicInfos.add(new BarEntry(i, 100 * (float)tmpRecordList.get(i).getRatio()));
            }
        }

        BarDataSet barDataSet = new BarDataSet(topicInfos, "Tỉ lệ trả lời đúng (%)");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        barChart.getXAxis().setDrawLabels(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getAxisLeft().setAxisMaximum(100);
        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.invalidate();

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Thống kê các trận đấu gần nhất");
        barChart.animateY(1000);
    }

    private void updateRadarChart()
    {
        radarChart.setVisibility(View.VISIBLE);
        barChart.setVisibility(View.INVISIBLE);
        ArrayList<RadarEntry> topicInfos = new ArrayList<RadarEntry>();
        ArrayList<String> topicNames = new ArrayList<String>();
        for (int i = 0; i < topics.size(); i++) {
            if (!topics.get(i).getName().equals("Thống kê chung")) {
                ArrayList<Record> tmpRecordList = new ArrayList<Record>();
                tmpRecordList = db.getRecords(topics.get(i).getId());
                float tmpRatioAverage = 0;
                if (tmpRecordList.size() != 0) {
                    for (Record record : tmpRecordList) {
                        tmpRatioAverage += record.getRatio();
                    }
                    tmpRatioAverage /= tmpRecordList.size();
                    tmpRatioAverage *= 100; //In percentage
                }
                topicInfos.add(new RadarEntry(tmpRatioAverage));
                topicNames.add(topics.get(i).getName());
            }
        }

        RadarDataSet radarDataSet = new RadarDataSet(topicInfos, "Tỉ lệ trả lời đúng trung bình các lĩnh vực (%)");
        radarDataSet.setColor(Color.CYAN);
        radarDataSet.setLineWidth(2f);
        radarDataSet.setValueTextColor(Color.BLACK);
        radarDataSet.setValueTextSize(14f);
        radarDataSet.setFillAlpha(180);
        radarDataSet.setDrawFilled(true);

        RadarData radarData = new RadarData();
        radarData.addDataSet(radarDataSet);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(topicNames));
        xAxis.setAxisMaximum(90);
        xAxis.setAxisMinimum(0);
        YAxis yAxis = radarChart.getYAxis();
        yAxis.setAxisMaximum(90);
        yAxis.setAxisMinimum(0);
        radarChart.invalidate();
        radarChart.getDescription().setText("");
        radarChart.setData(radarData);
    }
}