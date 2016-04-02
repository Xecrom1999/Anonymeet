package com.example.or.anonymeet.FireBaseChat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Or on 02/04/2016.
 */
public class MessagesDB extends SQLiteOpenHelper {
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "Anonymeet_messages.db";
    static final String TABLE_NAME_CONV = "Conversations";
    static final String UID = "_id";
    static final String USER = "User";
    static final String MESSAGE = "Message";

    private static final String CREATE_TABLE_CONV = "CREATE TABLE "+TABLE_NAME_CONV+"("+
            UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            USER+" varchar(225));";
    private static String CREATE_TABLE_MESSAGES;


    public void insertUser(String user){
        SQLiteDatabase db = getWritableDatabase();
        CREATE_TABLE_MESSAGES = "CREATE TABLE "+user+"("+
                UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MESSAGE+" varchar(max));";
        ContentValues values = new ContentValues();
        values.put(USER, user);
        db.insert(TABLE_NAME_CONV, null, values);
    }
    public void insertMessage(String user, String message){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MESSAGE, message);
        try{
            db.insert(user, null, values);
        }
        catch (Exception e){
            Log.d("hiiiiiiiiiiiii", "tryed to insert a message but no such user");
        }

    }

    public ArrayList<String> getMessagesOfUser(String user){
        SQLiteDatabase db = getWritableDatabase();
        String  selectQuery = "SELECT * FROM "+user+";";
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<String> list = new ArrayList<String>();
        String message;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){

            message = cursor.getString(cursor.getColumnIndex(MESSAGE));
            list.add(message);
        }

        return list;
    }





    public MessagesDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONV);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_CONV);
        String  selectQuery = "SELECT * FROM "+TABLE_NAME_CONV+";";
        Cursor cursor = db.rawQuery(selectQuery, null);
        String user;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){

            user = cursor.getString(cursor.getColumnIndex(USER));
            db.execSQL("DROP TABLE IF EXISTS "+user);

        }

        onCreate(db);
    }
}
