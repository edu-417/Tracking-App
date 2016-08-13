package com.example.eduardo.tabtest.service;

import android.app.IntentService;
import android.content.Intent;

import com.example.eduardo.tabtest.network.NetworkReceiver;
import com.example.eduardo.tabtest.utils.NetworkUtilities;
import com.example.eduardo.tabtest.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by EDUARDO on 01/08/2016.
 */
public class SendLocationService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SendLocationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long myID = Utilities.getMyID(this);
        double longitude = intent.getDoubleExtra(NetworkReceiver.EXTRA_LONGITUDE, 0.0);
        double latitude = intent.getDoubleExtra(NetworkReceiver.EXTRA_LATITUDE, 0.0);

        JSONObject json = new JSONObject();
        try {
            json.put("longitude", longitude);
            json.put("latitude", latitude);
            json.put("user", Long.toString(myID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String BASE_URL_POST = NetworkUtilities.BASE_DOMAIN + "locations/";
        NetworkUtilities.postJSON(BASE_URL_POST, json);
    }
}
