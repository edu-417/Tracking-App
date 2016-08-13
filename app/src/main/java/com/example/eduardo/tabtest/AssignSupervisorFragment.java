package com.example.eduardo.tabtest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eduardo.tabtest.adapter.ProfileAdapter;
import com.example.eduardo.tabtest.data.DbUtilities;
import com.example.eduardo.tabtest.data.TrackAppContract;
import com.example.eduardo.tabtest.utils.NetworkUtilities;
import com.example.eduardo.tabtest.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 */
public class AssignSupervisorFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String sSupervisorSelection =
            TrackAppContract.ProfileEntry.COLUMN_IS_SUPERVISOR + " = ? ";

    private static final int SUPERVISOR_LOADER = 0;

    private static final String[] SUPERVISOR_COLUMNS = {
            TrackAppContract.ProfileEntry.TABLE_NAME + "." + TrackAppContract.ProfileEntry._ID,
            TrackAppContract.ProfileEntry.COLUMN_PHOTO,
            TrackAppContract.ProfileEntry.COLUMN_NAME,
            TrackAppContract.ProfileEntry.COLUMN_CONTACT_KEY
    };

    static final int COL_PROFILE_ID = 0;
    static final int COL_PROFILE_PHOTO = 1;
    static final int COL_PROFILE_NAME = 2;
    static final int COL_PROFILE_CONTACT_KEY = 3;

    ProfileAdapter profileAdapter;

    public AssignSupervisorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_asign_supervisor, container, false);

        profileAdapter = new ProfileAdapter(getActivity(), null, 0);


        ListView listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if(cursor != null){
                    long contactID = cursor.getLong(COL_PROFILE_CONTACT_KEY);
                    cursor.close();

                    Cursor contactCursor = getActivity().getContentResolver().query(TrackAppContract.ContactEntry.CONTENT_URI,
                            new String[]{TrackAppContract.ContactEntry.COLUMN_CLOUD_ID},
                            TrackAppContract.ContactEntry.TABLE_NAME + "." + TrackAppContract.ContactEntry._ID + " = ?",
                            new String[]{Long.toString(contactID)},
                            null);

                    if(contactCursor != null){
                        long cloudID = contactCursor.getLong(0);
                        contactCursor.close();

                        Intent intent = getActivity().getIntent();
                        String name = intent.getStringExtra(NewGroupFragment.EXTRA_GROUP_NAME);
                        String iconPath = intent.getStringExtra(NewGroupFragment.EXTRA_GROUP_ICON);
                        long adminID = intent.getLongExtra(NewGroupFragment.EXTRA_GROUP_ADMIN_ID, 0);

                        new RegisterGroupTask().execute(name, iconPath, Long.toString(cloudID), Long.toString(adminID) );
                    }
                }

                getActivity().finish();
            }
        });
        listView.setAdapter(profileAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(SUPERVISOR_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = TrackAppContract.GroupEntry.COLUMN_NAME + " ASC";
        return new CursorLoader(getActivity(),
                TrackAppContract.ProfileEntry.CONTENT_URI,
                SUPERVISOR_COLUMNS,
                sSupervisorSelection,
                new String[]{Integer.toString(1)},
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        profileAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        profileAdapter.swapCursor(null);
    }


    public class RegisterGroupTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            JSONObject json = new JSONObject();

            String name = params[0];
            String iconPath = params[1];
            int supervisor = Integer.parseInt(params[2]);
            int administrator = Integer.parseInt(params[3]);

            try {
                json.put("name", name);
                json.put("supervisor", supervisor);
                json.put("administrator", administrator);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String BASE_URL_POST = NetworkUtilities.BASE_DOMAIN + "groups/";
            String jsonStr = "";
            Pair<Integer, String> result= NetworkUtilities.postJSON(BASE_URL_POST, json);

            int cloudID = 0;

            try {
                json = new JSONObject(jsonStr);
                cloudID = json.getInt("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            DbUtilities.insertGroup( getActivity(), cloudID, name, iconPath);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String token = sharedPreferences.getString(MainActivity.TOKEN, "NO TOKEN");
            try {
                Utilities.subscribeTopics(getActivity(), token, new String[]{"Group-" + cloudID});
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getActivity(), "Group created", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }
}
