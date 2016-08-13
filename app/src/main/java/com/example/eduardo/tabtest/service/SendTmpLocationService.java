package com.example.eduardo.tabtest.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Pair;

import com.example.eduardo.tabtest.data.TrackAppContract;
import com.example.eduardo.tabtest.utils.NetworkUtilities;
import com.example.eduardo.tabtest.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.Vector;


public class SendTmpLocationService extends IntentService {

    static final String LOCATION_COLUMNS[] = {
            TrackAppContract.TmpLocationEntry._ID,
            TrackAppContract.TmpLocationEntry.COLUMN_LONGITUDE,
            TrackAppContract.TmpLocationEntry.COLUMN_LATITUDE,
            TrackAppContract.TmpLocationEntry.COLUMN_CREATED
    };

    static final int COLUMN_LOCATION_ID = 0;
    static final int COLUMN_LOCATION_LONGITUDE = 1;
    static final int COLUMN_LOCATION_LATITUDE = 2;
    static final int COLUMN_LOCATION_CREATED = 3;

    public SendTmpLocationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Cursor cursor = getContentResolver().query(TrackAppContract.TmpLocationEntry.CONTENT_URI,
                LOCATION_COLUMNS,
                null,
                null,
                TrackAppContract.TmpLocationEntry.COLUMN_CREATED + " ASC"
        );

        if(cursor != null){

            long myID = Utilities.getMyID(this);
            String BASE_URL_POST = NetworkUtilities.BASE_DOMAIN + "locations/";

            Vector<Long> needDelete = new Vector<Long>();

            while ( cursor.moveToNext() ) {
                double longitude = cursor.getDouble(COLUMN_LOCATION_LONGITUDE);
                double latitude = cursor.getDouble(COLUMN_LOCATION_LATITUDE);
                long id = cursor.getLong(COLUMN_LOCATION_ID);
                String created = cursor.getString(COLUMN_LOCATION_CREATED);



                JSONObject json = new JSONObject();
                try {
                    json.put("longitude", longitude);
                    json.put("latitude", latitude);
                    json.put("user", Long.toString(myID));
                    json.put("created", created);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if( NetworkUtilities.isOnline(this) ){

                    Pair<Integer, String> result = NetworkUtilities.postJSON(BASE_URL_POST, json);
                    int httpResult = -1;

                    if(result != null){
                        httpResult = result.first;
                    }

                    if( httpResult == HttpURLConnection.HTTP_CREATED ){
                        needDelete.add(id);
                    }
                }
            }

            Iterator<Long> iterator = needDelete.iterator();
            while( iterator.hasNext() ){
                long element = iterator.next();
                getContentResolver().delete(TrackAppContract.TmpLocationEntry.CONTENT_URI,
                        TrackAppContract.TmpLocationEntry._ID + " = ? ",
                        new String[]{Long.toString(element)});
            }


        }
    }
}
