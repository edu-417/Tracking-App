package com.example.eduardo.tabtest.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class DbUtilities {

    public static long insertContact(Context context, long contactCloudID, String countryCode, String phoneNumber){
        Cursor cursor = context.getContentResolver().query(TrackAppContract.ContactEntry.CONTENT_URI,
                new String[]{TrackAppContract.ContactEntry.TABLE_NAME + "." + TrackAppContract.ContactEntry._ID},
                TrackAppContract.ContactEntry.COLUMN_CLOUD_ID + " = ?",
                new String[]{Long.toString(contactCloudID)},
                null);

        if( cursor != null && cursor.getCount() > 0 ){
            cursor.moveToFirst();
            return cursor.getLong(0);
        }

        if(cursor != null)cursor.close();

        ContentValues contactValues = new ContentValues();
        contactValues.put(TrackAppContract.ContactEntry.COLUMN_CLOUD_ID, contactCloudID);
        contactValues.put(TrackAppContract.ContactEntry.COLUMN_COUNTRY_CODE, countryCode);
        contactValues.put(TrackAppContract.ContactEntry.COLUMN_PHONE_NUMBER, phoneNumber);
        Uri insertedUri = context.getContentResolver().insert(TrackAppContract.ContactEntry.CONTENT_URI,contactValues);

        long contactID = ContentUris.parseId(insertedUri);
        return contactID;
    }

    public static long insertContactProfile(Context context, long contactId, String name, String photo){

        Cursor cursor = context.getContentResolver().query(TrackAppContract.ProfileEntry.CONTENT_URI,
                new String[]{TrackAppContract.ProfileEntry.TABLE_NAME + "." + TrackAppContract.ProfileEntry._ID},
                TrackAppContract.ProfileEntry.COLUMN_CONTACT_KEY + " = ?",
                new String[]{Long.toString(contactId)},
                null);

        if( cursor != null && cursor.getCount() > 0 ){
            cursor.moveToFirst();
            return cursor.getLong(0);
        }

        if(cursor != null)cursor.close();

        ContentValues profileValues = new ContentValues();
        profileValues.put(TrackAppContract.ProfileEntry.COLUMN_NAME, name);
        profileValues.put(TrackAppContract.ProfileEntry.COLUMN_PHOTO, photo);
        profileValues.put(TrackAppContract.ProfileEntry.COLUMN_CONTACT_KEY, contactId);
        Uri insertedUri = context.getContentResolver().insert(TrackAppContract.ProfileEntry.CONTENT_URI, profileValues);

        long profileID = ContentUris.parseId(insertedUri);
        return profileID;
    }

    public static long insertChatMessage(Context context, long from, long to, String message, boolean isSend){
        ContentValues chatValues = new ContentValues();
        chatValues.put(TrackAppContract.ChatEntry.COLUMN_FROM, from);
        chatValues.put(TrackAppContract.ChatEntry.COLUMN_TO, to);
        chatValues.put(TrackAppContract.ChatEntry.COLUMN_CONTENT, message);
        chatValues.put(TrackAppContract.ChatEntry.COLUMN_IS_SEND, isSend);
        Uri insertedUri = context.getContentResolver().insert(TrackAppContract.ChatEntry.CONTENT_URI, chatValues);

        long chatID = ContentUris.parseId(insertedUri);
        return chatID;
    }

    public static long insertGroupChatMessage(Context context, long from, long to, String message, boolean isSend){
        ContentValues chatValues = new ContentValues();
        chatValues.put(TrackAppContract.GroupChatEntry.COLUMN_FROM, from);
        chatValues.put(TrackAppContract.GroupChatEntry.COLUMN_TO, to);
        chatValues.put(TrackAppContract.GroupChatEntry.COLUMN_CONTENT, message);
        chatValues.put(TrackAppContract.GroupChatEntry.COLUMN_IS_SEND, isSend);
        Uri insertedUri = context.getContentResolver().insert(TrackAppContract.GroupChatEntry.CONTENT_URI, chatValues);

        long chatID = ContentUris.parseId(insertedUri);
        return chatID;
    }

    public static long insertLocation(Context context, long contactID, long userCloudID, double longitude, double latitude ){
        ContentValues locationValues = new ContentValues();
        locationValues.put(TrackAppContract.LocationEntry.COLUMN_USER_CLOUD_ID, userCloudID);
        locationValues.put(TrackAppContract.LocationEntry.COLUMN_CONTACT_KEY, contactID);
        locationValues.put(TrackAppContract.LocationEntry.COLUMN_LATITUDE, latitude);
        locationValues.put(TrackAppContract.LocationEntry.COLUMN_LONGITUDE, longitude);
        Uri insertedUri = context.getContentResolver().insert(TrackAppContract.LocationEntry.CONTENT_URI, locationValues);

        long locationID = ContentUris.parseId(insertedUri);
        return locationID;
    }

    public static long insertLocation(Context context, long contactID, long userCloudID, double longitude, double latitude, String created ){
        ContentValues locationValues = new ContentValues();
        locationValues.put(TrackAppContract.LocationEntry.COLUMN_USER_CLOUD_ID, userCloudID);
        locationValues.put(TrackAppContract.LocationEntry.COLUMN_CONTACT_KEY, contactID);
        locationValues.put(TrackAppContract.LocationEntry.COLUMN_LATITUDE, latitude);
        locationValues.put(TrackAppContract.LocationEntry.COLUMN_LONGITUDE, longitude);
        locationValues.put(TrackAppContract.LocationEntry.COLUMN_CREATED, created);
        Uri insertedUri = context.getContentResolver().insert(TrackAppContract.LocationEntry.CONTENT_URI, locationValues);

        long locationID = ContentUris.parseId(insertedUri);
        return locationID;
    }

    public static long insertTmpLocation(Context context, double longitude, double latitude){
        ContentValues locationValues = new ContentValues();
        locationValues.put(TrackAppContract.TmpLocationEntry.COLUMN_LONGITUDE, longitude);
        locationValues.put(TrackAppContract.TmpLocationEntry.COLUMN_LATITUDE, latitude);
        Uri insertedUri = context.getContentResolver().insert(TrackAppContract.TmpLocationEntry.CONTENT_URI, locationValues);

        long locationID = ContentUris.parseId(insertedUri);
        return locationID;
    }

    public static long insertGroup(Context context, long groupCloudID, String name, String icon){
        Cursor cursor = context.getContentResolver().query(TrackAppContract.GroupEntry.CONTENT_URI,
                new String[]{TrackAppContract.GroupEntry.TABLE_NAME + "." + TrackAppContract.GroupEntry._ID},
                TrackAppContract.GroupEntry.COLUMN_CLOUD_ID + " = ?",
                new String[]{Long.toString(groupCloudID)},
                null);

        if( cursor != null && cursor.getCount() > 0 ){
            cursor.moveToFirst();
            return cursor.getLong(0);
        }

        if(cursor != null)cursor.close();

        ContentValues groupValues = new ContentValues();
        groupValues.put(TrackAppContract.GroupEntry.COLUMN_CLOUD_ID, groupCloudID);
        groupValues.put(TrackAppContract.GroupEntry.COLUMN_NAME, name);
        groupValues.put(TrackAppContract.GroupEntry.COLUMN_ICON, icon);
        Uri insertedUri = context.getContentResolver().insert(TrackAppContract.GroupEntry.CONTENT_URI, groupValues);

        long groupID = ContentUris.parseId(insertedUri);
        return groupID;
    }

    public static long insertGroupMember(Context context, long contactID, long groupID){
        Cursor cursor = context.getContentResolver().query(TrackAppContract.GroupMemberEntry.CONTENT_URI,
                new String[]{TrackAppContract.GroupMemberEntry.TABLE_NAME + "." + TrackAppContract.GroupMemberEntry._ID},
                TrackAppContract.GroupMemberEntry.COLUMN_CONTACT_KEY + " = ? AND " + TrackAppContract.GroupMemberEntry.COLUMN_GROUP_KEY + " = ?",
                new String[]{Long.toString(contactID), Long.toString(groupID)},
                null);

        if( cursor != null && cursor.getCount() > 0 ){
            cursor.moveToFirst();
            return cursor.getLong(0);
        }

        if(cursor != null)cursor.close();

        ContentValues groupContactValues = new ContentValues();
        groupContactValues.put(TrackAppContract.GroupMemberEntry.COLUMN_CONTACT_KEY, contactID);
        groupContactValues.put(TrackAppContract.GroupMemberEntry.COLUMN_GROUP_KEY, groupID);
        Uri insertedUri = context.getContentResolver().insert(TrackAppContract.GroupMemberEntry.CONTENT_URI, groupContactValues);

        long contactGroupID = ContentUris.parseId(insertedUri);
        return contactGroupID;
    }


    public static long insertSubscription(Context context, long subscriptionCloudID, long subscriberID){

        Cursor cursor = context.getContentResolver().query(TrackAppContract.SubscriptionEntry.CONTENT_URI,
                new String[]{TrackAppContract.SubscriptionEntry.TABLE_NAME + "." + TrackAppContract.SubscriptionEntry._ID},
                TrackAppContract.SubscriptionEntry.COLUMN_CLOUD_ID + " = ? ",
                new String[]{Long.toString(subscriptionCloudID)},
                null);

        if( cursor != null && cursor.getCount() > 0 ){
            cursor.moveToFirst();
            return cursor.getLong(0);
        }

        if(cursor != null)cursor.close();

        ContentValues subscriptionValues = new ContentValues();
        subscriptionValues.put(TrackAppContract.SubscriptionEntry.COLUMN_IS_ACTIVE, true);
        subscriptionValues.put(TrackAppContract.SubscriptionEntry.COLUMN_CLOUD_ID, subscriptionCloudID);
        subscriptionValues.put(TrackAppContract.SubscriptionEntry.COLUMN_SUBSCRIBER_KEY, subscriberID);
        Uri insertedUri = context.getContentResolver().insert(TrackAppContract.SubscriptionEntry.CONTENT_URI, subscriptionValues);

        long subscriptionID = ContentUris.parseId(insertedUri);
        return subscriptionID;
    }

    public static long insertSubscriptionMember(Context context, long contactID, long subscriptionID, boolean isSupervisor, boolean isAdministrator){

        Cursor cursor = context.getContentResolver().query(TrackAppContract.SubscriptionMemberEntry.CONTENT_URI,
                new String[]{TrackAppContract.SubscriptionMemberEntry.TABLE_NAME + "." + TrackAppContract.SubscriptionMemberEntry._ID},
                TrackAppContract.SubscriptionMemberEntry.COLUMN_CONTACT_KEY + " = ? AND " + TrackAppContract.SubscriptionMemberEntry.COLUMN_SUBSCRIPTION_KEY + " = ?",
                new String[]{Long.toString(contactID), Long.toString(subscriptionID)},
                null);

        if( cursor != null && cursor.getCount() > 0 ){
            cursor.moveToFirst();
            return cursor.getLong(0);
        }

        if(cursor != null)cursor.close();

        ContentValues subscriptionMemberValues = new ContentValues();
        subscriptionMemberValues.put(TrackAppContract.SubscriptionMemberEntry.COLUMN_CONTACT_KEY, contactID);
        subscriptionMemberValues.put(TrackAppContract.SubscriptionMemberEntry.COLUMN_SUBSCRIPTION_KEY, subscriptionID);
        subscriptionMemberValues.put(TrackAppContract.SubscriptionMemberEntry.COLUMN_IS_ADMINISTRATOR, isAdministrator);
        subscriptionMemberValues.put(TrackAppContract.SubscriptionMemberEntry.COLUMN_IS_SUPERVISOR, isSupervisor);
        Uri insertedUri = context.getContentResolver().insert(TrackAppContract.SubscriptionMemberEntry.CONTENT_URI, subscriptionMemberValues);

        long subscriptionMemberID = ContentUris.parseId(insertedUri);
        return subscriptionMemberID;
    }
}
