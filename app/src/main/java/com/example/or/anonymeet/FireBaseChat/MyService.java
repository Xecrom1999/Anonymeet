package com.example.or.anonymeet.FireBaseChat;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.or.anonymeet.GPS.FindPeopleActivity;
import com.example.or.anonymeet.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MyService extends Service implements ChildEventListener{


    MessagesDB myDB;
    SQLiteDatabase db;
    Firebase myFirebaseChat;
    Firebase myFirebaseUsers;
    NotificationManager nm;
    SharedPreferences preferences;
    SharedPreferences.Editor se;
    String gender;
    String message;
    public static boolean isActive;

    public MyService() {

}

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("hiiiiiiiiiiii", "onCreate");
        isActive = true;
        myDB = new MessagesDB(this);
        db = myDB.getWritableDatabase();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        String myNickname = preferences.getString("nickname", "");
        myFirebaseUsers = new Firebase("https://anonymeetapp.firebaseio.com/Users");
        myFirebaseChat = new Firebase("https://anonymeetapp.firebaseio.com/Chat");
        myFirebaseChat = myFirebaseChat.child(myNickname);
        myFirebaseChat.addChildEventListener(this);
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        se = preferences.edit();
        se.putString("check", "").commit();


    }



    @Override
    public void onDestroy() {
        Log.i("hiiiiiiiiiiii", "onCreate");
        myFirebaseChat.removeEventListener(this);
        isActive = false;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.i("hiiiiiiiiiiii", "onStartCommand");
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        se = preferences.edit();
        return super.onStartCommand(intent, flags, startId);
    }





    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        Log.i("hiiiiiiiiii", "child changed");
        //finding out if read or message was changed
        Log.i("hiiiiiiiiii", "first argument: "+(!dataSnapshot.child("message").getValue().toString().equals(null)));
        Log.i("hiiiiiiiiii", "second argument: "+(!dataSnapshot.child("message").getValue().toString().equals(preferences.getString("check", ""))));
        Log.i("hiiiiiiiiii", "a message has been recieved: " + dataSnapshot.child("message").getValue().toString());
        if ((!dataSnapshot.child("message").getValue().equals(null))&&(!dataSnapshot.child("message").getValue().toString().equals(preferences.getString("check", "")))) {
            Log.i("hiiiiiiiiii", "a message has been recieved: " + dataSnapshot.child("message").getValue().toString());
            message = cleanCode(dataSnapshot.child("message").getValue().toString());
            if(!myDB.userExists(dataSnapshot.getKey().toString())){
                myFirebaseUsers.child(dataSnapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        gender = dataSnapshot.child("gender").getValue().toString();
                        myDB.insertUser(dataSnapshot.getKey().toString(), gender);
                        myDB.insertMessage(dataSnapshot.getKey().toString(), message, false);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });


            }else{
                myDB.insertMessage(dataSnapshot.getKey().toString(), message, false);
            }




            if (ChatActivity.isActive() && ChatActivity.userWith.equals(dataSnapshot.getKey().toString())) {
                ChatActivity.recyclerAdapter.syncMessages();
                ChatActivity.scrollDown();
            } else {

                Notification.Builder n = new Notification.Builder(getApplicationContext())
                        .setContentTitle(dataSnapshot.getKey().toString() + "sent a message")
                        .setContentText(cleanCode(dataSnapshot.child("message").getValue().toString()))
                        .setSmallIcon(R.drawable.contact)
                        .setAutoCancel(true)
                        .setTicker("hiiiiii")
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND);
                TaskStackBuilder t = TaskStackBuilder.create(getApplicationContext());
                Intent i = new Intent(getApplicationContext(), FindPeopleActivity.class);
                i.putExtra("fromNoti", true);
                i.putExtra("usernameFrom", dataSnapshot.getKey().toString());
                t.addParentStack(FindPeopleActivity.class);
                t.addNextIntent(i);
                PendingIntent pendingIntent = t.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                n.setContentIntent(pendingIntent);
                nm.notify(0, n.build());
            }
            if (MessagesActivity.isActive) {
                MessagesActivity.usersAdapter.syncContacts();

            }
        }
        se.putString("check", dataSnapshot.child("message").getValue().toString()).commit();
    }

    public String cleanCode(String m) {
        if (m.length() > 36 && m.substring(0, 36).equals("cbd9b0a2-d183-45ee-9582-27df3020ff65")) {
            m = m.substring(36);
        }
        return m;
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
