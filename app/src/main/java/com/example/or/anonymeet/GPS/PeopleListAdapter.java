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

    ArrayList<String> names;
    ArrayList<String> addresses;

    public PeopleListAdapter() {
        names = new ArrayList<>();
        addresses = new ArrayList<>();
    }

    public void update(Collection<String> names, Collection<String> addresses) {
        this.names = (ArrayList<String>) names;
        this.addresses = (ArrayList<String>) addresses;
        notifyDataSetChanged();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name_text.setText(names.get(position));
        holder.address_text.setText(addresses.get(position));
    }

    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name_text;
        TextView address_text;

        public ViewHolder(View itemView) {
            super(itemView);

            name_text = (TextView) itemView.findViewById(R.id.name_text);
            address_text = (TextView) itemView.findViewById(R.id.address_text);

        }
    }
}
