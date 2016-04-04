package com.example.or.anonymeet.FireBaseChat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.or.anonymeet.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.Path;
import com.firebase.client.snapshot.IndexedNode;
import com.firebase.client.snapshot.Node;

import java.util.ArrayList;


public class ChatActivity extends AppCompatActivity {

    Firebase myFirebaseRef;
    static String emailWith;
    String myEmail;
    EditText SendMessage;
    SQLiteDatabase db;
    MessagesDB myDB;
    SharedPreferences preferences;
    SharedPreferences.Editor se;
    String lastMessage;
    static boolean active = false;
    Context context;
    static RecyclerView recyclerView;
    static ChatAdapter recyclerAdapter;



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

    public static boolean isActive(){
        return active;
    }
    public static String userWith(){
        return emailWith;
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
        Firebase.setAndroidContext(this);
        context = this;
        emailWith = getIntent().getStringExtra("usernameTo");
        myDB = new MessagesDB(this);
        db = myDB.getWritableDatabase();
        recyclerView = (RecyclerView)findViewById(R.id.chatList);
        recyclerAdapter = new ChatAdapter(this, myDB, emailWith);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        se = preferences.edit();
        lastMessage = preferences.getString("lastMessage", "");
        myEmail = preferences.getString("email", "");
        myEmail = myEmail.substring(0, myEmail.indexOf('.'));
        myFirebaseRef = new Firebase("https://anonymeetapp.firebaseio.com/Chat");
        SendMessage = (EditText)findViewById(R.id.sendMessage);
        recyclerView.getLayoutManager().scrollToPosition(recyclerAdapter.messages.size() - 1);


    }

    public void onClick(View view){
        if(!SendMessage.getText().toString().equals("")) {
            lastMessage = preferences.getString("lastMessage", "");
            myDB.insertMessage(emailWith, SendMessage.getText().toString(), true);
            recyclerAdapter.syncMessages();
            if (SendMessage.getText().toString().equals(lastMessage)) {
                String message = "cbd9b0a2-d183-45ee-9582-27df3020ff65" + SendMessage.getText().toString();
                myFirebaseRef.child(emailWith).child(myEmail).setValue(message);
                se.putString("lastMessage", message).commit();
            } else {
                myFirebaseRef.child(emailWith).child(myEmail).setValue(SendMessage.getText().toString());
                se.putString("lastMessage", SendMessage.getText().toString()).commit();
            }
            SendMessage.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(SendMessage.getWindowToken(), 0);
        }

    }

    public static void scrollDown(){
        recyclerView.getLayoutManager().scrollToPosition(recyclerAdapter.getItemCount()-2);

    }
}
