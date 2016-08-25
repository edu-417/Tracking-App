package com.example.eduardo.tabtest.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eduardo.tabtest.MainActivity;
import com.example.eduardo.tabtest.RegisterActivityFragment;
import com.example.eduardo.tabtest.SubscriptionFragment;
import com.google.android.gms.gcm.GcmPubSub;

import java.io.IOException;


public class Utilities {

    public static void subscribeTopics(Context context, String token, String[] topics) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(context);
        for (String topic : topics) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

    public static void unsubscribeTopics(Context context, String token, String[] topics) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(context);
        for (String topic : topics) {
            pubSub.unsubscribe(token, "/topics/" + topic);
        }
    }

    public static String getMyToken(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(MainActivity.TOKEN,"No token");
    }

    public static long getMyID(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(RegisterActivityFragment.PREFS_NAME, 0);
        return sharedPreferences.getLong(RegisterActivityFragment.USER_ID_PREF, 0);
    }

    public static boolean alreadySubscribe(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(SubscriptionFragment.IS_SUBSCRIBE_PREF, false);
    }

    public static boolean haveAdminPrivileges(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(SubscriptionFragment.IS_ADMIN_PREF, false);
    }

    public static boolean haveSupervisorPrivileges(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(SubscriptionFragment.IS_SUPERVISOR_PREF, false);
    }

    private static final int RESULT_LOAD_IMG = 1;

    public static void putImageInView(Context context, int requestCode, int resultCode, Intent data, ImageView imageView){
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = context.getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                if( cursor != null && cursor.moveToFirst() ){
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodeString = cursor.getString(columnIndex);
                    cursor.close();
                    // Set the Image in ImageView after decoding the String
                    imageView.setImageBitmap(BitmapFactory
                            .decodeFile(imgDecodeString));
                }

            } else {
                Toast.makeText(context, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }
}
