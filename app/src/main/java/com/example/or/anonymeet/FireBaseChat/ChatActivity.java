package com.example.or.anonymeet.FireBaseChat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.or.anonymeet.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class ChatActivity extends AppCompatActivity {

    Firebase myFirebaseRef;
    String usernameTo;
    String myUsername;
    EditText SendMessage;
    EditText user;
    TextView getMessage;
    SQLiteDatabase db;
    DB myDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Firebase.setAndroidContext(this);
        user = (EditText)findViewById(R.id.user);
        usernameTo= getIntent().getStringExtra("username");
        myFirebaseRef = new Firebase("https://anonymeetapp.firebaseio.com/Chat");
        SendMessage = (EditText)findViewById(R.id.sendMessage);
        getMessage = (TextView)findViewById(R.id.getMessage);
        myDB = new DB(this);
        db = myDB.getWritableDatabase();
        String selectQuery = "SELECT * FROM "+myDB.TABLE_NAME+" WHERE "+myDB.USER+"='"+usernameTo+"';";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        //myFirebaseRef = myFirebaseRef.child(cursor.getString(cursor.getColumnIndex(myDB.REF)));
        myFirebaseRef.child(usernameTo).addValueEventListener(new ValueEventListener() {
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
        myFirebaseRef.child(myUsername).setValue(SendMessage.getText().toString());
    }
    public void setUser(View view){
        myUsername = user.getText().toString();
    }



}
