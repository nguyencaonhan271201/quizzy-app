package com.example.quizzyapplication_v2.Online;

import androidx.annotation.NonNull;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quizzyapplication_v2.R;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    private EditText txtUsername, txtPassword;
    private Button btnLogin;
    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    private MaterialCheckBox cbRemember;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        txtUsername = findViewById(R.id.txtLoginUsername);
        txtPassword = findViewById(R.id.txtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");

        Database.getInstance();

        database = FirebaseDatabase.getInstance();
        cbRemember = findViewById(R.id.cbRemember);
    }

    private void loggingIn(final String username, final String password)
    {
        progressDialog.show();
        DatabaseReference myRef = database.getReference("users").child(username);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    DatabaseReference myRef = database.getReference("users/" + username + "/password");
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String passwordGet = snapshot.getValue(String.class);
                            BCrypt.Result checkPass = BCrypt.verifyer().verify(password.toCharArray(), passwordGet.toCharArray());
                            if (!checkPass.verified)
                            {
                                Toast.makeText(getApplicationContext(), "Tên đăng nhập hoặc mật khẩu không đúng.", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                DatabaseReference myRef1 = database.getReference("users/" + username + "/isLoggedIn");
                                myRef1.setValue(1);
                                updateUserInfo(username);
                                if (cbRemember.isChecked())
                                {
                                    SharedPreferences loginInfo = getSharedPreferences("loginUsername", MODE_PRIVATE);
                                    loginInfo.edit().putString("username", username).apply();
                                    loginInfo.edit().putString("password", password).apply();
                                }
                                Intent intent = new Intent(getApplicationContext(), NewsFeedActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            progressDialog.hide();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    progressDialog.hide();
                }
                else
                {
                    progressDialog.hide();
                    Toast.makeText(getApplicationContext(), "Tên đăng nhập hoặc mật khẩu không đúng.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.hide();
            }
        });
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

    private void updateUserInfo(String username) {
        DatabaseReference myRef = database.getReference("users").child(username);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ThisUser instance = ThisUser.getInstance(getApplicationContext());
                instance.setUsername((String)snapshot.getKey());
                instance.setEmail((String)snapshot.child("email").getValue());
                instance.setProfileImage((String)snapshot.child("profileImage").getValue());
                instance.setLevel(Integer.parseInt(String.valueOf(snapshot.child("level").getValue())));
                instance.setExp(Integer.parseInt(String.valueOf(snapshot.child("exp").getValue())));
                instance.setCoin(Integer.parseInt(String.valueOf(snapshot.child("coin").getValue())));
                ArrayList<com.example.quizzyapplication_v2.Online.Topic> favoriteTopics = new ArrayList<Topic>();
                if (snapshot.hasChild("favTopics"))
                {
                    for (DataSnapshot shot : snapshot.child("favTopics").getChildren())
                    {
                        com.example.quizzyapplication_v2.Online.Topic tmp = new com.example.quizzyapplication_v2.Online.Topic();
                        int getTopicID = Integer.parseInt(String.valueOf(shot.getValue()));
                        ArrayList<com.example.quizzyapplication_v2.Online.Topic> totalTopics = Database.getInstance().getTopicsList();
                        for (int i = 0; i < totalTopics.size(); i++)
                        {
                            if (totalTopics.get(i).getId() == getTopicID)
                            {
                                favoriteTopics.add(totalTopics.get(i));
                                break;
                            }
                        }
                    }
                }
                instance.setFavoriteTopics(favoriteTopics);
                ArrayList<Power> userPowersList = instance.getPowersList();
                int[] powerIcons = {R.drawable.power0, R.drawable.power1, R.drawable.power2, R.drawable.power3,
                                    R.drawable.power6, R.drawable.power5, R.drawable.power4, R.drawable.power7};
                int[] powerNames = {R.string.power0name, R.string.power1name, R.string.power2name, R.string.power3name,
                                    R.string.power4name, R.string.power5name, R.string.power6name, R.string.power7name};
                int[] powerDescriptions = {R.string.power0des, R.string.power1des, R.string.power2des, R.string.power3des,
                                           R.string.power4des, R.string.power5des, R.string.power6des, R.string.power7des};
                for (int i = 0; i < 8; i++)
                {
                    userPowersList.get(i).setQuantity(Integer.parseInt(String.valueOf(snapshot.child("powers").child("power" + String.valueOf(i)).getValue())));
                    userPowersList.get(i).setIcon(BitmapFactory.decodeResource(getResources(), powerIcons[i]));
                    userPowersList.get(i).setName(getResources().getString(powerNames[i]));
                    userPowersList.get(i).setDescription(getResources().getString(powerDescriptions[i]));
                }
                instance.setPowersList(userPowersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void Login(View view) {
        String username = txtUsername.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        if (password.length() < 6)
        {
            txtPassword.setError("Password is unavailable.");
            txtPassword.setFocusable(true);
        }
        else
        {
            loggingIn(username, password);
        }

    }
}