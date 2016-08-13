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
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eduardo.tabtest.adapter.GroupAdapter;
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
public class AssignGroupFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private static final int GROUP_LOADER = 0;

    private static final String[] GROUP_COLUMNS = {
            TrackAppContract.GroupEntry.TABLE_NAME + "." + TrackAppContract.GroupEntry._ID,
            TrackAppContract.GroupEntry.COLUMN_ICON,
            TrackAppContract.GroupEntry.COLUMN_NAME,
            TrackAppContract.GroupEntry.COLUMN_CLOUD_ID
    };

    //static final int COL_GROUP_ID = 0;
    //static final int COL_GROUP_PHOTO = 1;
    //static final int COL_GROUP_NAME = 2;
    static final int COL_GROUP_CLOUD_ID = 3;

    GroupAdapter groupAdapter;

    public AssignGroupFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_assign_group, container, false);


        groupAdapter = new GroupAdapter(getActivity(), null, 0);


        ListView listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if(cursor != null){
                    long cloudID = cursor.getLong(COL_GROUP_CLOUD_ID);

                    Intent intent = getActivity().getIntent();
                    //String countryCode = intent.getStringExtra(NewUserFragment.EXTRA_CONTACT_COUNTRY_CODE);
                    //String phoneNumber = intent.getStringExtra(NewUserFragment.EXTRA_CONTACT_PHONE_NUMBER);

                    //new RegisterContactTask().execute(countryCode, phoneNumber, Long.toString(cloudID) );

                }
            }
        });
        listView.setAdapter(groupAdapter);

        return rootView;
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


    public long getContactCloudID(String phoneNumber){
        String BASE_URL = NetworkUtilities.BASE_DOMAIN + "users/phone/";
        String jsonStr = NetworkUtilities.getJSON(BASE_URL.concat(phoneNumber));
        Log.d("RESPONSE GET PHONE", jsonStr);

        int userID = 0;

        try {
            JSONObject jsonResponse = new JSONObject(jsonStr);
            userID = jsonResponse.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userID;
    }

    public class RegisterContactTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            String countryCode = params[0];
            String phoneNumber = params[1];
            long groupCloudID = Long.parseLong( params[2] );
            Log.d("PHONE_NUMBER", phoneNumber );

            long userCloudID = getContactCloudID(phoneNumber);
            long contactID = DbUtilities.insertContact(getActivity(), userCloudID, countryCode, phoneNumber);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String token = sharedPreferences.getString(MainActivity.TOKEN, "NO TOKEN");

            try {
                Utilities.subscribeTopics(getActivity(), token, new String[]{"Location-User-" + userCloudID});
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(userCloudID > 0){
                String BASE_URL = NetworkUtilities.BASE_DOMAIN + "users/";
                String jsonStr = NetworkUtilities.getJSON(BASE_URL.concat(userCloudID + "/profile"));

                String name = "Contact " + phoneNumber;
                String photo = "";

                try {
                    JSONObject jsonResponse = new JSONObject(jsonStr);
                    name = jsonResponse.getString("name");
                    photo = jsonResponse.getString("photo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                DbUtilities.insertContactProfile(getActivity(), contactID, name, photo);
            }else{
                DbUtilities.insertContactProfile(getActivity(), contactID, "Contact " + phoneNumber, "");
            }

            sendInvitation(userCloudID, groupCloudID);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getActivity(), "Contact registered", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }


    public int sendInvitation(long contactCloudID, long groupCloudID){
        String BASE_URL = NetworkUtilities.BASE_DOMAIN + "invitations/";

        JSONObject json = new JSONObject();

        try {
            json.put("ffrom", Utilities.getMyID(getActivity()));
            json.put("to", contactCloudID);
            json.put("content", "Se le ha invitado a un grupo " + groupCloudID);
            json.put("group", groupCloudID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int httpResult = -1;
        Pair<Integer, String> result = NetworkUtilities.postJSON( BASE_URL, json );

        if(result != null ){
            httpResult = result.first;
        }

        return httpResult;
    }


}
