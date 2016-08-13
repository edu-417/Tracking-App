package com.example.eduardo.tabtest.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eduardo.tabtest.GroupTabFragment;
import com.example.eduardo.tabtest.R;

/**
 * Created by EDUARDO on 10/07/2016.
 */
public class GroupAdapter extends CursorAdapter {

    public GroupAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_group_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();

        String pathImage = cursor.getString(GroupTabFragment.COL_GROUP_ICON);
        //viewHolder.photoView.setImageResource();

        String name = cursor.getString(GroupTabFragment.COL_GROUP_NAME);
        viewHolder.nameView.setText(name);
    }

    public static class ViewHolder{
        public final ImageView iconView;
        public final TextView nameView;

        public ViewHolder(View view){
            iconView = (ImageView)view.findViewById(R.id.list_item_iconview);
            nameView = (TextView)view.findViewById(R.id.list_item_nameview);
        }
    }
}
