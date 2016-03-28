package com.example.or.anonymeet.FireBaseChat;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
    SQLiteDatabase db;
    Context context;
    ArrayList<Contact> contacts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        context = this;
        recyclerView = (RecyclerView)findViewById(R.id.recycle);
        DB myDB = new DB(this);
        db = myDB.getWritableDatabase();
        contacts = new ArrayList<Contact>();
        String[] columns = {myDB.USER};
        Cursor cursor = db.query(myDB.TABLE_NAME, columns, null, null, null, null, null);
        Contact contact;
        String user;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){

            user = cursor.getString(cursor.getColumnIndex(myDB.USER));
            contact = new Contact(user);
            contacts.add(contact);
        }
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(this, contacts, myDB);
        recyclerAdapter.SetOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String name) {
                Intent myintent = new Intent(context, ChatActivity.class).putExtra("username", contacts.get(position).name);
                startActivity(myintent);

            }

        });


        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    public void addChat(View view){
        Fragment f = new AddChatFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(f, "add user");
        transaction.commit();
    }
}
