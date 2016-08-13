package com.example.eduardo.tabtest.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.eduardo.tabtest.GroupChatActivityFragment;
import com.example.eduardo.tabtest.R;
import com.example.eduardo.tabtest.data.TrackAppContract;


public class GroupChatAdapter extends CursorAdapter {

    public GroupChatAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return cursor.getInt(GroupChatActivityFragment.COL_CHAT_IS_SEND);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int send = cursor.getInt(GroupChatActivityFragment.COL_CHAT_IS_SEND);

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
        String content = cursor.getString(GroupChatActivityFragment.COL_CHAT_CONTENT);
        String date = cursor.getString(GroupChatActivityFragment.COL_CHAT_CREATED);
        long contactID = cursor.getLong(GroupChatActivityFragment.COL_CHAT_FROM);
        String name = Long.toString(contactID);

        Cursor contactCursor = context.getContentResolver().query(TrackAppContract.ProfileEntry.CONTENT_URI,
                new String[]{TrackAppContract.ProfileEntry.COLUMN_NAME},
                TrackAppContract.ProfileEntry.COLUMN_CONTACT_KEY + " = ? ",
                new String[]{ Long.toString(contactID)},
                null
                );

        if(contactCursor != null){
            if( contactCursor.moveToFirst() ){
                name = contactCursor.getString(0);
            }
        }
        viewHolder.dateView.setText(date);
        viewHolder.contentView.setText(content);
        viewHolder.fromNameView.setText(name);
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
