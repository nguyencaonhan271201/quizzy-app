package com.example.quizzyapplication_v2.Online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizzyapplication_v2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.util.ArrayList;

public class PowerStoreActivity extends AppCompatActivity {
    private TextView txtCoin;
    private ListView lv_power_store_type1;
    private ListView lv_power_store_type2;
    private ArrayList<Power> type1;
    private ArrayList<Power> type2;
    private ArrayList<Power> totalPower;
    private PowerStoreAdapter adapter1;
    private PowerStoreAdapter adapter2;
    private int myCoin;
    private int getTmpPowerID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_power_store);

        txtCoin = findViewById(R.id.txtCoin);
        lv_power_store_type1 = findViewById(R.id.lv_power_store_type1);
        lv_power_store_type2 = findViewById(R.id.lv_power_store_type2);

        type1 = new ArrayList<Power>();
        type2 = new ArrayList<Power>();

        initComponents();

        SpaceNavigationView bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_home_24));
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_add_circle_24));
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_storefront_24));
        bottom_navigation.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_person_24));
        bottom_navigation.changeCurrentItem(2);
        bottom_navigation.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent gameIntent = new Intent(getApplicationContext(), com.example.quizzyapplication_v2.Online.ChooseActivity.class);
                startActivity(gameIntent);
                finish();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex)
                {
                    case 0:
                        Intent feedIntent = new Intent(getApplicationContext(), NewsFeedActivity.class);
                        startActivity(feedIntent);
                        finish();
                        break;
                    case 1:
                        Intent createFeed = new Intent(getApplicationContext(), CreateFeedActivity.class);
                        startActivity(createFeed);
                        finish();
                        break;
                    case 2:
                        break;
                    case 3:
                        Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(profileIntent);
                        finish();
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent feedIntent = new Intent(getApplicationContext(), NewsFeedActivity.class);
        startActivity(feedIntent);
        finish();
    }

    private void initComponents()
    {
        type1.clear();
        type2.clear();

        totalPower = ThisUser.getInstance(this).getPowersList();
        for (int i = 0; i < totalPower.size(); i++)
        {
            if (i < 4)
                type1.add(totalPower.get(i));
            else
                type2.add(totalPower.get(i));
        }

        adapter1 = new PowerStoreAdapter(this, R.layout.power_store_lsv_item, type1);
        adapter2 = new PowerStoreAdapter(this, R.layout.power_store_lsv_item, type2);

        lv_power_store_type1.setAdapter(adapter1);
        lv_power_store_type2.setAdapter(adapter2);

        myCoin = ThisUser.getInstance(this).getCoin();
        txtCoin.setText(String.valueOf(myCoin));

        lv_power_store_type1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                buyPower(position);
            }
        });

        lv_power_store_type2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                buyPower(position + 4);
            }
        });
    }

    private void buyPower(int powerID)
    {
        getTmpPowerID = powerID;
        if (myCoin < totalPower.get(powerID).getPrice())
            Toast.makeText(this, getResources().getString(R.string.notEnoughMoney), Toast.LENGTH_SHORT).show();
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage(getResources().getString(R.string.buyConfirmation) + " " + totalPower.get(powerID).getName() + "?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            myCoin -= totalPower.get(getTmpPowerID).getPrice();
                            int getQuantity = totalPower.get(getTmpPowerID).getQuantity();
                            totalPower.get(getTmpPowerID).setQuantity(getQuantity + 1);
                            ThisUser get = ThisUser.getInstance(getApplicationContext());
                            get.setPowersList(totalPower);
                            get.setCoin(myCoin);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            database.getReference("users/" + get.getUsername() + "/coin").setValue(myCoin);
                            database.getReference("users/" + get.getUsername() + "/powers/power" + String.valueOf(getTmpPowerID)).setValue(getQuantity + 1);
                            initComponents();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no), null)
                    .show();
        }
    }
}