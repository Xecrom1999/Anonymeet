package com.example.or.anonymeet.GPS;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.or.anonymeet.R;

import java.util.ArrayList;
import java.util.Collection;

public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.ViewHolder> {

    ArrayList<String> userNames;
    ArrayList<String> addresses;
    ListListener listener;

    public PeopleListAdapter(ListListener listener) {
        userNames = new ArrayList<>();
        addresses = new ArrayList<>();
        this.listener = listener;
    }

    public void update(Collection<String> names, Collection<String> addresses) {
        this.userNames = (ArrayList<String>) names;
        this.addresses = (ArrayList<String>) addresses;
        notifyDataSetChanged();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = userNames.get(position);
        holder.usernameTo = name;
        holder.name_text.setText(name);
        holder.address_text.setText(addresses.get(position));
    }

    public int getItemCount() {
        return userNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name_text;
        TextView address_text;
        boolean gender;
        String usernameTo;


        public ViewHolder(View itemView) {
            super(itemView);

            name_text = (TextView) itemView.findViewById(R.id.name_text);
            address_text = (TextView) itemView.findViewById(R.id.address_text);

            itemView.setOnClickListener(this);
        }

        public void onClick(View v) {
            listener.startChat(usernameTo);
        }
    }
}
