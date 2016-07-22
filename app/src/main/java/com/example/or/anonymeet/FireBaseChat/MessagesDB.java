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
    static final int DATABASE_VERSION = 36;
    static final String DATABASE_NAME = "Anonymeet.db";
    static final String TABLE_NAME_CONV = "Conversations";
    static final String UID = "_id";
    static final String USER = "User";
    static final String Gender = "Gender";
    static final String MESSAGE = "Message";
    static final String IS_MINE = "IsMine";

    private static final String CREATE_TABLE_CONV = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME_CONV+" ("+
            UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            USER+" varchar(225), "+
            Gender+" varchar(225));";


    public void insertUser(String user, String gender){
        SQLiteDatabase db = getWritableDatabase();
        boolean f = userExists(user);
        if(!f) {
            ContentValues values = new ContentValues();
            values.put(USER, user);
            Log.i("ddddd", gender);
            values.put(Gender, gender);
            db.insert(TABLE_NAME_CONV, null, values);
        }
            db.execSQL("CREATE TABLE IF NOT EXISTS " + '"' + user + '"' + " (" +
                    UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MESSAGE + " varchar(225), " +
                    IS_MINE + " char(1));");

    }

    public boolean userExists(String user){
        SQLiteDatabase db = getWritableDatabase();
        boolean f = false;
        String[] columns = {USER};
        Cursor cursor = db.query('"' + TABLE_NAME_CONV + '"', columns, null, null, null, null, null);
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            if(cursor.getString(cursor.getColumnIndex(USER)).equals(user)) f = true;
        }

        return f;

    }

    public void insertMessage(String user, String message, boolean isMine){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MESSAGE, message);
        String i;
        if(isMine)i = "t";
        else i = "f";
        values.put(IS_MINE, i);
        try{
            db.insert('"'+user+'"', null, values);
        }
        catch (Exception e){
        }

    }

    public ArrayList<MyMessage> getMessagesOfUser(String user){
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {MESSAGE,IS_MINE};
        Cursor cursor = db.query('"'+user+'"', columns, null, null, null, null, null);
        ArrayList<MyMessage> list = new ArrayList<>();
        boolean f;
        String m;
        Log.d("hiiiiiiiiiiiiiiii", "cursor lenght: " + cursor.getCount());
        String i;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            m = cursor.getString(cursor.getColumnIndex(MESSAGE));
            i = cursor.getString(cursor.getColumnIndex(IS_MINE));
            if(i.equals("t")) f = true;
            else f = false;
            MyMessage message = new MyMessage(m, f);
            list.add(message);
        }

        return list;
    }





    public MessagesDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(CREATE_TABLE_CONV);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONV);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String[] columns = {USER};
        Cursor cursor = db.query('"' + TABLE_NAME_CONV + '"', columns, null, null, null, null, null);
        String user;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){

            user = cursor.getString(cursor.getColumnIndex(USER));
            db.execSQL("DROP TABLE IF EXISTS "+'"'+user+'"');

        }
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_CONV);


        onCreate(db);
    }

    public void deleteAll(){
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {USER};
        Cursor cursor = db.query('"' + TABLE_NAME_CONV + '"', columns, null, null, null, null, null);
        String user;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){

            user = cursor.getString(cursor.getColumnIndex(USER));
            db.execSQL("DROP TABLE IF EXISTS "+'"'+user+'"');

        }
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_CONV);


        onCreate(db);
    }
}
