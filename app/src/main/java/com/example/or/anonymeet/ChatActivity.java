package com.example.or.anonymeet;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.or.anonymeet.GPS.GPSActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private String SENDER_ID;
    private String msgId;
    private GoogleCloudMessaging gcm;
    private RegistrationIntentService reg;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        gcm = GoogleCloudMessaging.getInstance(this);
        SENDER_ID = "260934602355";

        button = (Button) findViewById(R.id.activity_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GPSActivity.class);
                startActivity(i);
            }
        });
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
