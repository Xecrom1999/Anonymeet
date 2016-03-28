package com.example.or.anonymeet.FireBaseChat;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.or.anonymeet.R;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    SQLiteDatabase db;
    Context context;
    SharedPreferences preferences;
    android.support.v4.app.FragmentTransaction transaction;
    android.support.v4.app.Fragment f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        context = this;
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        recyclerView = (RecyclerView)findViewById(R.id.recycle);
        DB myDB = new DB(this);
        db = myDB.getWritableDatabase();
        recyclerAdapter = new RecyclerAdapter(this, myDB);
        recyclerAdapter.SetOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String name) {
                Intent myintent = new Intent(context, ChatActivity.class).putExtra("username", recyclerAdapter.contacts.get(position).name);
                startActivity(myintent);

            }

        });
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        f = new AddChatFragment(recyclerAdapter);





    }

    public void addChat(View view){

            f = new AddChatFragment(recyclerAdapter);
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frame, f);
            transaction.show(f);
            transaction.commit();

    }
}
