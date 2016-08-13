package com.example.eduardo.tabtest.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.eduardo.tabtest.R;
import com.example.eduardo.tabtest.SelectSubscriptionFragment;


public class SubscriptionAdapter extends CursorAdapter {
    public SubscriptionAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_select_subscription_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();

        long contactID = cursor.getLong(SelectSubscriptionFragment.COL_SUBSCRIPTION_SUBSCRIBER_ID);
        String name = Long.toString(contactID);
        viewHolder.nameView.setText(name);
    }

    public static class ViewHolder{
        public final TextView nameView;

        public ViewHolder(View view){
            nameView = (TextView)view.findViewById(R.id.list_item_nameview);
        }
    }
}
