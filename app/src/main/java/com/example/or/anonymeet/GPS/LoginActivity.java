package com.example.or.anonymeet.GPS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import com.example.or.anonymeet.R;
import com.firebase.client.Firebase;



public class LoginActivity extends AppCompatActivity {

    Firebase users;
    SharedPreferences preferences;
    EditText nicknameInput;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        initializeViews();

        setSupportActionBar(toolbar);

        toolbar.setTitle("Getting Started");

        users = new Firebase("https://anonymeetapp.firebaseio.com");

        preferences = getSharedPreferences("data", MODE_PRIVATE);

    }

    public void initializeViews() {
        toolbar = (Toolbar) findViewById(R.id.toolBar1);
        nicknameInput = (EditText) findViewById(R.id.nickname);
    }

    public void login(View view) {
        String nickname = nicknameInput.getText().toString();
        if (nickname.length() < 4) nicknameInput.setError("Nickname too short.");

        else if (userNameExists(nickname)) nicknameInput.setError("Nickname already exists.");

        else {
            users.child("Users").child(nickname).setValue(nickname);
            preferences.edit().putString("nickname", nickname).commit();
            startActivity(new Intent(this, FindPeopleActivity.class));
        }
    }

    private boolean userNameExists(String nickname) {
        return false;
    }
}