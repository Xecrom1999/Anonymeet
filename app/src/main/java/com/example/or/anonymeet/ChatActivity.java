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
        gcm = GoogleCloudMessaging.getInstance(this);
        SENDER_ID = "260934602355";

    }

    public void onClick(final View view) {
        msgId = UUID.randomUUID().toString();
            new AsyncTask() {


                @Override
                protected String doInBackground(Object[] params) {
                    String msg = "";

                        Bundle data = new Bundle();
                        data.putString("my_message", "Hiiiiii you");
                        data.putString("my_action", "SAY_HELLO");
                    try {

                        gcm.send(SENDER_ID + "@gcm.googleapis.com", msgId, data);
                        msg = "Sent message";
                    } catch (IOException e) {
                        msg = "error";
                    }


                    Log.d("hiiiiiiiiiiiiiiiii", msg);
                    return msg;
                }




                protected void onPostExecute(String msg) {
                    
                }
            }.execute(null, null, null);


    }
}
