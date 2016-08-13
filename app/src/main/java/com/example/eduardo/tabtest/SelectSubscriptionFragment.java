package com.example.eduardo.tabtest;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.eduardo.tabtest.adapter.MemberAdapter;
import com.example.eduardo.tabtest.adapter.SubscriptionAdapter;
import com.example.eduardo.tabtest.data.TrackAppContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class SelectSubscriptionFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = SelectSubscriptionFragment.class.getSimpleName();

    public static final String EXTRA_THEME = "theme";

    public static final String EXTRA_SUBSCRIPTION_ID = "subscriptionID";
    public static final String EXTRA_SUBSCRIPTION_CLOUD_ID = "subscriptionCloudID";

    private static final int SUBSCRIPTION_LOADER = 0;

    private static final String[] SUBSCRIPTION_COLUMNS = {
            TrackAppContract.SubscriptionEntry.TABLE_NAME + "." + TrackAppContract.SubscriptionEntry._ID,
            TrackAppContract.SubscriptionEntry.COLUMN_SUBSCRIBER_KEY,
            TrackAppContract.SubscriptionEntry.COLUMN_CLOUD_ID
    };

    public static final int COL_SUBSCRIPTION_ID = 0;
    public static final int COL_SUBSCRIPTION_SUBSCRIBER_ID = 1;
    public static final int COL_SUBSCRIPTION_CLOUD_ID = 2;

    SubscriptionAdapter subscriptionAdapter;

    public SelectSubscriptionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_subscription, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview);

        subscriptionAdapter = new SubscriptionAdapter( getActivity(), null, 0);
        listView.setAdapter(subscriptionAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor)parent.getItemAtPosition(position);
                long subscriptionID = cursor.getLong(COL_SUBSCRIPTION_ID);
                long subscriptionCloudID = cursor.getLong(COL_SUBSCRIPTION_CLOUD_ID);

//                Cursor contactCursor = getActivity().getContentResolver().query(TrackAppContract.ProfileEntry.CONTENT_URI,
//                        new String[]{TrackAppContract.ProfileEntry.COLUMN_NAME},
//                        TrackAppContract.ProfileEntry.COLUMN_CONTACT_KEY + " = ? ",
//                        new String[]{Long.toString(contactID)},
//                        null);
//
//                String name = "ERROR";
//                if(contactCursor != null){
//                    if( contactCursor.moveToFirst() )
//                        name = contactCursor.getString(0);
//                    contactCursor.close();
//                }

                Intent intent = getActivity().getIntent();
                String theme = intent.getStringExtra(EXTRA_THEME);
                if(theme.equals("contact")){
                    Intent userIntent = new Intent(getActivity(), NewUser.class);
                    userIntent.putExtra(EXTRA_SUBSCRIPTION_ID, subscriptionID);
                    userIntent.putExtra(EXTRA_SUBSCRIPTION_CLOUD_ID, subscriptionCloudID);
                    startActivity(userIntent);
                    return;
                }
                if(theme.equals("group")){
                    Intent groupIntent = new Intent(getActivity(), NewGroup.class);
                    groupIntent.putExtra(EXTRA_SUBSCRIPTION_ID, subscriptionID);
                    groupIntent.putExtra(EXTRA_SUBSCRIPTION_CLOUD_ID, subscriptionCloudID);
                    startActivity(groupIntent);
                    return;
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(SUBSCRIPTION_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                TrackAppContract.SubscriptionEntry.CONTENT_URI,
                SUBSCRIPTION_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        subscriptionAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        subscriptionAdapter.swapCursor(null);
    }
}
