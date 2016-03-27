package com.example.or.anonymeet.FireBaseChat;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    OnItemClickListener mItemClickListener;


    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    Context context;

    public RecyclerAdapter(Context con, ArrayList<Contact> c, DB d){

        context = con;
        inflater = LayoutInflater.from(context);
        contacts = c;
        myDB = d;
        db = myDB.getWritableDatabase();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        v = inflater.inflate(R.layout.item_recycle_view, parent, false);


        MyViewHolder viewHolder = new MyViewHolder(v, contacts, this, mItemClickListener);
        return viewHolder;
    }




    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Contact c = contacts.get(position);
        holder.image.setImageResource(c.photo);
        holder.name .setText(c.name);
        holder.position = position;



    }

    @Override
    public int getItemCount() {
        return this.contacts.size();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void delete(int position){

        String contactName = contacts.get(position).name;
        db.delete(myDB.TABLE_NAME, myDB.USER + "='" + contactName + "'", null);
        contacts.remove(position);
        if (contacts.size() == 1) {
            notifyDataSetChanged();
        } else {
            notifyItemRemoved(position);
        }
    }


}

class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    ImageView image;
    RecyclerAdapter recyclerAdapter;
    ArrayList<Contact> contacts;
    OnItemClickListener mItemClickListener;
    TextView name;
    int position;

    public MyViewHolder(View v, ArrayList<Contact> c, final RecyclerAdapter r, OnItemClickListener m){
        super(v);
        recyclerAdapter = r;
        contacts = c;
        image = (ImageView) v.findViewById(R.id.contactImage);
        name = (TextView) v.findViewById(R.id.contactName);
        mItemClickListener = m;
        v.setOnClickListener(this);
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                r.delete(getPosition());
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        mItemClickListener.onItemClick(v, getPosition(), name.getText().toString()); //OnItemClickListener mItemClickListener;
    }



}

   interface OnItemClickListener {
    public void onItemClick(View view , int position, String name);

}

