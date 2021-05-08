package com.example.quizzyapplication_v2.Online;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.quizzyapplication_v2.R;

public class LevelUpFragment extends Fragment {
    private int newLevel;
    public LevelUpFragment(int nl) {
        newLevel = nl;
    }

    public static LevelUpFragment newInstance(int nl) {
        LevelUpFragment fragment = new LevelUpFragment(nl);
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
                inflater.inflate(R.layout.fragment_level_up, container, false);

        TextView txtLevelUp = rootView.findViewById(R.id.txtLevelUp);
        txtLevelUp.setText("Level " + String.valueOf(newLevel));

        return rootView;
    }
}