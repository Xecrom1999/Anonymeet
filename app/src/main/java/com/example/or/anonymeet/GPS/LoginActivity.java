package com.example.or.anonymeet.GPS;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.or.anonymeet.R;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements Firebase.AuthResultHandler, Firebase.ResultHandler {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    CheckBox checkBox;
    Firebase users;
    boolean isLoggedIn;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_login);

        users = new Firebase("https://anonymeetapp.firebaseio.com");

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

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

            //users.authWithPassword(email, password, this);
        }
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
        Toast.makeText(getApplicationContext(), "Error: " + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getApplicationContext(), "Error: " + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
        showProgress(false);
    }
}

