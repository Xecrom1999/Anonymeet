package com.example.or.anonymeet.FireBaseChat;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.or.anonymeet.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class ChatActivity extends AppCompatActivity {

    static Firebase myFirebaseRef;
    static String userWith;
    static String myNickname;
    EditText SendMessage;
    SQLiteDatabase db;
    MessagesDB myDB;
    ImageView isRead;
    ImageView arrived;
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
        userWith = getIntent().getStringExtra("usernameTo");
        myDB = new MessagesDB(this);
        db = myDB.getWritableDatabase();
        recyclerView = (RecyclerView)findViewById(R.id.chatList);
        recyclerAdapter = new ChatAdapter(this, myDB, userWith);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        se = preferences.edit();
        se.putInt("user " + userWith, 0).commit();
        lastMessage = preferences.getString("lastMessage", "");
        myNickname = preferences.getString("nickname", "");
        myFirebaseRef = new Firebase("https://anonymeetapp.firebaseio.com/Chat");
        SendMessage = (EditText)findViewById(R.id.sendMessage);
        recyclerView.getLayoutManager().scrollToPosition(recyclerAdapter.messages.size() - 1);
        isRead = (ImageView)findViewById(R.id.read);
        arrived = (ImageView)findViewById(R.id.arrived);

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                scrollDown();
            }
        });
        myFirebaseRef.child(myNickname).child(userWith).child("read").setValue("true");
        myFirebaseRef.child(userWith).child(myNickname).child("read").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()!=null){
                    if(dataSnapshot.getValue().toString().equals("true")){
                        isRead.setImageResource(R.drawable.read);
                    }
                    else isRead.setImageResource(R.drawable.unread);

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        myFirebaseRef.child(userWith).child(myNickname).child("arrived").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists() || !getIntent().getBooleanExtra("userWasExisted", true)){
                    arrived.setVisibility(View.INVISIBLE);
                    isRead.setVisibility(View.INVISIBLE);
                } else if(dataSnapshot.exists() && dataSnapshot.getValue().toString().equals("true")){
                    arrived.setVisibility(View.INVISIBLE);
                    isRead.setVisibility(View.VISIBLE);
                }
                else{
                    isRead.setVisibility(View.INVISIBLE);
                    arrived.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



    }

    public void onClick(View view){
        if(!SendMessage.getText().toString().equals("")) {
            String message;
            lastMessage = preferences.getString("lastMessage", "");
            myDB.insertMessage(userWith, SendMessage.getText().toString(), true);
            recyclerAdapter.syncMessages();
            if (SendMessage.getText().toString().equals(lastMessage)) {
                message = "cbd9b0a2-d183-45ee-9582-27df3020ff65" + SendMessage.getText().toString();

            } else {
                message = SendMessage.getText().toString();

            }
            myFirebaseRef.child(userWith).child(myNickname).child("message").setValue(message);
            se.putString("lastMessage", message).commit();
            SendMessage.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            scrollDown();
            myFirebaseRef.child(userWith).child(myNickname).child("read").setValue("false");
            myFirebaseRef.child(userWith).child(myNickname).child("arrived").setValue("false");

        }

    }



    public static void scrollDown(){
        recyclerView.getLayoutManager().scrollToPosition(recyclerAdapter.getItemCount() - 1);
        myFirebaseRef.child(myNickname).child(userWith).child("read").setValue("true");
    }
}
