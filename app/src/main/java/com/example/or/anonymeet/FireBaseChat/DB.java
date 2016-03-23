package com.example.or.anonymeet.FireBaseChat;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Or on 23/03/2016.
 */
public class DB extends SQLiteOpenHelper {
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "Anonymeet.db";
    static final String TABLE_NAME = "Chats";
    static final String UID = "_id";
    static final String USER = "User";
    static final String REF = "Ref";
    private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"("+
            UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            USER+" varchar(225), "+
            REF+" varchar(225);";


    public void insert(String user, String ref){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER, user);
        values.put(REF, ref);
        db.insert(TABLE_NAME, null, values);
    }





    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
}
