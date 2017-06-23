package com.Tapp.Anonymeet.FireBaseChat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Or on 02/04/2016.
 */
public class MessagesDB extends SQLiteOpenHelper {
    static final int DATABASE_VERSION = 39;
    static final String DATABASE_NAME = "Anonymeet.db";
    static final String TABLE_NAME_CONV = "Conversations";
    static final String UID = "_id";
    static final String USER = "User";
    static final String Gender = "Gender";
    static final String NOTI = "Notifications";
    static final String MESSAGE = "Message";
    static final String IS_MINE = "IsMine";

    private static final String CREATE_TABLE_CONV = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME_CONV+" ("+
            UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            USER+" varchar(225), "+
            Gender+" varchar(225), " +
            NOTI+" int);";


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


}
