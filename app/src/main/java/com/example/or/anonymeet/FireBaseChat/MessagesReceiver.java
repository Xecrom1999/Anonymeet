package com.example.or.anonymeet.FireBaseChat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
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
        Notification.Builder n  = new Notification.Builder(context)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")
                .setSmallIcon(R.drawable.contact)
                .setAutoCancel(true)
                .setTicker("hiiiiii")
                .setDefaults(NotificationCompat.DEFAULT_SOUND);
        TaskStackBuilder t = TaskStackBuilder.create(context);
        Intent i = new Intent(context, MessagesActivity.class);
        t.addParentStack(MessagesActivity.class);
        t.addNextIntent(i);
        PendingIntent pendingIntent = t.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        n.setContentIntent(pendingIntent);
        nm.notify(0, n.build());

    }
}
