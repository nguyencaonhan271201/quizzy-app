package com.example.quizzyapplication_v2.Online;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quizzyapplication_v2.R;
import com.example.quizzyapplication_v2.SquareImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PreGamePowerAdapter extends ArrayAdapter<Power> {
    private Context context;
    private int layoutID;
    private ArrayList<Power> powers;
    public PreGamePowerAdapter(@NonNull Context context,
                             int resource,
                             @NonNull List<Power> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutID = resource;
        this.powers = (ArrayList<Power>)objects;
    }

    @Override
    public int getCount() {
        return powers.size();
    }

    TextView Level1;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(layoutID, null, false);
        }

        Power p = powers.get(position);

        SquareImageView powerIcon = convertView.findViewById(R.id.imgPowerIcon);
        TextView powerName = convertView.findViewById(R.id.power_name);
        TextView powerQuantity = convertView.findViewById(R.id.power_quantity);

        powerIcon.setImageBitmap(p.getIcon());
        powerName.setText(p.getName());
        powerQuantity.setText(context.getResources().getString(R.string.quantity) + ": " + String.valueOf(p.getQuantity()));

        return convertView;
    }
}