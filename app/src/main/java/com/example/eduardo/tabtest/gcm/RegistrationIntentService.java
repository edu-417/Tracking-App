package com.example.eduardo.tabtest.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.eduardo.tabtest.MainActivity;
import com.example.eduardo.tabtest.R;
import com.example.eduardo.tabtest.RegisterActivityFragment;
import com.example.eduardo.tabtest.utils.NetworkUtilities;
import com.example.eduardo.tabtest.utils.Utilities;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by EDUARDO on 18/07/2016.
 */
public class RegistrationIntentService extends IntentService{

    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                InstanceID instanceID = InstanceID.getInstance(this);

                // TODO: gcm_default sender ID comes from the API console
                String senderId = getString(R.string.gcm_defaultSenderId);
                if ( senderId.length() != 0 ) {
                    String token = instanceID.getToken(senderId,
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    sendRegistrationToServer(token);
                    sharedPreferences.edit().putString(MainActivity.TOKEN, token).apply();
                }

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                sharedPreferences.edit().putBoolean(MainActivity.SENT_TOKEN_TO_SERVER, true).apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);

            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(MainActivity.SENT_TOKEN_TO_SERVER, false).apply();
        }
    }

    /**
     * Normally, you would want to persist the registration to third-party servers. Because we do
     * not have a server, and are faking it with a website, you'll want to log the token instead.
     * That way you can see the value in logcat, and note it for future use in the website.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        Log.i(TAG, "GCM Registration Token: " + token);

        JSONObject json = new JSONObject();
        try {
            json.put("registration_id", token);
            json.put("active", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String BASE_URL_POST = NetworkUtilities.BASE_DOMAIN + "device/gcms/";
        NetworkUtilities.postJSON(BASE_URL_POST, json);


        long myID = Utilities.getMyID(this);

        json = new JSONObject();
        try {
            json.put("token", token);
            json.put("user", myID);
        } catch (JSONException e) {
            e.printStackTrace();

        }

        BASE_URL_POST = NetworkUtilities.BASE_DOMAIN + "gcms/";
        NetworkUtilities.postJSON(BASE_URL_POST, json);
    }
}
