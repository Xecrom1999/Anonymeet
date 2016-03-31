package com.example.or.anonymeet.FireBaseChat;

import android.app.IntentService;
import android.app.Service;
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
        Log.d("hiiiiiiiiiiiiiiiiii", "Serveice started");
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
                NotificationCompat.Builder n = new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle("New mail from " + "test@gmail.com")
                        .setContentText("Subject")
                        .setSmallIcon(R.drawable.contact)
                        .setDefaults()

                nm.notify(0, n);
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
        return super.onStartCommand(intent, flags, startId);
    }

    @Override


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
