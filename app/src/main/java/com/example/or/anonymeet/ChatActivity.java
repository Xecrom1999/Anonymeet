package com.example.or.anonymeet;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.util.UUID;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class ChatActivity extends AppCompatActivity {

    private String SENDER_ID;
    private String msgId;
    private GoogleCloudMessaging gcm;
    private RegistrationIntentService reg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        msgId = "12345";
        GcmBroadcastReceiver gcmBroadcastReceiver = new GcmBroadcastReceiver(this);
        gcm = GoogleCloudMessaging.getInstance(this);
        reg = new RegistrationIntentService("me");
        SENDER_ID = reg.getSenderID();

    }

    public void onClick(final View view) {
            new AsyncTask() {


                @Override
                protected String doInBackground(Object[] params) {
                    String msg = "";

                        Bundle data = new Bundle();
                        data.putString("my_message", "Hiiiiii you");
                        data.putString("my_action", "SAY_HELLO");
                    try {
                        gcm.send(SENDER_ID + "@gcm.googleapis.com", msgId, data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    msg = "Sent message";

                    Log.d("hiiiiiiiiiiiiiiiii", msg);
                    return msg;
                }




                protected void onPostExecute(String msg) {
                    
                }
            }.execute(null, null, null);


    }
}
