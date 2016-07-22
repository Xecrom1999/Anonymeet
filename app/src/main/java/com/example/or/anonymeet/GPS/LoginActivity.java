package com.example.or.anonymeet.GPS;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.or.anonymeet.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class LoginActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    Firebase users;
    SharedPreferences preferences;
    EditText nicknameInput;
    EditText passwordInput;
    Toolbar toolbar;
    RadioButton female_button;
    CheckBox checkBox;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        preferences = getSharedPreferences("data", MODE_PRIVATE);

        if(!preferences.getString("nickname", "").equals("")){
            startActivity(new Intent(getApplicationContext(), FindPeopleActivity.class));
            finish();
        }

        initializeViews();

        setSupportActionBar(toolbar);

        toolbar.setTitle("Getting Started");

        users = new Firebase("https://anonymeetapp.firebaseio.com/Users");

        checkBox.setOnCheckedChangeListener(this);

        radioGroup.setVisibility(View.GONE);
    }

    public void initializeViews() {
        toolbar = (Toolbar) findViewById(R.id.toolBar1);
        nicknameInput = (EditText) findViewById(R.id.nickname);
        passwordInput = (EditText) findViewById(R.id.password);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        female_button = (RadioButton) findViewById(R.id.female_button);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
    }

    public void attemptLogin(View view) {

        final String nickname = nicknameInput.getText().toString();
        final String password = passwordInput.getText().toString();

        final boolean newUser = checkBox.isChecked();

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean exists = dataSnapshot.hasChild(nickname);

                String gender = "male";
                if (female_button.isChecked()) gender = "female";

                if (newUser) {

                    if (nickname.isEmpty()) nicknameInput.setError("Please enter a nickname.");

                    if (nickname.length() < 3) nicknameInput.setError("Nickname too short.");

                    else if (exists) nicknameInput.setError("Nickname already exists.");

                    else if (password.isEmpty()) passwordInput.setError("Please enter a password.");

                    else if (password.length() < 5) passwordInput.setError("Password too short.");

                    else {
                        users.child(nickname).child("gender").setValue(gender);
                        users.child(nickname).child("password").setValue(password);

                        login(nickname, gender);
                    }
                }

                else {
                    if (!exists) nicknameInput.setError("Nickname not exists.");

                    else if (!dataSnapshot.child(nickname).child("password").getValue().toString().equals(password))
                        passwordInput.setError("Password incorrect");

                    else {
                        gender = dataSnapshot.child(nickname).getValue().toString();
                        login(nickname, gender);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    private void login(String nickname, String gender) {
        preferences.edit().putString("nickname", nickname).putString("gender", gender).commit();
        startActivity(new Intent(getApplicationContext(), FindPeopleActivity.class));
        finish();
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        radioGroup.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }
}