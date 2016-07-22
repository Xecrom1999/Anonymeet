package com.example.or.anonymeet.FireBaseChat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
    String gender;

    public Contact(String name, String gender){

        this.gender = gender;
        if(gender.equals("male"))this.photo = R.drawable.boy2;
        else this.photo= R.drawable.girl2;
        this.name=name;

    }

}

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    ArrayList<Contact> contacts;
    LayoutInflater inflater;
    MessagesDB myDB;
    SQLiteDatabase db;
    View v;
    MyListener mItemClickListener;
    Context context;
    UsersAdapter adapter = this;

    public UsersAdapter(Context con, MessagesDB d, MyListener myListener){

        this.mItemClickListener = myListener;

        context = con;
        inflater = LayoutInflater.from(context);
        myDB = d;
        db = myDB.getWritableDatabase();
        syncContacts();

    }

    public void syncContacts(){
        contacts = new ArrayList<Contact>();
        String[] columns = {myDB.USER, myDB.Gender};
        Cursor cursor = db.query(myDB.TABLE_NAME_CONV, columns, null, null, null, null, null);
        Contact contact;
        String user;
        String gender;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){

            user = cursor.getString(cursor.getColumnIndex(myDB.USER));
            gender = cursor.getString(cursor.getColumnIndex(myDB.Gender));
            Log.i("cccccccccccccc", gender);
            contact = new Contact(user, gender);
            contacts.add(contact);
        }
        notifyDataSetChanged();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        v = inflater.inflate(R.layout.item_recycle_view, parent, false);


        MyViewHolder viewHolder = new MyViewHolder(v, this);
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

    public void SetOnItemClickListener(final MyListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void delete(int position) {

        String contactName = contacts.get(position).name;
        db.delete(myDB.TABLE_NAME_CONV, myDB.USER + "='" + contactName + "'", null);
        db.execSQL("DROP TABLE IF EXISTS " + '"' + contactName + '"');
        contacts.remove(position);
        if (contacts.size() == 1) {
            notifyDataSetChanged();
        } else {
            notifyItemRemoved(position);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView image;
        TextView name;
        int position;

        public MyViewHolder(View v, final UsersAdapter adapter){
            super(v);
            image = (ImageView) v.findViewById(R.id.contactImage);
            name = (TextView) v.findViewById(R.id.contactName);
            v.setOnClickListener(this);
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete Contact");
                    builder.setMessage("Are you sure you want to delete your contact? You will lose your chat history too.");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UsersAdapter.this.adapter.delete(getPosition());
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    Dialog dialog = builder.create();
                    dialog.show();


                    return false;
                }
            });

        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(v, getPosition(), name.getText().toString());
        }
    }

}



   interface MyListener {

    public void onItemClick(View view , int position, String name);
}

