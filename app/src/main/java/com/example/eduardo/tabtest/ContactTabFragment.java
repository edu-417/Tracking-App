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

import com.example.eduardo.tabtest.adapter.ProfileAdapter;
import com.example.eduardo.tabtest.data.TrackAppContract;
import com.example.eduardo.tabtest.utils.Utilities;


public class ContactTabFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String EXTRA_TITLE = "Title";
    public static final String EXTRA_CONTACT_ID = "ContactID";

    private static final int PROFILE_LOADER = 0;

    private static final String[] PROFILE_COLUMNS = {
            TrackAppContract.ProfileEntry.TABLE_NAME + "." + TrackAppContract.ProfileEntry._ID,
            TrackAppContract.ProfileEntry.COLUMN_PHOTO,
            TrackAppContract.ProfileEntry.COLUMN_NAME,
            TrackAppContract.ProfileEntry.COLUMN_CONTACT_KEY
    };

    //public static final int COL_PROFILE_ID = 0;
    public static final int COL_PROFILE_PHOTO = 1;
    public static final int COL_PROFILE_NAME = 2;
    public static final int COL_PROFILE_CONTACT_KEY = 3;

    ProfileAdapter profileAdapter;

    public ContactTabFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createContact();
            }
        });

        profileAdapter = new ProfileAdapter(getActivity(), null, 0);

        ListView listView = (ListView) rootView.findViewById(R.id.list_contact);
        listView.setAdapter(profileAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if(cursor != null){

                    String name = cursor.getString(COL_PROFILE_NAME);
                    long contactID = cursor.getInt(COL_PROFILE_CONTACT_KEY);

                    Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                    chatIntent.putExtra(EXTRA_TITLE, name);
                    chatIntent.putExtra(EXTRA_CONTACT_ID, contactID);

                    chatIntent.setData(TrackAppContract.ChatEntry.buildChatFromTo(0, contactID));
                    startActivity(chatIntent);
                }
            }
        });

        return rootView;
    }

    public void createContact  (){
        boolean haveAdminPrivileges = Utilities.haveAdminPrivileges(getActivity());
        if(!haveAdminPrivileges){
            Toast.makeText(getActivity(), "You need to subscribe", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(getActivity(), SelectSubscription.class);
        intent.putExtra(SelectSubscriptionFragment.EXTRA_THEME, "contact");
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(PROFILE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = TrackAppContract.ProfileEntry.COLUMN_NAME + " ASC";
        return new CursorLoader(getActivity(),
                TrackAppContract.ProfileEntry.CONTENT_URI,
                PROFILE_COLUMNS,
                TrackAppContract.ProfileEntry.COLUMN_CONTACT_KEY + " > ? ",
                new String[]{"0"},
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        profileAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        profileAdapter.swapCursor(null);
    }

}
