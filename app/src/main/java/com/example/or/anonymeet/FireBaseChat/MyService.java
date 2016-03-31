package com.example.or.anonymeet.FireBaseChat;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.or.anonymeet.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MyService extends IntentService {
    public MyService() {
        super("myService");
    }


    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
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
                Log.d("hiiiiiiiiiiiiiiiiii", "Service got");
                Notification.Builder n = new Notification.Builder(getApplicationContext())
                        .setContentTitle("New mail from " + "test@gmail.com")
                        .setContentText("Subject")
                        .setSmallIcon(R.drawable.contact)
                        .setAutoCancel(true)
                        .setTicker("hiiiiii")
                        .setDefaults(NotificationCompat.DEFAULT_SOUND);
                TaskStackBuilder t = TaskStackBuilder.create(getApplicationContext());
                Intent i = new Intent(getApplicationContext(), MessagesActivity.class);
                t.addParentStack(MessagesActivity.class);
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
        });
        return START_STICKY;
    }




    @Override
    public IBinder onBind(Intent intent) {
        Log.d("hiiiiiiiiiiiiiiiiii", "onBind");
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("hiiiiiiiiiiiiiiiiii", "onHandleIntent");
    }
}
