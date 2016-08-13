package com.example.eduardo.tabtest;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.eduardo.tabtest.data.TrackAppContract;
import com.example.eduardo.tabtest.utils.NetworkUtilities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewSupervisorActivityFragment extends Fragment {

    public NewSupervisorActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_supervisor, container, false);

        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.countries_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button button = (Button) rootView.findViewById(R.id.continue_button);

        final EditText countryCodeText = (EditText) rootView.findViewById(R.id.country_code_text);
        final EditText phoneNumberText = (EditText) rootView.findViewById(R.id.phone_number_text);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countryCode = countryCodeText.getText().toString();
                String phoneNumber = phoneNumberText.getText().toString();
                new FetchContactProfileTask().execute(countryCode, phoneNumber);
                //Intent intent = new Intent(getActivity(), Monitoring.class);
                //startActivity(intent);
                getActivity().finish();
            }
        });

        return rootView;
    }


    public long insertSupervisor(long userCloudID, String countryCode, String phoneNumber){
        Cursor cursor = getActivity().getContentResolver().query(TrackAppContract.ContactEntry.CONTENT_URI,
                new String[]{TrackAppContract.ContactEntry.TABLE_NAME + "." + TrackAppContract.ContactEntry._ID},
                TrackAppContract.ContactEntry.COLUMN_CLOUD_ID + " = ?",
                new String[]{Long.toString(userCloudID)},
                null);

        if( cursor != null && cursor.getCount() > 0 ){
            return cursor.getLong(0);
        }
        ContentValues contactValues = new ContentValues();
        contactValues.put(TrackAppContract.ContactEntry.COLUMN_CLOUD_ID, userCloudID);
        contactValues.put(TrackAppContract.ContactEntry.COLUMN_COUNTRY_CODE, countryCode);
        contactValues.put(TrackAppContract.ContactEntry.COLUMN_PHONE_NUMBER, phoneNumber);
        Uri insertedUri = getActivity().getContentResolver().insert(TrackAppContract.ContactEntry.CONTENT_URI,contactValues);

        long contactID = ContentUris.parseId(insertedUri);
        return contactID;
    }

    public long insertSupervisorProfile(long contactId, String name, String photo){
        Cursor cursor = getActivity().getContentResolver().query(TrackAppContract.ProfileEntry.CONTENT_URI,
                new String[]{TrackAppContract.ProfileEntry.TABLE_NAME + "." + TrackAppContract.ProfileEntry._ID},
                TrackAppContract.ProfileEntry.COLUMN_CONTACT_KEY + " = ?",
                new String[]{Long.toString(contactId)},
                null);

        if( cursor != null && cursor.getCount() > 0 ){
            ContentValues profileValues = new ContentValues();
            profileValues.put(TrackAppContract.ProfileEntry.COLUMN_IS_SUPERVISOR, 1);
            int updates = getActivity().getContentResolver().update(TrackAppContract.ProfileEntry.CONTENT_URI, profileValues,
                    TrackAppContract.ProfileEntry.COLUMN_CONTACT_KEY + " = ?",
                    new String[]{Long.toString(contactId)});
            return cursor.getLong(0);
        }

        ContentValues profileValues = new ContentValues();
        profileValues.put(TrackAppContract.ProfileEntry.COLUMN_NAME, name);
        profileValues.put(TrackAppContract.ProfileEntry.COLUMN_PHOTO, photo);
        profileValues.put(TrackAppContract.ProfileEntry.COLUMN_CONTACT_KEY, contactId);
        profileValues.put(TrackAppContract.ProfileEntry.COLUMN_IS_SUPERVISOR, 1);
        Uri insertedUri = getActivity().getContentResolver().insert(TrackAppContract.ProfileEntry.CONTENT_URI, profileValues);

        long profileID = ContentUris.parseId(insertedUri);
        return profileID;
    }
    public class FetchContactProfileTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String countryCode = params[0];
            String phoneNumber = params[1];
            Log.d("PHONE_NUMBER", phoneNumber );
            String BASE_URL = NetworkUtilities.BASE_DOMAIN + "users/phone/";
            String jsonstr = NetworkUtilities.getJSON(BASE_URL.concat(phoneNumber));
            Log.d("RESPONSE GET PHONE", jsonstr);

            long userID = 0;

            try {
                JSONObject jsonResponse = new JSONObject(jsonstr);
                userID = jsonResponse.getLong("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            long contactID = insertSupervisor(userID, countryCode, phoneNumber);

            if(userID > 0){
                BASE_URL = NetworkUtilities.BASE_DOMAIN + "users/";
                jsonstr = NetworkUtilities.getJSON(BASE_URL.concat(userID + "/profile"));

                String name = "Contact " + phoneNumber;
                String photo = "";
                try {
                    JSONObject jsonResponse = new JSONObject(jsonstr);
                    name = jsonResponse.getString("name");
                    photo = jsonResponse.getString("photo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                insertSupervisorProfile(contactID, name, photo);
            }else{
                insertSupervisorProfile(contactID, "Contact " + phoneNumber, "");
            }

            return null;
        }
    }
}
