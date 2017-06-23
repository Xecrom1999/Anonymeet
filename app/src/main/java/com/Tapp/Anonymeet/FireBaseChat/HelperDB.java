package com.Tapp.Anonymeet.FireBaseChat;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Or on 23/07/2016.
 */
public class HelperDB {

    MessagesDB d;
    SQLiteDatabase db;
    SharedPreferences preferences;
    SharedPreferences.Editor se;

    public HelperDB(Context c){
        d = new MessagesDB(c);
        db = d.getWritableDatabase();
        preferences = c.getSharedPreferences("data", c.MODE_PRIVATE);
        se = preferences.edit();
    }

    public void insertUser(String user, String gender, int noti){
        boolean f = userExists(user);
        if(!f) {
            ContentValues values = new ContentValues();
            values.put(d.USER, user);
            values.put(d.NOTI, noti);
            values.put(d.Gender, gender);
            db.insert(d.TABLE_NAME_CONV, null, values);
        }
        db.execSQL("CREATE TABLE IF NOT EXISTS " + '"' + user + '"' + " (" +
                d.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                d.MESSAGE + " varchar(225), " +
                d.IS_MINE + " char(1));");
    }

    public String getUserLastMessage(String user){
        String lastMessage = "";
        String[] columns = {d.MESSAGE,d.IS_MINE};
        Cursor cursor = db.query('"'+user+'"', columns, null, null, null, null, null);
        cursor.moveToFirst();
        boolean f = false;
        while(!f && cursor.isAfterLast()){
            String s1 = cursor.getString(cursor.getColumnIndex(d.IS_MINE));
            if(s1.equals("f")){
                lastMessage = cursor.getString(cursor.getColumnIndex(d.MESSAGE));
                f = true;
            }
            cursor.moveToNext();
        }
        return lastMessage;
    }

    public boolean userExists(String user){
        boolean f = false;
        String[] columns = {d.USER};
        Cursor cursor = db.query('"' + d.TABLE_NAME_CONV + '"', columns, null, null, null, null, null);
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            if(cursor.getString(cursor.getColumnIndex(d.USER)).equals(user)) f = true;
        }

        return f;
    }

    public void insertMessage(String user, String message, boolean isMine){
        ContentValues values = new ContentValues();
        values.put(d.MESSAGE, message);
        String i;
        if(isMine)i = "t";
        else i = "f";
        values.put(d.IS_MINE, i);
        try{
            db.insert('"'+user+'"', null, values);
        }
        catch (Exception e){
        }
    }


    public void insertNoti(String user){
        se.putInt("user " + user, 1 + preferences.getInt("user " + user, 0)).commit();
        int num = preferences.getInt("user " + user, 0);
        String query="UPDATE " + d.TABLE_NAME_CONV + " SET " + d.NOTI + "=" + num + " WHERE " + d.USER + "='" + user + "'";
        db.execSQL(query);

    }

    public void deleteNotis(String user){
        se.putInt("user " + user, 0).commit();
        int num = 0;
        String query="UPDATE " + d.TABLE_NAME_CONV + " SET " + d.NOTI + "=" + num + " WHERE " + d.USER + "='" + user + "'";
        db.execSQL(query);
    }

    public int getSumOfNotis(){
        String[] columns = {d.NOTI};
        Cursor cursor = db.query('"'+d.TABLE_NAME_CONV+'"', columns, null, null, null, null, null);
        int num = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            Log.i("hiiiiiiiiiiii", "num: " + cursor.getInt(cursor.getColumnIndex(d.NOTI)));
            num += cursor.getInt(cursor.getColumnIndex(d.NOTI));
        }

        return  num;
    }

    public int getContactNumOfNotis(String user){
        String[] columns = {d.USER, d.NOTI};
        Cursor cursor = db.query('"'+d.TABLE_NAME_CONV+'"', columns, null, null, null, null, null);
        int num=0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            if(user.equals(cursor.getString(cursor.getColumnIndex(d.USER)))) num = cursor.getInt(cursor.getColumnIndex(d.NOTI));
        }
        return  num;
    }

    public ArrayList<MyMessage> getMessagesOfUser(String user){
        String[] columns = {d.MESSAGE,d.IS_MINE};
        Cursor cursor = db.query('"'+user+'"', columns, null, null, null, null, null);
        ArrayList<MyMessage> list = new ArrayList<>();
        boolean f;
        String m;
        Log.d("hiiiiiiiiiiiiiiii", "cursor lenght: " + cursor.getCount());
        String i;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            m = cursor.getString(cursor.getColumnIndex(d.MESSAGE));
            i = cursor.getString(cursor.getColumnIndex(d.IS_MINE));
            if(i.equals("t")) f = true;
            else f = false;
            MyMessage message = new MyMessage(m, f);
            list.add(message);
        }

        return list;
    }

    public void deleteUser(String contactName){
        db.delete(d.TABLE_NAME_CONV, d.USER + "='" + contactName + "'", null);
        db.execSQL("DROP TABLE IF EXISTS " + '"' + contactName + '"');
    }

    public ArrayList<Contact> getContacts(){
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        String[] columns = {d.USER, d.Gender};
        Cursor cursor = db.query(d.TABLE_NAME_CONV, columns, null, null, null, null, null);
        Contact contact;
        String user;
        String gender;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){

            user = cursor.getString(cursor.getColumnIndex(d.USER));
            gender = cursor.getString(cursor.getColumnIndex(d.Gender));
            contact = new Contact(user, gender);
            contacts.add(contact);
        }
        return contacts;
    }

    public void deleteAll(){
        String[] columns = {d.USER};
        Cursor cursor = db.query('"' + d.TABLE_NAME_CONV + '"', columns, null, null, null, null, null);
        String user;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){

            user = cursor.getString(cursor.getColumnIndex(d.USER));
            db.execSQL("DROP TABLE IF EXISTS "+'"'+user+'"');

        }
        db.execSQL("DROP TABLE IF EXISTS "+d.TABLE_NAME_CONV);


        d.onCreate(db);
    }


}
