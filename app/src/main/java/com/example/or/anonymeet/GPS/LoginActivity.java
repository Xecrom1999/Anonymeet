package com.example.or.anonymeet.GPS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.or.anonymeet.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    Firebase users;
    SharedPreferences preferences;
    EditText nicknameInput;
    Toolbar toolbar;
    RadioButton female_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        initializeViews();

        setSupportActionBar(toolbar);

        toolbar.setTitle("Getting Started");

        users = new Firebase("https://anonymeetapp.firebaseio.com/Users");

        preferences = getSharedPreferences("data", MODE_PRIVATE);

    }

    public void initializeViews() {
        toolbar = (Toolbar) findViewById(R.id.toolBar1);
        nicknameInput = (EditText) findViewById(R.id.nickname);
        female_button = (RadioButton) findViewById(R.id.female_button);
    }

    public void login(View view) {

        final String nickname = nicknameInput.getText().toString();


        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean exists = dataSnapshot.hasChild(nickname);

                if (exists) nicknameInput.setError("Nickname already exists.");

                else if (nickname.length() < 4) nicknameInput.setError("Nickname too short.");

                else if (userNameExists(nickname)) nicknameInput.setError("Nickname already exists.");

                else {

                    String gender = "male";
                    if (female_button.isChecked()) gender = "female";

                    users.child(nickname).setValue(gender);
                    preferences.edit().putString("nickname", nickname).putString("gender", gender).commit();
                    startActivity(new Intent(getApplicationContext(), FindPeopleActivity.class));
                    finish();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });



    }

    private boolean userNameExists(String nickname) {

        return false;
    }
}