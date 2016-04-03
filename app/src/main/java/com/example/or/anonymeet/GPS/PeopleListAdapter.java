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
    ArrayList<Integer> distances;
    ListListener listener;

    public PeopleListAdapter(ListListener listener) {
        userNames = new ArrayList<>();
        distances = new ArrayList<>();
        this.listener = listener;
    }

    public void update(Collection<String> names, Collection<Integer> distances) {
        this.userNames = (ArrayList<String>) names;
        this.distances = (ArrayList<Integer>) distances;
        notifyDataSetChanged();
        if (userNames.size() == 0) listener.noUsers(true);
        else listener.noUsers(false);
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
        if (distances != null && distances.size() > 0) holder.distance_text.setText(distances.get(position) + " meters from you");
    }

    public int getItemCount() {
        return userNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name_text;
        TextView distance_text;
        String usernameTo;

        public ViewHolder(View itemView) {
            super(itemView);

            name_text = (TextView) itemView.findViewById(R.id.name_text);
            distance_text = (TextView) itemView.findViewById(R.id.distance_text);

            itemView.setOnClickListener(this);
        }

        public void onClick(View v) {
            listener.startChat(usernameTo);
        }
    }
}
