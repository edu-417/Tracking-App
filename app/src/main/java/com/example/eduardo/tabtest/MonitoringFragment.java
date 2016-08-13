package com.example.eduardo.tabtest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eduardo.tabtest.data.DbUtilities;
import com.example.eduardo.tabtest.utils.NetworkUtilities;
import com.example.eduardo.tabtest.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 */
public class MonitoringFragment extends Fragment {

    private static final String TAG = MonitoringFragment.class.getSimpleName();

    public MonitoringFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_monitoring, container, false);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.list_monitoring_item, R.id.monitoring_item_title, getResources().getStringArray(R.array.monitoring_days_array));

        final ListView list = (ListView)rootView.findViewById(R.id.list_monitoring);
        list.setAdapter(adapter);

        CheckBox checkbox = (CheckBox)rootView.findViewById(R.id.monitoring_all_hours_check);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    int n = list.getCount();

                    for(int i = 0; i < n; ++i ){
                        View view = list.getChildAt(i);
                        EditText startText = (EditText)view.findViewById(R.id.monitoring_item_start_time);
                        startText.setText("0:00");
                        EditText endText = (EditText)view.findViewById(R.id.monitoring_item_end_time);
                        endText.setText("24:00");
                    }
                }else{
                    int n = list.getCount();

                    for(int i = 0; i < n; ++i ){
                        View view = list.getChildAt(i);
                        EditText startText = (EditText)view.findViewById(R.id.monitoring_item_start_time);
                        startText.setText("");
                        EditText endText = (EditText)view.findViewById(R.id.monitoring_item_end_time);
                        endText.setText("");
                    }
                }
            }
        });


        Button button = (Button) rootView.findViewById(R.id.continue_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getActivity().getIntent();
                String countryCode = intent.getStringExtra(NewUserFragment.EXTRA_CONTACT_COUNTRY_CODE);
                String phoneNumber = intent.getStringExtra(NewUserFragment.EXTRA_CONTACT_PHONE_NUMBER);
                boolean isSupervisor = intent.getBooleanExtra(NewUserFragment.EXTRA_CONTACT_IS_SUPERVISOR, false);
                long subscriptionID = intent.getLongExtra(SelectSubscriptionFragment.EXTRA_SUBSCRIPTION_ID, 0);
                long subscriptionCloudID = intent.getLongExtra(SelectSubscriptionFragment.EXTRA_SUBSCRIPTION_CLOUD_ID, 0);

                int n = list.getCount();

                for(int i = 0; i < n; ++i ){
                    View view = list.getChildAt(i);
                    EditText startText = (EditText)view.findViewById(R.id.monitoring_item_start_time);
                    EditText endText = (EditText)view.findViewById(R.id.monitoring_item_end_time);
                    String startTime = startText.getText().toString();
                    String endTime = endText.getText().toString();
                }

                if( NetworkUtilities.isOnline(getActivity())){
                    new RegisterContactTask().execute(countryCode, phoneNumber, Boolean.toString(isSupervisor), Long.toString(subscriptionID), Long.toString(subscriptionCloudID));
                }else{
                    Toast.makeText(getActivity(), R.string.no_network_connection, Toast.LENGTH_LONG).show();
                }
            }
        });
        return rootView;
    }


    public long getContactCloudID(String phoneNumber){
        String BASE_URL = NetworkUtilities.BASE_DOMAIN + "users/phone/";
        String jsonStr = NetworkUtilities.getJSON(BASE_URL.concat(phoneNumber));
        Log.d(TAG, "GET USER JSON : " + jsonStr);

        long userID = 0;

        try {
            JSONObject jsonResponse = new JSONObject(jsonStr);
            userID = jsonResponse.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userID;
    }

    public class RegisterContactTask extends AsyncTask<String, Void, Long> {

        @Override
        protected Long doInBackground(String... params) {
            String countryCode = params[0];
            String phoneNumber = params[1];
            boolean isSupervisor = Boolean.parseBoolean(params[2]);
            long subscriptionID = Long.parseLong(params[3]);
            long subscriptionCloudID = Long.parseLong(params[4]);

            long contactCloudID = getContactCloudID(phoneNumber);
            long contactID = 0;

            if(contactCloudID > 0){
                contactID = DbUtilities.insertContact(getActivity(), contactCloudID, countryCode, phoneNumber);
                String BASE_URL = NetworkUtilities.BASE_DOMAIN + "users/";
                String jsonStr = NetworkUtilities.getJSON(BASE_URL.concat(contactCloudID + "/profile"));

                String token = Utilities.getMyToken(getActivity());

                try {
                    Utilities.subscribeTopics(getActivity(), token, new String[]{"Location-User-" + contactCloudID});
                    Log.i(TAG, "Subscribe to location user " + contactCloudID);
                } catch (IOException e) {
                    Log.d(TAG, "Error subscribing to location user" + contactCloudID);
                    e.printStackTrace();
                }

                String name = "Contact " + phoneNumber;
                String photo = "";

                if( jsonStr != null ){
                    try {
                        JSONObject jsonResponse = new JSONObject(jsonStr);
                        name = jsonResponse.getString("name");
                        photo = jsonResponse.getString("photo");
                    } catch (JSONException e) {
                        Log.d(TAG, "Json ERROR!!!");
                        e.printStackTrace();
                    }

                    DbUtilities.insertContactProfile(getActivity(), contactID, name, photo);
                }

                JSONObject json = new JSONObject();
                try {
                    json.put("user", contactCloudID );
                    json.put("subscription", subscriptionCloudID);
                    json.put("is_administrator", false );
                    json.put("is_supervisor", isSupervisor);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String BASE_URL_POST = NetworkUtilities.BASE_DOMAIN + "subscriptions/members/";
                NetworkUtilities.postJSON(BASE_URL_POST, json);
                DbUtilities.insertSubscriptionMember(getActivity(), 0, subscriptionID, true, true);
                sendInvitation(contactCloudID, subscriptionCloudID);
            }



            return contactID;
        }

        @Override
        protected void onPostExecute(Long contactID) {
            if(contactID > 0){
                Toast.makeText(getActivity(), "Contact registered", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
            else{
                Toast.makeText(getActivity(), "Error registering contact", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String sendInvitation(long contactCloudID, long subscriptionCloudID){
        String BASE_URL = NetworkUtilities.BASE_DOMAIN + "member/invitations/";

        JSONObject json = new JSONObject();


        try {
            long myID = Utilities.getMyID(getActivity());
            json.put("ffrom", myID);
            json.put("to", contactCloudID);
            json.put("subscription", subscriptionCloudID);
            json.put("content", "El contacto " + myID + " solicita permiso para geolocalizarlo.");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonStr = "";
        Pair<Integer, String> result = NetworkUtilities.postJSON( BASE_URL, json );
        if(result != null){
            jsonStr = result.second;
        }

        return jsonStr;
    }
}
