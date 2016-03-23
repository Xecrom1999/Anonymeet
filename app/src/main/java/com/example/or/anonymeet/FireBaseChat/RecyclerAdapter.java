package com.example.or.anonymeet.FireBaseChat;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.or.anonymeet.R;

import java.util.ArrayList;

/**
 * Created by Or on 18/01/2016.
 */
class Contact{
    int photo;
    String name;

    public Contact(String name){
        this.photo= R.drawable.contact;
        this.name=name;

    }

}

public class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

    ArrayList<Contact> contacts;
    LayoutInflater inflater;
    DB myDB;
    SQLiteDatabase db;
    View v;
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    Context context;

    public RecyclerAdapter(Context con, ArrayList<Contact> c, DB d){

        inflater = LayoutInflater.from(context);
        contacts = c;
        context = con;
        myDB = d;
        db = myDB.getWritableDatabase();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        v = inflater.inflate(R.layout.item_recycle_view, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                context.startActivity(intent);
            }
        });


        MyViewHolder viewHolder = new MyViewHolder(v, contacts, this);
        return viewHolder;
    }




    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Contact c = contacts.get(position);
        holder.image.setImageResource(c.photo);
        holder.name .setText(c.name);



    }

    @Override
    public int getItemCount() {
        return this.contacts.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{

    ImageView image;
    RecyclerAdapter recyclerAdapter;
    ArrayList<Contact> contacts;
    TextView name;
    public MyViewHolder(View v, ArrayList<Contact> c, final RecyclerAdapter r){
        super(v);
        recyclerAdapter = r;
        contacts = c;
        image = (ImageView) v.findViewById(R.id.contactImage);
        name = (TextView) v.findViewById(R.id.contactName);


    }



}
