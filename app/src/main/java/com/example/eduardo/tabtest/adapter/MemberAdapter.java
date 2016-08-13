package com.example.eduardo.tabtest.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eduardo.tabtest.ContactTabFragment;
import com.example.eduardo.tabtest.R;
import com.example.eduardo.tabtest.SelectMembersActivity;
import com.example.eduardo.tabtest.SelectMembersActivityFragment;

public class MemberAdapter extends CursorAdapter {

    public SelectMembersActivity mActivity;

    public MemberAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mActivity = (SelectMembersActivity)context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_select_member_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();

        String pathImage = cursor.getString(ContactTabFragment.COL_PROFILE_PHOTO);
        //viewHolder.photoView.setImageResource();

        final long contactID = cursor.getLong(SelectMembersActivityFragment.COL_PROFILE_CONTACT_KEY);

        String name = cursor.getString(ContactTabFragment.COL_PROFILE_NAME);
        viewHolder.nameView.setText(name);
        viewHolder.checkBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked ){
                    mActivity.members.add(contactID);
                }
                else{
                    mActivity.members.remove(contactID);
                }
            }
        });
    }

    public static class ViewHolder{
        public final ImageView photoView;
        public final TextView nameView;
        public final CheckBox checkBoxView;

        public ViewHolder(View view){
            photoView = (ImageView)view.findViewById(R.id.list_item_photoview);
            nameView = (TextView)view.findViewById(R.id.list_item_nameview);
            checkBoxView = (CheckBox)view.findViewById(R.id.list_item_check);
        }
    }
}
