package com.Tapp.Anonymeet.GPS;

import com.firebase.client.Firebase;

/**
 * Created by user on 03/03/16.
 */
public class FireBaseHelper extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        LocationListenerService.cancelNotification();
    }


}
