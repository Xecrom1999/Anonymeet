package com.Tapp.Anonymeet.GPS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.Tapp.Anonymeet.FireBaseChat.MyService;
import com.Tapp.Anonymeet.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    Firebase users;
    SharedPreferences preferences;
    EditText nicknameInput;
    EditText passwordInput;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        preferences = getSharedPreferences("data", MODE_PRIVATE);

        if(!preferences.getString("nickname", "").equals("")){
            startActivity(new Intent(getApplicationContext(), FindPeopleActivity.class));
            finish();
        }
        else stopService(new Intent(this, MyService.class));

        initializeViews();
        setSupportActionBar(toolbar);

        toolbar.setTitle("Login to Anonymeet");

        users = new Firebase("https://anonymeetapp.firebaseio.com/Users");
    }

    public void initializeViews() {
        toolbar = (Toolbar) findViewById(R.id.toolBar1);
        nicknameInput = (EditText) findViewById(R.id.nickname);
        passwordInput = (EditText) findViewById(R.id.password);
    }

    public void attemptLogin(View view) {

        final String nickname = nicknameInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if (nickname.isEmpty()) nicknameInput.setError("Nickname is empty");
        else if (password.isEmpty()) passwordInput.setError("Password is empty");

        else users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    boolean exists = dataSnapshot.hasChild(nickname);

                    if (!exists)
                        nicknameInput.setError("Nickname not exists.");

                    else if (!dataSnapshot.child(nickname).child("password").getValue().toString().equals(password))
                        passwordInput.setError("Password is incorrect");

                    else {
                        String gender = dataSnapshot.child(nickname).child("gender").getValue().toString();
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

    public void signUp(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        finish();
    }
}