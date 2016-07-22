package com.example.or.anonymeet.GPS;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class StatusReceiver extends BroadcastReceiver {
    public StatusReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, LocationListenerService.class);
        context.stopService(intent1);
    }
}
