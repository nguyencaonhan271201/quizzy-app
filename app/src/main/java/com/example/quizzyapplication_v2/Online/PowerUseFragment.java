package com.example.quizzyapplication_v2.Online;

import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.quizzyapplication_v2.R;

public class PowerUseFragment extends Fragment {

    private Power powerUsed;
    private String attacker;
    private String attackedPlayer;

    public PowerUseFragment(Power power, String attacker, String attacked) {
        this.powerUsed = power;
        this.attacker = attacker;
        this.attackedPlayer = attacked;
    }

    public static PowerUseFragment newInstance(Power power, String attacker, String attacked) {
        PowerUseFragment fragment = new PowerUseFragment(power, attacker, attacked);
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
                inflater.inflate(R.layout.fragment_power_use, container, false);
        ImageView imgPowerUse = rootView.findViewById(R.id.imgPowerUse);
        switch (powerUsed.getId())
        {
            case 4:
                imgPowerUse.setImageResource(R.drawable.powerbanner3);
                break;
            case 5:
                imgPowerUse.setImageResource(R.drawable.powerbanner2);
                break;
            case 6:
                imgPowerUse.setImageResource(R.drawable.powerbanner1);
                break;
            case 7:
                imgPowerUse.setImageResource(R.drawable.powerbanner4);
                break;
        }
        return rootView;
    }
}