package com.example.or.anonymeet.FireBaseChat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.or.anonymeet.R;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class ChatActivity extends AppCompatActivity {

    Firebase myFirebaseRef;
    String emailWith;
    String myEmail;
    EditText SendMessage;
    TextView getMessage;
    SQLiteDatabase db;
    DB myDB;
    SharedPreferences preferences;
    boolean active;
    String lastMessage;

    @Override
    protected void onStart() {
        active = true;
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        active = false;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        active = true;
        super.onResume();
    }

    @Override
    protected void onStop() {
        active = false;
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        lastMessage = "";
        Firebase.setAndroidContext(this);
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        myEmail = preferences.getString("email", "");
        myEmail = myEmail.substring(0, myEmail.indexOf('.'));
        emailWith = getIntent().getStringExtra("username");
        emailWith = emailWith.substring(0, emailWith.indexOf('.'));
        myFirebaseRef = new Firebase("https://anonymeetapp.firebaseio.com/Chat");
        SendMessage = (EditText)findViewById(R.id.sendMessage);
        getMessage = (TextView)findViewById(R.id.getMessage);
        myDB = new DB(this);
        db = myDB.getWritableDatabase();
        String selectQuery = "SELECT * FROM "+myDB.TABLE_NAME+" WHERE "+myDB.USER+"='"+emailWith+"';";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        myFirebaseRef.authWithPassword("bobtony@firebase.com", "correcthorsebatterystaple", new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // there was an error
            }
        });
        myFirebaseRef.child(myEmail).child(emailWith).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getValue().toString().length()>36 && dataSnapshot.getValue().toString().substring(0,35).equals("cbd9b0a2-d183-45ee-9582-27df3020ff65")) getMessage.setText(dataSnapshot.getValue().toString().substring(36,dataSnapshot.getValue().toString().length()-1));
                    else getMessage.setText(dataSnapshot.getValue().toString());


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void onClick(View view){
        if(SendMessage.getText().toString().equals(lastMessage)){
            myFirebaseRef.child(emailWith).child(myEmail).setValue("cbd9b0a2-d183-45ee-9582-27df3020ff65"+SendMessage.getText().toString());
        }
        myFirebaseRef.child(emailWith).child(myEmail).setValue(SendMessage.getText().toString());
        SendMessage.setText("");
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(SendMessage.getWindowToken(), 0);
    }

    public boolean isActive(){
        return active;
    }
    public String getEmailWith(){
        return emailWith;
    }



}
