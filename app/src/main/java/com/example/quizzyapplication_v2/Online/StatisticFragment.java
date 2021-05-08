package com.example.quizzyapplication_v2.Online;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.quizzyapplication_v2.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StatisticFragment extends Fragment {

    private RadarChart profileRadarChart;
    private BarChart profileBarChart;
    private ArrayList<TextView> statisticValues;
    private ArrayList<com.example.quizzyapplication_v2.Online.Topic> allTopics;
    private int getPosition2;
    private ArrayList<Statistic> getResult;

    public StatisticFragment(int getPosition2) {
        allTopics = Database.getInstance().getTopicsList();
        this.getPosition2 = getPosition2;
    }

    public static StatisticFragment newInstance(int getPosition2) {
        StatisticFragment fragment = new StatisticFragment(getPosition2);
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
                inflater.inflate(R.layout.fragment_statistic, container, false);

        profileBarChart = rootView.findViewById(R.id.profileBarChart);
        profileRadarChart = rootView.findViewById(R.id.profileRadarChart);
        statisticValues = new ArrayList<TextView>();
        statisticValues.add((TextView)rootView.findViewById(R.id.txtValue1));
        statisticValues.add((TextView)rootView.findViewById(R.id.txtValue2));
        statisticValues.add((TextView)rootView.findViewById(R.id.txtValue3));
        statisticValues.add((TextView)rootView.findViewById(R.id.txtValue4));
        statisticValues.add((TextView)rootView.findViewById(R.id.txtValue5));
        statisticValues.add((TextView)rootView.findViewById(R.id.txtValue6));
        statisticValues.add((TextView)rootView.findViewById(R.id.txtValue7));
        
        getStatisticResult();

        return rootView;
    }

    private void getStatisticResult() {
        getResult = new ArrayList<Statistic>();
        DatabaseReference tmp = FirebaseDatabase.getInstance().getReference("statistics");
        tmp.orderByChild("username").equalTo(ThisUser.getInstance(null).getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    int topicID = Integer.parseInt(String.valueOf(snapshot1.child("topicID").getValue()));
                    int correctQuest = Integer.parseInt(String.valueOf(snapshot1.child("correctQuest").getValue()));
                    int point = Integer.parseInt(String.valueOf(snapshot1.child("point").getValue()));
                    int status = Integer.parseInt(String.valueOf(snapshot1.child("status").getValue()));
                    int totalQuest = Integer.parseInt(String.valueOf(snapshot1.child("totalQuest").getValue()));
                    if (getPosition2 == allTopics.size() || getPosition2 == topicID - 1)
                    {
                        Statistic get = new Statistic(topicID, ThisUser.getInstance(null).getUsername(), status, point, correctQuest, totalQuest);
                        getResult.add(get);
                    }
                }
                if (getPosition2 == allTopics.size())
                {
                    statisticValues.get(0).setText(getResources().getString(R.string.generalStatistic));
                }
                else
                {
                    statisticValues.get(0).setText(allTopics.get(getPosition2).getName());
                }
                statisticValues.get(1).setText(String.valueOf(getResult.size()));
                double ratioSum = 0;
                int countWin = 0;
                int countTie = 0;
                int countLose = 0;
                double maxRatio = 0;
                for (Statistic statistic : getResult)
                {
                    ratioSum += statistic.getRatio();
                    if (statistic.getRatio() > maxRatio)
                        maxRatio = statistic.getRatio();
                    if (statistic.getStatus() == 0)
                        countWin++;
                    else if (statistic.getStatus() == 1)
                        countTie++;
                    else
                        countLose++;
                }
                statisticValues.get(2).setText(getResult.size() == 0? "0" : String.format("%.2f %%", (double)100 * ratioSum / getResult.size()));
                statisticValues.get(3).setText(String.valueOf(countWin));
                statisticValues.get(4).setText(String.valueOf(countTie));
                statisticValues.get(5).setText(String.valueOf(countLose));
                statisticValues.get(6).setText(String.format("%.2f %%", maxRatio * 100));

                //Update chart
                if (getPosition2 == allTopics.size())
                    updateRadarChart();
                else
                    updateBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateBarChart() {
        profileRadarChart.setVisibility(View.INVISIBLE);
        profileBarChart.setVisibility(View.VISIBLE);
        if (getResult.size() > 0) ((View)getView().findViewById(R.id.profileChartBackground)).setVisibility(View.VISIBLE);
        else ((View)getView().findViewById(R.id.profileChartBackground)).setVisibility(View.INVISIBLE);
        ArrayList<BarEntry> topicInfos = new ArrayList<BarEntry>();
        ArrayList<LegendEntry> legendEntries = new ArrayList<LegendEntry>();

        if (getResult.size() == 0)
        {
            profileBarChart.setVisibility(View.INVISIBLE);
            return;
        }
        else if (getResult.size() <= 5)
        {
            for (int i = 0; i < getResult.size(); i++)
            {
                topicInfos.add(new BarEntry(i, 100 * (float)getResult.get(i).getRatio()));
            }
        }
        else
        {
            for (int i = getResult.size() - 5; i < getResult.size(); i++)
            {
                topicInfos.add(new BarEntry(i, 100 * (float)getResult.get(i).getRatio()));
            }
        }

        BarDataSet barDataSet = new BarDataSet(topicInfos, getResources().getString(R.string.barChartLabel1));
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(R.color.colorPrimaryDark);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        profileBarChart.getXAxis().setDrawLabels(false);
        profileBarChart.getAxisRight().setDrawLabels(false);
        profileBarChart.getAxisLeft().setAxisMaximum(100);
        profileBarChart.getAxisLeft().setAxisMinimum(0);
        profileBarChart.invalidate();

        profileBarChart.setFitBars(true);
        profileBarChart.setData(barData);
        profileBarChart.getDescription().setText(getResources().getString(R.string.barChartLabel2));
        profileBarChart.animateY(1000);
    }

    private ArrayList<Statistic> getRecords(int topicID)
    {
        ArrayList<Statistic> result = new ArrayList<Statistic>();
        for (Statistic statistic : getResult)
        {
            if (statistic.getTopicID() == topicID)
                result.add(statistic);
        }
        return result;
    }

    private void updateRadarChart()
    {
        ((View)getView().findViewById(R.id.profileChartBackground)).setVisibility(View.VISIBLE);
        profileRadarChart.setVisibility(View.VISIBLE);
        profileBarChart.setVisibility(View.INVISIBLE);
        ArrayList<RadarEntry> topicInfos = new ArrayList<RadarEntry>();
        ArrayList<String> topicNames = new ArrayList<String>();
        for (int i = 0; i < allTopics.size(); i++) {
            ArrayList<Statistic> tmpRecordList = new ArrayList<Statistic>();
            tmpRecordList = getRecords(allTopics.get(i).getId());
            float tmpRatioAverage = 0;
            if (tmpRecordList.size() != 0) {
                for (Statistic statistic : tmpRecordList) {
                    tmpRatioAverage += statistic.getRatio();
                }
                tmpRatioAverage /= tmpRecordList.size();
                tmpRatioAverage *= 100; //In percentage
            }
            topicInfos.add(new RadarEntry(tmpRatioAverage));
            topicNames.add(allTopics.get(i).getName());
        }

        RadarDataSet radarDataSet = new RadarDataSet(topicInfos, getResources().getString(R.string.radarChartLabel));
        radarDataSet.setColor(R.color.colorPrimary);
        radarDataSet.setLineWidth(2f);
        radarDataSet.setValueTextColor(R.color.colorPrimaryDark);
        radarDataSet.setValueTextSize(14f);
        radarDataSet.setFillAlpha(180);
        radarDataSet.setDrawFilled(true);

        RadarData radarData = new RadarData();
        radarData.addDataSet(radarDataSet);

        XAxis xAxis = profileRadarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(topicNames));
        xAxis.setAxisMaximum(90);
        xAxis.setAxisMinimum(0);
        YAxis yAxis = profileRadarChart.getYAxis();
        yAxis.setAxisMaximum(90);
        yAxis.setAxisMinimum(0);
        profileRadarChart.invalidate();
        profileRadarChart.getDescription().setText("");
        profileRadarChart.setData(radarData);
    }
}