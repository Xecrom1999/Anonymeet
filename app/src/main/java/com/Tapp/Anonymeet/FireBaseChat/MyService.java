package com.Tapp.Anonymeet.FireBaseChat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.Tapp.Anonymeet.GPS.FindPeopleActivity;
import com.Tapp.Anonymeet.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MyService extends Service implements ChildEventListener {


    HelperDB db;
    Firebase myFirebaseChat;
    Firebase myFirebaseUsers;
    NotificationManager nm;
    SharedPreferences preferences;
    SharedPreferences.Editor se;
    String gender;
    String message;
    static int numOfNoti = 0;
    public static boolean isActive;

    public MyService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("hiiiiiiiiiiii", "onCreate");
        isActive = true;
        db = new HelperDB(getApplicationContext());
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
        Log.i("hiiiiiiiiiiii", "onDestroy");
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

        //finding out if read or message was changed
        //Log.i("hiiiiiiiiii", db.getUserLastMessage(dataSnapshot.getKey().toString()) + " = " + dataSnapshot.child("message").getValue().toString());
        //if (!(db.getUserLastMessage(dataSnapshot.getKey().toString()).equals(dataSnapshot.child("message").getValue().toString()))) {
            //if ((!dataSnapshot.child("read").exists()) || (dataSnapshot.child("read").getValue().toString().equals(preferences.getString("checkRead", "")))) {
            //if ((!dataSnapshot.child("arrived").exists()) || (dataSnapshot.child("arrived").getValue().toString().equals(preferences.getString("checkArrived", "")))){
            Log.i("hiiiiiiiiii", "a message has been recieved: " + dataSnapshot.child("message").getValue().toString());
            myFirebaseChat.child(dataSnapshot.getKey().toString()).child("arrived").setValue("true");
            message = dataSnapshot.child("message").getValue().toString();
            if (!db.userExists(dataSnapshot.getKey().toString())) {
                myFirebaseUsers.child(dataSnapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        gender = dataSnapshot.child("gender").getValue().toString();
                        db.insertUser(dataSnapshot.getKey().toString(), gender, 0);
                        db.insertMessage(dataSnapshot.getKey().toString(), message, false);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });


            } else {
                db.insertMessage(dataSnapshot.getKey().toString(), message, false);
            }

            if (ChatActivity.isActive() && ChatActivity.userWith.equals(dataSnapshot.getKey().toString())) {
                ChatActivity.recyclerAdapter.syncMessages();
                ChatActivity.scrollDown();
            } else {
                se.putInt("user " + dataSnapshot.getKey().toString(), 1 + preferences.getInt("user " + dataSnapshot.getKey().toString(), 0));
                numOfNoti += 1;
                if (numOfNoti == 1)
                    notifyOne(dataSnapshot.getKey().toString(), cleanCode(dataSnapshot.child("message").getValue().toString()));
                else notifyFew();

            }
            if (MessagesActivity.isActive) {
                MessagesActivity.usersAdapter.syncContacts();

            }
        //}




    }

    public String cleanCode(String m) {
        if (m.length() > 36 && m.substring(0, 36).equals("cbd9b0a2-d183-45ee-9582-27df3020ff65")) {
            m = m.substring(36);
        }
        return m;
    }



    public void notifyOne(String sender, String m){
        Notification.Builder n = new Notification.Builder(getApplicationContext())
                .setContentTitle(sender + " sent a message")
                .setContentText(m)
                .setSmallIcon(R.drawable.contact)
                .setAutoCancel(true)
                .setTicker("hiiiiii")
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_SOUND);
        TaskStackBuilder t = TaskStackBuilder.create(getApplicationContext());
        Intent i = new Intent(getApplicationContext(), FindPeopleActivity.class);
        i.putExtra("fromNoti", true);
        i.putExtra("usernameFrom", sender);
        t.addParentStack(FindPeopleActivity.class);
        t.addNextIntent(i);
        PendingIntent pendingIntent = t.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        n.setContentIntent(pendingIntent);
        nm.notify(0, n.build());
    }

    public void notifyFew(){
        Notification.Builder n = new Notification.Builder(getApplicationContext())
                .setContentTitle("You have " + numOfNoti + " new messages")
                .setSmallIcon(R.drawable.contact)
                .setAutoCancel(true)
                .setTicker("hiiiiii")
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_SOUND);
        TaskStackBuilder t = TaskStackBuilder.create(getApplicationContext());
        Intent i = new Intent(getApplicationContext(), FindPeopleActivity.class);
        i.putExtra("fromNotiFew", true);
        t.addParentStack(FindPeopleActivity.class);
        t.addNextIntent(i);
        PendingIntent pendingIntent = t.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        n.setContentIntent(pendingIntent);
        nm.notify(0, n.build());
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
