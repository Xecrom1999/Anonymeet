package com.example.or.anonymeet.FireBaseChat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.or.anonymeet.R;

public class MessagesReceiver extends BroadcastReceiver {
    Context context;
    NotificationManager nm;

    public MessagesReceiver() {
    }
    public MessagesReceiver(Context c, NotificationManager n) {
        context = c;
        nm = n;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("hiiiiiiiiiiiiiiiiii", "Receiver started");
        Notification n  = new Notification.Builder(context)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")
                .setSmallIcon(R.drawable.ic_media_play)
                .setAutoCancel(true)
                .build();

        nm.notify(0, n);
    }
}
