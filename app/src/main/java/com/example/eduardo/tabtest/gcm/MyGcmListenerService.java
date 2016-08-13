package com.example.eduardo.tabtest.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.eduardo.tabtest.MainActivity;
import com.example.eduardo.tabtest.R;
import com.example.eduardo.tabtest.data.DbUtilities;
import com.example.eduardo.tabtest.data.TrackAppContract;
import com.example.eduardo.tabtest.utils.Utilities;
import com.google.android.gms.gcm.GcmListenerService;

import java.io.IOException;


public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    private static final String EXTRA_THEME = "theme";
    private static final String EXTRA_CONTENT = "content";
    private static final String EXTRA_CREATED = "created";
    private static final String EXTRA_FROM_ID = "from_id";
    private static final String EXTRA_FROM_NAME = "from_name";
    private static final String EXTRA_FROM_PHOTO = "from_photo";
    private static final String EXTRA_FROM_COUNTRY_CODE = "from_country_code";
    private static final String EXTRA_FROM_PHONE_NUMBER = "from_phone_number";
    private static final String EXTRA_GROUP_ID = "group_id";
    private static final String EXTRA_GROUP_NAME = "group_name";
    private static final String EXTRA_GROUP_ICON = "group_icon";

    private static final String EXTRA_SUBSCRIPTION_ID = "subscription_id";
    private static final String EXTRA_SUBSCRIBER_ID = "subscriber_id";
    private static final String EXTRA_SUBSCRIPTION_IS_ACTIVE = "subscription_is_active";



    private static final String EXTRA_LONGITUDE = "longitude";
    private static final String EXTRA_LATITUDE = "latitude";

    public static final int NOTIFICATION_ID = 1;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */

    @Override
    public void onMessageReceived(String from, Bundle data) {
        // Time to unparcel the bundle!
        Log.i(TAG, "Message from: " + from);
        if (!data.isEmpty()) {
            String locationTopic = "/topics/Location-User-";
            if( from.startsWith(locationTopic) ){
                long userCloudID = Long.parseLong( from.substring(locationTopic.length()) );
                receiveLocationFromContact(userCloudID, data);
                return;
            }

            String groupTopic = "/topics/Group-";
            if( from.startsWith(groupTopic)){
                long groupCloudID = Long.parseLong( from.substring(groupTopic.length()) );
                receiveMessageFromGroup(groupCloudID, data);
                return;
            }
            // TODO: gcm_default sender ID comes from the API console
            String senderId = getString(R.string.gcm_defaultSenderId);
            if (senderId.length() == 0) {
                Toast.makeText(this, "SenderID string needs to be set", Toast.LENGTH_LONG).show();
            }
            // Not a bad idea to check that the message is coming from your server.
            if ((senderId).equals(from)) {
                // Process message and then post a notification of the received message.

                String theme = data.getString(EXTRA_THEME);

                if(theme == null){
                    Log.i(TAG, "No Theme. Received: " + data.toString());
                    return;
                }

                if( theme.equals("message") ){
                    long contactCloudID = Long.parseLong(data.getString(EXTRA_FROM_ID));
                    receiveMessageFromContact(contactCloudID, data);
                }

                if(theme.equals("group_invitation")){
                    long groupCloudID = Long.parseLong(data.getString(EXTRA_GROUP_ID));
                    receiveInvitationFromGroup(groupCloudID, data);
                }

                if(theme.equals("member_invitation")){
                    long subscriptionCloudID = Long.parseLong(data.getString(EXTRA_SUBSCRIPTION_ID));
                    receiveInvitationFromSubscription(subscriptionCloudID, data);
                }

            }
            Log.i(TAG, "Received: " + data.toString());
        }
    }

    public void receiveLocationFromContact(long contactCloudID, Bundle data){
        Cursor cursor = getContentResolver().query(TrackAppContract.ContactEntry.CONTENT_URI,
                new String[]{TrackAppContract.ContactEntry.TABLE_NAME + "." + TrackAppContract.ContactEntry._ID},
                TrackAppContract.ContactEntry.COLUMN_CLOUD_ID + " = ?",
                new String[]{Long.toString(contactCloudID)},
                null);

        double longitude = Double.parseDouble(data.getString(EXTRA_LONGITUDE));
        double latitude = Double.parseDouble(data.getString(EXTRA_LATITUDE));
        String created = data.getString(EXTRA_CREATED);

        Log.i(TAG, "cloudID: " + contactCloudID + ", longitude: " + longitude + ", latitude: " + latitude );

        if(cursor != null){
            if(cursor.moveToFirst()){
                long contactID = cursor.getLong(0);

                Log.i(TAG, "contactID: " + contactID );
                DbUtilities.insertLocation(this, contactID, contactCloudID, longitude, latitude, created );
            }
            cursor.close();
        }

        Log.i(TAG, "Received: " + data.toString());
    }

    public void receiveMessageFromContact(long contactCloudID, Bundle data){
        String content = data.getString(EXTRA_CONTENT);

        sendNotification(content, "New Message");

        Cursor cursor = getContentResolver().query(TrackAppContract.ContactEntry.CONTENT_URI,
                new String[]{TrackAppContract.ContactEntry.TABLE_NAME + "." + TrackAppContract.ContactEntry._ID},
                TrackAppContract.ContactEntry.COLUMN_CLOUD_ID + " = ?",
                new String[]{Long.toString(contactCloudID)},
                null);

        if(cursor != null){
            cursor.moveToFirst();
            long fromID = cursor.getLong(0);
            DbUtilities.insertChatMessage(this, fromID, 0, content, false);
            cursor.close();
        }
    }

    public void receiveInvitationFromGroup(long groupCloudID, Bundle data){
        Log.i("GROUP INVITATION", Long.toString(groupCloudID) );
        long contactCloudID = Long.parseLong(data.getString(EXTRA_FROM_ID));
        String content = data.getString(EXTRA_CONTENT);
        String groupName = data.getString(EXTRA_GROUP_NAME);
        String icon = data.getString(EXTRA_GROUP_ICON);
        String profileName = data.getString(EXTRA_FROM_NAME);
        String profilePhoto = data.getString(EXTRA_FROM_PHOTO);
        String phoneNumber = data.getString(EXTRA_FROM_PHONE_NUMBER);
        String countryCode = data.getString(EXTRA_FROM_COUNTRY_CODE);

        DbUtilities.insertGroup(this, groupCloudID, groupName, icon);
        long contactID = DbUtilities.insertContact(this, contactCloudID, countryCode,phoneNumber);
        DbUtilities.insertContactProfile(this, contactID, profileName, profilePhoto);
        String token = Utilities.getMyToken(this);

        try {
            Utilities.subscribeTopics(this, token, new String[]{"Group-" + groupCloudID});
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendNotification(content, "Group Invitation");
    }

    public void receiveInvitationFromSubscription(long subscriptionCloudID, Bundle data){
        Log.i("MEMBER INVITATION", Long.toString(subscriptionCloudID) );
        long contactCloudID = Long.parseLong(data.getString(EXTRA_FROM_ID));
        String content = data.getString(EXTRA_CONTENT);
        String profileName = data.getString(EXTRA_FROM_NAME);
        String profilePhoto = data.getString(EXTRA_FROM_PHOTO);
        String phoneNumber = data.getString(EXTRA_FROM_PHONE_NUMBER);
        String countryCode = data.getString(EXTRA_FROM_COUNTRY_CODE);

        long contactID = DbUtilities.insertContact(this, contactCloudID, countryCode,phoneNumber);
        DbUtilities.insertContactProfile(this, contactID, profileName, profilePhoto);
        String token = Utilities.getMyToken(this);
        //DbUtilities.insertSubscription(this, subscriptionCloudID, contactID);

        sendNotification(content, "Invitation");
    }

    public void receiveMessageFromGroup(long groupCloudID, Bundle data){
        long from = Long.parseLong( data.getString(EXTRA_FROM_ID) );
        String content = data.getString(EXTRA_CONTENT);

        Log.i(TAG, "from: " + from + ". content: " + content);

        long fromID = 0;

        long myID = Utilities.getMyID(this);

        if( from != myID ){

            sendNotification(content, "Group Message");

            Cursor cursor = getContentResolver().query(TrackAppContract.ContactEntry.CONTENT_URI,
                    new String[]{TrackAppContract.ContactEntry.TABLE_NAME + "." + TrackAppContract.ContactEntry._ID},
                    TrackAppContract.ContactEntry.COLUMN_CLOUD_ID + " = ?",
                    new String[]{Long.toString(from)},
                    null);

            if(cursor != null){
                if(cursor.moveToFirst()){
                    fromID = cursor.getLong(0);
                }

                cursor.close();
            }

            Cursor groupCursor = getContentResolver().query(TrackAppContract.GroupEntry.CONTENT_URI,
                    new String[]{TrackAppContract.GroupEntry.TABLE_NAME + "." + TrackAppContract.GroupEntry._ID},
                    TrackAppContract.GroupEntry.COLUMN_CLOUD_ID + " = ?",
                    new String[]{Long.toString(groupCloudID)},
                    null);

            if(groupCursor != null){
                if(groupCursor.moveToFirst()){
                    long toID = groupCursor.getLong(0);
                    DbUtilities.insertGroupChatMessage(this, fromID, toID, content, false);
                }
                groupCursor.close();
            }
        }

        Log.i(TAG, "Received: " + data.toString());
    }

    /**
     *  Put the message into a notification and post it.
     *  This is just one simple example of what you might choose to do with a GCM message.
     *
     * @param message The alert message to be posted.
     */
    private void sendNotification(String message, String title) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        // Notifications using both a large and a small icon (which yours should!) need the large
        // icon as a bitmap. So we need to create that here from the resource ID, and pass the
        // object along in our notification builder. Generally, you want to use the app icon as the
        // small icon, so that users understand what app is triggering this notification.
        //Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.art_storm);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.in_message_bg)
                        //.setLargeIcon(largeIcon)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
