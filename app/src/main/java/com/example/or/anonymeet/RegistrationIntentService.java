package com.example.or.anonymeet;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;


public class RegistrationIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private String token;

    public RegistrationIntentService(String name) {
        super(name);
    }
    // ...

    @Override
    public void onHandleIntent(Intent intent) {
        // ...
        String authorizedEntity = "anonymeet-1197"; // Project id from Google Developer Console
        String scope = "GCM"; // e.g. communicating using GCM, but you can use any
        // URL-safe characters up to a maximum of 1000, or
        // you can also leave it blank.
        try {
            String token = InstanceID.getInstance(this).getToken(authorizedEntity, scope);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String getSenderID(){
        return token;
    }

}
