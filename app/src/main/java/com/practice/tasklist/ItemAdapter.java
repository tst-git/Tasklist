package com.practice.tasklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ItemAdapter extends BaseAdapter {

    LayoutInflater tInflater;
    TaskList tList;

    public ItemAdapter(Context c, TaskList tasks) {
        tList = tasks;
        tInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return tList.size(); }

    @Override
    public Object getItem(int position) { return tList.item(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = tInflater.inflate(R.layout.task_listview_detail, null);
        TextView nameTextView = (TextView) v.findViewById(R.id.nameListTextView);
        TextView descriptionTextView = (TextView) v.findViewById(R.id.descriptionListTextView);
        TextView priorityTextView = (TextView) v.findViewById(R.id.priorityListTextView);

        nameTextView.setText(tList.item(position).name);
        descriptionTextView.setText(tList.item(position).shortDescription);
        priorityTextView.setText(Integer.toString(tList.item(position).priority));

        return v;
    }

}
