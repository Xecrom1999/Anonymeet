package com.example.or.anonymeet.FireBaseChat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.or.anonymeet.R;

import java.util.ArrayList;

/**
 * Created by Or on 02/04/2016.
 */

class MyMessage{
    String message;
    boolean isMine;

    public MyMessage(){

    }
    public MyMessage(String message, boolean isMine) {
        this.message = message;
        this.isMine = isMine;
    }
}

public class ChatAdapter extends RecyclerView.Adapter<MessageViewHolder> {


    View sendV;
    View getV;
    LayoutInflater inflater;
    Context context;
    MessagesDB myDB;
    SQLiteDatabase db;
    String user;
    ArrayList<MyMessage> messages;
    ViewGroup parent;

    public ChatAdapter(Context con, MessagesDB d, String u){
        context = con;
        user = u ;
        inflater = LayoutInflater.from(context);
        myDB = d;
        db = myDB.getWritableDatabase();
        messages = myDB.getMessagesOfUser(user);
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup p, int viewType) {

        parent = p;
        sendV = inflater.inflate(R.layout.send_chat_message, p, false);
        getV = inflater.inflate(R.layout.get_chat_message, p, false);
        MessageViewHolder viewHolder = new MessageViewHolder(getV);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
      //  if(messages.get(position).isMine) holder = new MessageViewHolder(sendV);
      //  else holder = new MessageViewHolder(getV);
        holder.message = new MyMessage(messages.get(position).message, messages.get(position).isMine);
        holder.text.setText(holder.message.message);
    }

    public void syncMessages(){
        messages = myDB.getMessagesOfUser(user);
        notifyDataSetChanged(); //TODO: messages are not presented in chat activity
        }




    @Override
    public int getItemCount() {
        return this.messages.size();
    }
}

class MessageViewHolder extends RecyclerView.ViewHolder{

    MyMessage message;
    TextView text;

    public MessageViewHolder(View v){
        super(v);
        text = (TextView)v.findViewById(R.id.chat);
    }


}
