package com.example.eduardo.tabtest;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eduardo.tabtest.data.DbUtilities;
import com.example.eduardo.tabtest.utils.NetworkUtilities;
import com.example.eduardo.tabtest.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionFragment extends Fragment {

    private static final String TAG = SubscriptionFragment.class.getSimpleName();

    public static final String IS_SUBSCRIBE_PREF = "IS_SUBSCRIBE_PREF";
    public static final String IS_ADMIN_PREF = "IS_ADMIN_PREF";
    public static final String IS_SUPERVISOR_PREF = "IS_SUPERVISOR_PREF";

    public SubscriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_subscription, container, false);

        Button button = (Button) rootView.findViewById(R.id.subscribe_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean subscribe = Utilities.alreadySubscribe(getActivity());
                if( subscribe ){
                    Toast.makeText(getActivity(), "You already subscribed", Toast.LENGTH_LONG).show();
                    return;
                }

                if( NetworkUtilities.isOnline( getActivity() ) ){
                    new SubscribeTask().execute();
                }
                else{
                    Toast.makeText(getActivity(), R.string.no_network_connection, Toast.LENGTH_LONG).show();
                }

            }
        });
        return rootView;

    }

    public class SubscribeTask extends AsyncTask<String, Void, Long> {

        @Override
        protected Long doInBackground(String... params) {

            Log.d(TAG, "Creating subscription");

            long subscriberCloudID = Utilities.getMyID(getActivity());

            JSONObject json = new JSONObject();
            try {
                json.put("subscriber", subscriberCloudID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String BASE_URL_POST = NetworkUtilities.BASE_DOMAIN + "subscriptions/";
            String jsonStr = "";
            Pair<Integer, String> result = NetworkUtilities.postJSON(BASE_URL_POST, json);
            if(result != null){
                jsonStr = result.second;
            }


            Log.d(TAG, "Subscribing as member");

            long subscriptionCloudID = 0;

            if(jsonStr != null){
                try {
                    json = new JSONObject(jsonStr);
                    subscriptionCloudID = json.getLong("id");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                json = new JSONObject();
                try {
                    json.put("user", subscriberCloudID );
                    json.put("subscription", subscriptionCloudID);
                    json.put("is_administrator", true );
                    json.put("is_supervisor", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                BASE_URL_POST = NetworkUtilities.BASE_DOMAIN + "subscriptions/members/";
                NetworkUtilities.postJSON(BASE_URL_POST, json);
            }

            if(subscriberCloudID > 0){
                long subscriptionID = DbUtilities.insertSubscription(getActivity(), subscriptionCloudID, 0);
                DbUtilities.insertSubscriptionMember(getActivity(), 0, subscriptionID, true, true);
            }

            return subscriptionCloudID;
        }

        @Override
        protected void onPostExecute(Long subscriptionCloudID) {
            if(subscriptionCloudID > 0 ){
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(IS_ADMIN_PREF, true);
                editor.putBoolean(IS_SUBSCRIBE_PREF, true);
                editor.apply();
                Toast.makeText(getActivity(), "Subscribe success", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getActivity(), "Error in subscribe", Toast.LENGTH_LONG).show();
            }
        }
    }

}
