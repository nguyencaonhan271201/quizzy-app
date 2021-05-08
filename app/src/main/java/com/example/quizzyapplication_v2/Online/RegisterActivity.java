package com.example.quizzyapplication_v2.Online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quizzyapplication_v2.MainActivity;
import com.example.quizzyapplication_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtUsername, txtPassword, txtEmail;
    private Button btnRegister;
    private ProgressDialog progressDialog;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);

        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        txtEmail = findViewById(R.id.txtEmail);
        btnRegister = findViewById(R.id.btnRegister);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        database = FirebaseDatabase.getInstance();
    }

    private void registerUser(final String username, final String password, final String email) {
        progressDialog.show();
        DatabaseReference myRef = database.getReference("users").child(username);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Toast.makeText(getApplicationContext(), "User existed", Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                }
                else
                {
                    DatabaseReference myRef = database.getReference("users/" + username);
                    User tmpUser = new User(username, email, 0, 1
                            ,"https://firebasestorage.googleapis.com/v0/b/testmultiplayerfirebase.appspot.com/o/profiles_images%2Fdefault_ava.jpg?alt=media&token=f3f35e5b-581e-4cef-bb8b-c8a5fa666921");
                    myRef.setValue(tmpUser);
                    myRef = database.getReference("users/" + username + "/password");
                    String hashedPass = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                    myRef.setValue(hashedPass);
                    myRef = database.getReference("users/" + username + "/isLoggedIn");
                    myRef.setValue(0);
                    myRef = database.getReference("users/" + username + "/coin");
                    myRef.setValue(200);
                    for (int i = 0; i < 8; i++)
                    {
                        database.getReference("users/" + username + "/powers/power" + String.valueOf(i)).setValue(0);
                    }
                    progressDialog.hide();
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.welcomeString1) + " " + username
                            + getResources().getString(R.string.welcomeString2), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                    startActivity(intent);
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

    public void Register(View view) {
        String username = txtUsername.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            txtEmail.setError("Invalid Email");
            txtEmail.setFocusable(true);
        }
        else if (password.length() < 6)
        {
            txtPassword.setError("Password is unavailable.");
            txtPassword.setFocusable(true);
        }
        else
        {
            registerUser(username, password, email);
        }
    }
}