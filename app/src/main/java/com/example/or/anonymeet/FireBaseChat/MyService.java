package com.example.or.anonymeet.FireBaseChat;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.or.anonymeet.GPS.GPSActivity;
import com.example.or.anonymeet.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MyService extends IntentService{

    boolean activityOn;
    MessagesDB myDB;
    SQLiteDatabase db;
    public static boolean isActive;

    public MyService() {
        super("myService");
}


    @Override
    public void onDestroy() {
        Log.d("hiiiiii", "onDestroy");
        isActive = false;
        super.onDestroy();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.d("hiiiiiiiiiiiiiii", "started");
        isActive = true;
        activityOn = false;
        myDB = new MessagesDB(this);
        db = myDB.getWritableDatabase();
        final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        String myEmail = preferences.getString("email", "");
        myEmail = myEmail.substring(0, myEmail.indexOf('.'));
        Firebase myFirebaseRef = new Firebase("https://anonymeetapp.firebaseio.com/Chat");
        myFirebaseRef = myFirebaseRef.child(myEmail);
        myFirebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue().toString().length()>36 && dataSnapshot.getValue().toString().substring(0, 36).equals("cbd9b0a2-d183-45ee-9582-27df3020ff65")){

                    Log.d("hiiiiiiiiiiiiiiiii", "adds with code");
                    myDB.insertMessage(dataSnapshot.getKey().toString(), dataSnapshot.getValue().toString().substring(36), false);


                }
                else {
                    Log.d("hiiiiiiiiiiiiiiiii", "adds without code");
                    myDB.insertMessage(dataSnapshot.getKey().toString(), dataSnapshot.getValue().toString(), false);
                }


                if (!(ChatActivity.isActive() && ChatActivity.emailWith.equals(dataSnapshot.getKey().toString()))) {
                    Notification.Builder n = new Notification.Builder(getApplicationContext())
                            .setContentTitle("New message from a " + "")
                            .setContentText(dataSnapshot.getValue().toString())
                            .setSmallIcon(R.drawable.contact)
                            .setAutoCancel(true)
                            .setTicker("hiiiiii")
                            .setDefaults(NotificationCompat.DEFAULT_SOUND);
                    TaskStackBuilder t = TaskStackBuilder.create(getApplicationContext());
                    Intent i = new Intent(getApplicationContext(), GPSActivity.class);
                    i.putExtra("fromNoti", true);
                    t.addParentStack(GPSActivity.class);
                    t.addNextIntent(i);
                    PendingIntent pendingIntent = t.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    n.setContentIntent(pendingIntent);
                    nm.notify(0, n.build());
                }
                else{
                    ChatActivity.recyclerAdapter.syncMessages();
                }
                if(MessagesActivity.isActive)MessagesActivity.recyclerAdapter.syncContacts();
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
        });
        return START_STICKY;
    }




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }


}
