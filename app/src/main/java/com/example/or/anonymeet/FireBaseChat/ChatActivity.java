package com.example.or.anonymeet.FireBaseChat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Firebase.setAndroidContext(this);
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        myEmail = preferences.getString("email", "");
        emailWith = getIntent().getStringExtra("username");
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
                try {
                    getMessage.setText(dataSnapshot.getValue().toString());
                }
                catch (Exception e){
                    getMessage.setText("");
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void onClick(View view){
        myFirebaseRef.child(emailWith).child(myEmail).setValue(SendMessage.getText().toString());
    }




}
