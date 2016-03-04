package com.example.or.anonymeet;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;


public class GcmBroadcastReceiver
        extends WakefulBroadcastReceiver {
    private Context context;


    public GcmBroadcastReceiver(Context c){
        this.context = c;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that
        // GcmIntentService will handle the intent.
        Log.d("hiiiiiiiii", "recieved");
        ComponentName comp =
                new ComponentName(
                        context.getPackageName(),
                        RegistrationIntentService.class.getName());
        // Start the service, keeping the
        // device awake while it is launching.
        startWakefulService(
                context,
                (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);

        Toast.makeText(this.context, ""+intent.getExtras().getString("message"), Toast.LENGTH_LONG).show();

    }
}
