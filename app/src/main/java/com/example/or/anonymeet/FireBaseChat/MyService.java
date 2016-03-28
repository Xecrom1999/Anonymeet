package com.example.or.anonymeet.FireBaseChat;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String name = intent.getExtras().getString("name");
        String message = intent.getExtras().getString("message");
        Intent i = new Intent("getMessages");
        i.putExtra("name", name);
        i.putExtra("message", message);
        sendBroadcast(i);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
