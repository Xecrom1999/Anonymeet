package com.example.or.anonymeet.FireBaseChat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.or.anonymeet.R;

public class MessagesActivity extends AppCompatActivity implements MyListener{

    RecyclerView recyclerView;
    static UsersAdapter usersAdapter;
    SQLiteDatabase db;
    Context context;
    static boolean isActive;
    SharedPreferences preferences;


    @Override
    protected void onPostResume() {
        isActive = true;
        super.onPostResume();
    }

    @Override
    protected void onStop() {
        isActive = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        isActive = false;
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        usersAdapter.syncContacts();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        isActive = true;
        context = this;
        if(getIntent().getBooleanExtra("fromNoti", false)){
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("usernameTo", getIntent().getStringExtra("usernameTo"));
            startActivity(i);
        }
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        recyclerView = (RecyclerView)findViewById(R.id.recycle);
        MessagesDB myDB = new MessagesDB(this);
        db = myDB.getWritableDatabase();
        usersAdapter = new UsersAdapter(this, myDB, this);
        recyclerView.setAdapter(usersAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onItemClick(View view, int position, String name) {
        Intent myintent = new Intent(context, ChatActivity.class).putExtra("usernameTo", usersAdapter.contacts.get(position).name);
        startActivity(myintent);
    }
}
