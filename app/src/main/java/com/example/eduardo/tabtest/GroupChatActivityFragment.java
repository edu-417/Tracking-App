package com.example.eduardo.tabtest;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.eduardo.tabtest.adapter.GroupChatAdapter;
import com.example.eduardo.tabtest.data.DbUtilities;
import com.example.eduardo.tabtest.data.TrackAppContract;
import com.example.eduardo.tabtest.utils.NetworkUtilities;
import com.example.eduardo.tabtest.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class GroupChatActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CHAT_LOADER = 0;

    private static final String[] CHAT_COLUMNS = {
            TrackAppContract.GroupChatEntry.TABLE_NAME + "." + TrackAppContract.GroupChatEntry._ID,
            TrackAppContract.GroupChatEntry.COLUMN_FROM,
            TrackAppContract.GroupChatEntry.COLUMN_TO,
            TrackAppContract.GroupChatEntry.COLUMN_CONTENT,
            TrackAppContract.GroupChatEntry.COLUMN_CREATED,
            TrackAppContract.GroupChatEntry.COLUMN_IS_SEND
    };

    public static final int COL_CHAT_ID = 0;
    public static final int COL_CHAT_FROM = 1;
    public static final int COL_CHAT_TO = 2;
    public static final int COL_CHAT_CONTENT = 3;
    public static final int COL_CHAT_CREATED = 4;
    public static final int COL_CHAT_IS_SEND = 5;

    GroupChatAdapter chatAdapter;

    public GroupChatActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_chat, container, false);

        Button sendButton = (Button)rootView.findViewById(R.id.send_Button);
        final EditText messageText = (EditText)rootView.findViewById(R.id.message_text);

        Intent intent = getActivity().getIntent();
        final long groupID = intent.getLongExtra(GroupTabFragment.EXTRA_GROUP_ID, 0);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageText.getText().toString();
                messageText.setText("");
                sendMessage(0, groupID, message);

                long myID = Utilities.getMyID(getActivity());

                Cursor cursor = getActivity().getContentResolver().query(TrackAppContract.GroupEntry.CONTENT_URI,
                        new String[]{TrackAppContract.GroupEntry.COLUMN_CLOUD_ID},
                        TrackAppContract.GroupEntry.TABLE_NAME + "." + TrackAppContract.GroupEntry._ID + " = ?",
                        new String[]{Long.toString(groupID)},
                        null);

                if(cursor != null){
                    cursor.moveToFirst();
                    long toID = cursor.getLong(0);
                    String from = Long.toString(myID);
                    String to = Long.toString(toID);
                    new SendMessageTask().execute(from, to, message);
                }
            }
        });

        chatAdapter = new GroupChatAdapter(getActivity(), null, 0);

        ListView listView = (ListView) rootView.findViewById(R.id.list_chat);
        listView.setAdapter(chatAdapter);

        return rootView;
    }

    public void sendMessage(long from, long to, String message){
        DbUtilities.insertGroupChatMessage(getActivity(), from, to, message, true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(CHAT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        String sortOrder = TrackAppContract.GroupChatEntry.COLUMN_CREATED + " DESC";
        return new CursorLoader(getActivity(),
                intent.getData(),
                CHAT_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        chatAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        chatAdapter.swapCursor(null);
    }

    public class SendMessageTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            JSONObject json = new JSONObject();
            try {
                json.put("ffrom", params[0]);
                json.put("to", params[1]);
                json.put("content", params[2]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String BASE_URL_POST = NetworkUtilities.BASE_DOMAIN + "group/messages/";

            Pair<Integer, String> result =NetworkUtilities.postJSON(BASE_URL_POST, json);
            int httpResult = 0;
            if(result != null){
                httpResult = result.first;
            }


            return null;
        }
    }
}
