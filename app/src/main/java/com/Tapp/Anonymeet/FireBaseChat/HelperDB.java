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

    public void insertUser(String user, String gender, long date){
        boolean f = userExists(user);
        if(!f) {
            ContentValues values = new ContentValues();
            values.put(d.USER, user);
            values.put(d.Gender, gender);
            values.put(d.DATE, date);
            db.insert(d.TABLE_NAME_CONV, null, values);
        }
        db.execSQL("CREATE TABLE IF NOT EXISTS " + '"' + user + '"' + " (" +
                d.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                d.MESSAGE + " varchar(225), " +
                d.IS_MINE + " char(1));");
    }

    public void updateDateOfUser(String user, long date) {
        ContentValues values = new ContentValues();
        values.put(d.DATE, date);
        db.update(d.TABLE_NAME_CONV, values, d.USER + "='" + user + "'", null);
    }


    public String getUserLastMessage(String user){
        String lastMessage = "";
        String[] columns = {d.MESSAGE,d.IS_MINE};


        Cursor cursor = db.query('"'+user+'"', columns, null, null, null, null, null);

        cursor.moveToLast();
            boolean f = false;


            while(!f && cursor.moveToPrevious()){
                String s1 = cursor.getString(cursor.getColumnIndex(d.IS_MINE));

                if(s1.equals("f")){
                    lastMessage = cursor.getString(cursor.getColumnIndex(d.MESSAGE));
                    f = true;
                }

            }


        return lastMessage;
    }

    public String getMyLastMessageWith(String user){
        String lastMessage = "";

        if(userExists(user)) {
            String[] columns = {d.MESSAGE,d.IS_MINE};


            Cursor cursor = db.query('"'+user+'"', columns, null, null, null, null, null);

            cursor.moveToLast();
            boolean f = false;


            while(!f && cursor.moveToPrevious()){
                String s1 = cursor.getString(cursor.getColumnIndex(d.IS_MINE));

                if(s1.equals("t")){
                    lastMessage = cursor.getString(cursor.getColumnIndex(d.MESSAGE));
                    f = true;
                }

            }
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




    public ArrayList<MyMessage> getMessagesOfUser(String user){
        String[] columns = {d.MESSAGE,d.IS_MINE};
        Cursor cursor = db.query('"'+user+'"', columns, null, null, null, null, null);
        ArrayList<MyMessage> list = new ArrayList<>();
        boolean f;
        String m;

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
        ArrayList<Contact> c = new ArrayList<Contact>();
        String[] columns = {d.USER, d.Gender, d.DATE};
        Cursor cursor = db.query(d.TABLE_NAME_CONV, columns, null, null, null, null, null);
        Contact contact;
        String user;
        String gender;
        long date;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){

            user = cursor.getString(cursor.getColumnIndex(d.USER));
            gender = cursor.getString(cursor.getColumnIndex(d.Gender));
            date = Long.parseLong(cursor.getString(cursor.getColumnIndex(d.DATE)));
            contact = new Contact(user, gender, date);
            c.add(contact);
        }

        ArrayList<Contact> contacts = new ArrayList<Contact>();
        while(!c.isEmpty()) {
            long max = c.get(0).date;
            int pos = 0;
            for(Contact con : c) {
                if (con.date > max) {
                    pos = c.indexOf(con);
                    max = con.date;
                }
            }
            contacts.add(c.get(pos));
            c.remove(c.get(pos));
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
