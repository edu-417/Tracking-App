package com.example.eduardo.tabtest.network;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.example.eduardo.tabtest.data.TrackAppContract;
import com.example.eduardo.tabtest.service.SendLocationService;
import com.example.eduardo.tabtest.utils.NetworkUtilities;


public class NetworkReceiver extends BroadcastReceiver {

    public static final String EXTRA_LONGITUDE = "longitude";
    public static final String EXTRA_LATITUDE = "latitude";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(NetworkUtilities.isOnline(context)){
            Intent sendTmpLocationIntent = new Intent(context, SendLocationService.class);
            context.startService(sendTmpLocationIntent);
        }
    }
}

