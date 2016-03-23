package com.example.or.anonymeet.FireBaseChat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.example.or.anonymeet.R;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        recyclerView = (RecyclerView)findViewById(R.id.recycle);
        DB myDB = new DB(this);
        db = myDB.getWritableDatabase();
        ArrayList<Contact> contacts = new ArrayList<Contact>();
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
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }
}
