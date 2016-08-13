package com.example.eduardo.tabtest.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.eduardo.tabtest.ChatActivityFragment;
import com.example.eduardo.tabtest.R;


public class ChatAdapter extends CursorAdapter {

    public ChatAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return cursor.getInt(ChatActivityFragment.COL_CHAT_IS_SEND);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int send = cursor.getInt(ChatActivityFragment.COL_CHAT_IS_SEND);

        int layoutID = R.layout.list_chat_in_item;
        if(send > 0){
            layoutID = R.layout.list_chat_out_item;
        }

        View view = LayoutInflater.from(context).inflate(layoutID, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        String content = cursor.getString(ChatActivityFragment.COL_CHAT_CONTENT);
        String date = cursor.getString(ChatActivityFragment.COL_CHAT_CREATED);
        viewHolder.dateView.setText(date);
        viewHolder.contentView.setText(content);
    }

    public static class ViewHolder{
        public final TextView contentView;
        public final TextView dateView;
        public final TextView fromNameView;

        public ViewHolder(View view){
            fromNameView = (TextView)view.findViewById(R.id.list_item_from_name_text);
            contentView = (TextView)view.findViewById(R.id.list_item_content_text);
            dateView = (TextView)view.findViewById(R.id.list_item_date_text);
        }
    }
}
