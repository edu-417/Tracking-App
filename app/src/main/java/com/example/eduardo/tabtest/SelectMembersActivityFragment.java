package com.example.eduardo.tabtest;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eduardo.tabtest.adapter.MemberAdapter;
import com.example.eduardo.tabtest.data.DbUtilities;
import com.example.eduardo.tabtest.data.TrackAppContract;
import com.example.eduardo.tabtest.utils.NetworkUtilities;
import com.example.eduardo.tabtest.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class SelectMembersActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = SelectMembersActivityFragment.class.getSimpleName();

    private static final int PROFILE_LOADER = 0;

    private static final String[] PROFILE_COLUMNS = {
            TrackAppContract.ProfileEntry.TABLE_NAME + "." + TrackAppContract.ProfileEntry._ID,
            TrackAppContract.ProfileEntry.COLUMN_PHOTO,
            TrackAppContract.ProfileEntry.COLUMN_NAME,
            TrackAppContract.ProfileEntry.COLUMN_CONTACT_KEY
    };

    public static final int COL_PROFILE_ID = 0;
    public static final int COL_PROFILE_PHOTO = 1;
    public static final int COL_PROFILE_NAME = 2;
    public static final int COL_PROFILE_CONTACT_KEY = 3;

    MemberAdapter memberAdapter;

    public SelectMembersActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_members, container, false);

        memberAdapter = new MemberAdapter(getActivity(), null, 0);

        Button continueButton = (Button)rootView.findViewById(R.id.continue_button);

        final ListView listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setAdapter(memberAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor)parent.getItemAtPosition(position);

            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getActivity().getIntent();
                String action = intent.getStringExtra(SelectMembersActivity.EXTRA_ACTION);

                if( action != null ){
                    if( action.equals("group") ){
                        String name = intent.getStringExtra(NewGroupFragment.EXTRA_GROUP_NAME);
                        String iconPath = intent.getStringExtra(NewGroupFragment.EXTRA_GROUP_ICON);
                        long adminID = intent.getLongExtra(NewGroupFragment.EXTRA_GROUP_ADMIN_ID, 0);

                        if( NetworkUtilities.isOnline(getActivity())){
                            new RegisterGroupTask().execute(name, iconPath, Long.toString(adminID), Long.toString(adminID));
                        }
                        else{
                            Toast.makeText(getActivity(), R.string.no_network_connection, Toast.LENGTH_LONG).show();
                        }
                    }
                    else if(action.equals("member")){
                        long groupID = intent.getLongExtra(GroupChatActivity.EXTRA_GROUP_ID, 0);
                        long groupCloudID = intent.getLongExtra(GroupChatActivity.EXTRA_GROUP_CLOUD_ID, 0);

                        if( NetworkUtilities.isOnline(getActivity())){
                            new RegisterMembersTask().execute(Long.toString(groupID), Long.toString(groupCloudID));
                        }
                        else{
                            Toast.makeText(getActivity(), R.string.no_network_connection, Toast.LENGTH_LONG).show();
                        }


                    }
                }
            }
        });

        return rootView;
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
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        memberAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        memberAdapter.swapCursor(null);
    }

    public class RegisterGroupTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            JSONObject json = new JSONObject();

            String name = params[0];
            String iconPath = params[1];
            long supervisor = Long.parseLong(params[2]);
            long administrator = Long.parseLong(params[3]);

            try {
                json.put("name", name);
                json.put("supervisor", supervisor);
                json.put("administrator", administrator);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String BASE_URL_POST = NetworkUtilities.BASE_DOMAIN + "groups/";

            Pair<Integer, String> result = NetworkUtilities.postJSON(BASE_URL_POST, json);

            String jsonStr = "";

            if( result != null ){
                jsonStr = result.second;
            }

            long groupCloudID = 0;

            try {
                json = new JSONObject(jsonStr);
                groupCloudID = json.getLong("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            long groupID = DbUtilities.insertGroup( getActivity(), groupCloudID, name, iconPath);

            String token = Utilities.getMyToken(getActivity());
            try {
                Utilities.subscribeTopics(getActivity(), token, new String[]{"Group-" + groupCloudID});
            } catch (IOException e) {
                e.printStackTrace();
            }

            registerMembers(groupID, groupCloudID);

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

    public class RegisterMembersTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            long groupID = Long.parseLong(params[0]);
            long groupCloudID = Long.parseLong(params[1]);

            registerMembers(groupID, groupCloudID);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Intent intent = new Intent(getActivity(), Monitoring.class);
            //startActivity(intent);
            Toast.makeText(getActivity(), "Members added", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    public void registerMembers(long groupID, long groupCloudID){

        String token = Utilities.getMyToken(getActivity());

        Log.i(TAG, "Inserting members of group " + groupID );

        Iterator<Long> iterator = ((SelectMembersActivity)getActivity()).members.iterator();

        while (iterator.hasNext()){
            long memberID = iterator.next();
            Log.i(TAG, "Inserting member" + memberID );

            DbUtilities.insertGroupMember(getActivity(), memberID, groupID);

            Cursor cursor = getActivity().getContentResolver().query(TrackAppContract.ContactEntry.CONTENT_URI,
                    new String[]{TrackAppContract.ContactEntry.COLUMN_CLOUD_ID},
                    TrackAppContract.ContactEntry._ID + " = ?",
                    new String[]{Long.toString(memberID)},
                    null);

            if( cursor != null ){
                if( cursor.moveToFirst() ){
                    long memberCloudID = cursor.getLong(0);
                    sendGroupInvitation(memberCloudID, groupCloudID);
                }
                cursor.close();
            }
        }
    }

    public String sendGroupInvitation(long contactCloudID, long groupCloudID){
        String BASE_URL = NetworkUtilities.BASE_DOMAIN + "group/invitations/";

        JSONObject json = new JSONObject();

        try {
            long myID = Utilities.getMyID(getActivity());
            json.put("ffrom", myID);
            json.put("to", contactCloudID);
            json.put("content", "El contacto " + myID + " le ha invitado a un grupo " + groupCloudID);
            json.put("group", groupCloudID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Pair<Integer, String> result = NetworkUtilities.postJSON( BASE_URL, json );
        String jsonStr = "";
        if( result != null){
            jsonStr = result.second;
        }

        return jsonStr;
    }

}
