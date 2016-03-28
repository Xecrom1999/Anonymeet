package com.example.or.anonymeet.GPS;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.or.anonymeet.R;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements Firebase.AuthResultHandler, Firebase.ResultHandler {

    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    CheckBox checkBox;
    Firebase users;
    boolean isLoggedIn;
    SharedPreferences preferences;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_login);

        toolbar = (Toolbar) findViewById(R.id.toolBar1);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Login & Register");

        users = new Firebase("https://anonymeetapp.firebaseio.com");

        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        checkBox = (CheckBox) findViewById(R.id.checkbox);

        preferences = getSharedPreferences("data", MODE_PRIVATE);
        isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            String email = preferences.getString("email", "");
            String password = preferences.getString("password", "");

            mEmailView.setText(email);
            mPasswordView.setText(password);
        }

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    attemptLogin();
                return false;
           }
        });
    }

    private void attemptLogin() {

        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        if (checkBox.isChecked())
            users.createUser(email, password, this);
        else {
            users.authWithPassword(email, password, this);
            showProgress(true);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private void showProgress(final boolean show) {

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void onSuccess() {
        Toast.makeText(getApplicationContext(), "Successfully registered!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(FirebaseError firebaseError) {
        mEmailView.setError(firebaseError.getMessage());
    }

    public void onAuthenticated(final AuthData authData) {

        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();


        Intent intent = new Intent(getApplicationContext(), GPSActivity.class);
        intent.putExtra("userId", authData.getUid());
        startActivity(intent);
        isLoggedIn = true;
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.commit();
        finish();
    }

    @Override
    public void onAuthenticationError(FirebaseError firebaseError) {
        mEmailView.setError(firebaseError.getMessage());
        showProgress(false);
    }
}