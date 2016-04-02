package com.example.or.anonymeet.FireBaseChat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.or.anonymeet.R;

/**
 * Created by Or on 02/04/2016.
 */
public class ChatAdapter extends RecyclerView.Adapter<MessageViewHolder> {


    View v;
    LayoutInflater inflater;
    Context context;
    MessagesDB myDB;
    SQLiteDatabase db;

    public ChatAdapter(Context con, MessagesDB d){
        context = con;
        inflater = LayoutInflater.from(context);
        myDB = d;
        db = myDB.getWritableDatabase();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        v = inflater.inflate(R.layout.item_recycle_view, parent, false);
        MessageViewHolder viewHolder = new MessageViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 0;
    }
}

class MessageViewHolder extends RecyclerView.ViewHolder{

    public MessageViewHolder(View v){
        super(v);
    }
}
