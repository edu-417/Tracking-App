package com.example.eduardo.tabtest;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eduardo.tabtest.adapter.GroupAdapter;
import com.example.eduardo.tabtest.data.TrackAppContract;
import com.example.eduardo.tabtest.utils.Utilities;

public class GroupTabFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_TITLE = "Title";
    public static final String EXTRA_GROUP_ID = "GroupID";
    public static final String EXTRA_GROUP_CLOUD_ID = "GroupCloudID";

    private static final int GROUP_LOADER = 0;

    private static final String[] GROUP_COLUMNS = {
            TrackAppContract.GroupEntry.TABLE_NAME + "." + TrackAppContract.GroupEntry._ID,
            TrackAppContract.GroupEntry.COLUMN_ICON,
            TrackAppContract.GroupEntry.COLUMN_NAME,
            TrackAppContract.GroupEntry.COLUMN_CLOUD_ID
    };

    public static final int COL_GROUP_ID = 0;
    public static final int COL_GROUP_ICON = 1;
    public static final int COL_GROUP_NAME = 2;
    public static final int COL_GROUP_CLOUD_ID = 3;

    GroupAdapter groupAdapter;

    public GroupTabFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroup();
            }
        });

        groupAdapter = new GroupAdapter(getActivity(), null, 0);

        ListView listView = (ListView) rootView.findViewById(R.id.list_group);
        listView.setAdapter(groupAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if(cursor != null){

                    String name = cursor.getString(COL_GROUP_NAME);
                    long groupID = cursor.getLong(COL_GROUP_ID);
                    long groupCloudID = cursor.getLong(COL_GROUP_CLOUD_ID);

                    Intent chatIntent = new Intent(getActivity(), GroupChatActivity.class);
                    chatIntent.putExtra(EXTRA_TITLE, name);
                    chatIntent.putExtra(EXTRA_GROUP_ID, groupID);
                    chatIntent.putExtra(EXTRA_GROUP_CLOUD_ID, groupCloudID);

                    chatIntent.setData( TrackAppContract.GroupChatEntry.buildChatTo(groupID));
                    startActivity(chatIntent);
                }
            }
        });

        return rootView;
    }

    public void createGroup  (){
        if(!Utilities.haveSupervisorPrivileges(getActivity()) && !Utilities.haveAdminPrivileges(getActivity())){
            Toast.makeText(getActivity(), "You are not supervisor or don't have admin privileges", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(getActivity(), SelectSubscription.class);
        intent.putExtra(SelectSubscriptionFragment.EXTRA_THEME, "group");
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(GROUP_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = TrackAppContract.GroupEntry.COLUMN_NAME + " ASC";
        return new CursorLoader(getActivity(),
                TrackAppContract.GroupEntry.CONTENT_URI,
                GROUP_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        groupAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        groupAdapter.swapCursor(null);
    }
}
