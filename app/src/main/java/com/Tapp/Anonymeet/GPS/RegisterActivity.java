package com.Tapp.Anonymeet.GPS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.Tapp.Anonymeet.FireBaseChat.MyService;
import com.Tapp.Anonymeet.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class RegisterActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    Firebase users;
    SharedPreferences preferences;
    EditText nicknameInput;
    EditText passwordInput;
    Toolbar toolbar;
    RadioButton female_button;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        preferences = getSharedPreferences("data", MODE_PRIVATE);

        if(!preferences.getString("nickname", "").equals("")){
            startActivity(new Intent(getApplicationContext(), FindPeopleActivity.class));
            finish();
        }
        else stopService(new Intent(this, MyService.class));

        initializeViews();
        setSupportActionBar(toolbar);

        toolbar.setTitle("Getting Started");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        users = new Firebase("https://anonymeetapp.firebaseio.com/Users");
        radioGroup.setVisibility(View.VISIBLE);
    }

    public void initializeViews() {
        toolbar = (Toolbar) findViewById(R.id.toolBar1);
        nicknameInput = (EditText) findViewById(R.id.nickname);
        passwordInput = (EditText) findViewById(R.id.password);
        female_button = (RadioButton) findViewById(R.id.female_button);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            LocationListenerService.cancelNotification();
        } catch (NullPointerException e) {
        }
    }

    public void attemptLogin(View view) {

        final String nickname = nicknameInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if (nickname.isEmpty()) nicknameInput.setError("Nickname is empty");
        else if (password.isEmpty()) passwordInput.setError("Password is empty");

        else users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String gender = female_button.isChecked() ? "female" : "male";

                    boolean exists = dataSnapshot.hasChild(nickname);

                    if (exists) nicknameInput.setError("Nickname already exists.");
                    else if (nickname.length() < 3) nicknameInput.setError("Nickname too short.");
                    else if (password.length() < 5) passwordInput.setError("Password too short.");

                    else {
                        users.child(nickname).child("gender").setValue(gender);
                        users.child(nickname).child("password").setValue(password);
                        login(nickname, gender);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
