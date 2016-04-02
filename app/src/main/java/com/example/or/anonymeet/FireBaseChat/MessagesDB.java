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
    static final int DATABASE_VERSION = 15;
    static final String DATABASE_NAME = "Anonymeet.db";
    static final String TABLE_NAME_CONV = "Conversations";
    static final String UID = "_id";
    static final String USER = "User";
    static final String MESSAGE = "Message";
    static final String IS_MINE = "IsMine";

    private static final String CREATE_TABLE_CONV = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME_CONV+" ("+
            UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            USER+" varchar(225));";


    public void insertUser(String user){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER, user);
        db.insert(TABLE_NAME_CONV, null, values);
        db.execSQL("CREATE TABLE IF NOT EXISTS "+'"'+user+'"'+" ("+
                UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MESSAGE+" varchar(225), " +
                IS_MINE+" char(1));");
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
            Log.d("hiiiiiiiiiiiii", "tryed to insert a message but no such user");
        }

    }

    public ArrayList<MyMessage> getMessagesOfUser(String user){
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {MESSAGE,IS_MINE};
        Cursor cursor = db.query('"'+user+'"', columns, null, null, null, null, null);
        ArrayList<MyMessage> list = new ArrayList<MyMessage>();
        MyMessage message = new MyMessage();
        String i;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            message.message = cursor.getString(cursor.getColumnIndex(MESSAGE));
            i = cursor.getString(cursor.getColumnIndex(IS_MINE));
            if(i.equals("t")) message.isMine = true;
            else message.isMine = false;
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
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_CONV);
        try{
        String  selectQuery = "SELECT * FROM "+TABLE_NAME_CONV+";";
        Cursor cursor = db.rawQuery(selectQuery, null);
        String user;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){

            user = cursor.getString(cursor.getColumnIndex(USER));
            db.execSQL("DROP TABLE IF EXISTS "+user);

        } }
        catch (Exception e){}

        onCreate(db);
    }
}
