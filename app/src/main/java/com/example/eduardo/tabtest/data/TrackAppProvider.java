package com.example.eduardo.tabtest.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class TrackAppProvider extends ContentProvider {

    static final int CONTACT = 100;
    static final int PROFILE = 200;
    static final int GROUP = 300;
    static final int CHAT = 400;
    static  final int CHAT_FROM_TO = 401;
    static final int COUNTRY = 500;
    static final int LOCATION = 600;
    static final int LOCATION_USER_RECENT = 601;
    static final int GROUP_CHAT = 700;
    static final int GROUP_CHAT_TO = 701;
    static final int GROUP_MEMBER = 800;
    static final int TMP_LOCATION = 900;
    static final int SUBSCRIPTION = 1000;
    static final int SUBSCRIPTION_MEMBER = 1100;

    private static final String sMessageSelection =
            "( " +
                    TrackAppContract.ChatEntry.COLUMN_FROM + " = ? AND " +
                    TrackAppContract.ChatEntry.COLUMN_TO + " = ? " +
                    ")" +
                    " OR ( " +
                    TrackAppContract.ChatEntry.COLUMN_FROM + " = ? AND " +
                    TrackAppContract.ChatEntry.COLUMN_TO + " = ? " +
                    " )"

            ;

    private static final String sGroupMessageSelection =
                    TrackAppContract.GroupChatEntry.COLUMN_TO + " = ? ";

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TrackAppDbHelper mOpenHelper;

    private Cursor getChatFromTo(Uri uri, String[] projection, String sortOrder){

        long from = TrackAppContract.ChatEntry.getFromFromUri(uri);
        long to = TrackAppContract.ChatEntry.getToFromUri(uri);

        Log.d("CHAT QUERY", "from: " + from);
        Log.d("CHAT QUERY", "to: " + to);
        return mOpenHelper.getReadableDatabase().query(
                TrackAppContract.ChatEntry.TABLE_NAME,
                projection,
                sMessageSelection,
                new String[]{ Long.toString(from), Long.toString(to), Long.toString(to), Long.toString(from)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getGroupChatTo(Uri uri, String[] projection, String sortOrder){

        long to = TrackAppContract.GroupChatEntry.getToFromUri(uri);

        Log.d("GROUP CHAT QUERY", "to: " + to);
        return mOpenHelper.getReadableDatabase().query(
                TrackAppContract.GroupChatEntry.TABLE_NAME,
                projection,
                sGroupMessageSelection,
                new String[]{ Long.toString(to)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getLocationByContact(Uri uri, String[] projection, String sortOrder){
        return mOpenHelper.getReadableDatabase().query(
                TrackAppContract.LocationEntry.TABLE_NAME,
                projection,
                null,
                null,
                TrackAppContract.LocationEntry.COLUMN_CONTACT_KEY,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TrackAppContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, TrackAppContract.PATH_CONTACT, CONTACT);
        matcher.addURI(authority, TrackAppContract.PATH_PROFILE, PROFILE);
        matcher.addURI(authority, TrackAppContract.PATH_GROUP, GROUP);
        matcher.addURI(authority, TrackAppContract.PATH_CHAT, CHAT);
        matcher.addURI(authority, TrackAppContract.PATH_GROUP_CHAT, GROUP_CHAT);
        matcher.addURI(authority, TrackAppContract.PATH_COUNTRY, COUNTRY);
        matcher.addURI(authority, TrackAppContract.PATH_LOCATION, LOCATION);
        matcher.addURI(authority, TrackAppContract.PATH_TMP_LOCATION, TMP_LOCATION);
        matcher.addURI(authority, TrackAppContract.PATH_GROUP_MEMBER, GROUP_MEMBER);
        matcher.addURI(authority, TrackAppContract.PATH_SUBSCRIPTION, SUBSCRIPTION);
        matcher.addURI(authority, TrackAppContract.PATH_SUBSCRIPTION_MEMBER, SUBSCRIPTION_MEMBER);
        matcher.addURI(authority, TrackAppContract.PATH_CHAT + "/#/#", CHAT_FROM_TO);
        matcher.addURI(authority, TrackAppContract.PATH_GROUP_CHAT + "/#", GROUP_CHAT_TO);
        matcher.addURI(authority, TrackAppContract.PATH_LOCATION + "/RECENT", LOCATION_USER_RECENT);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new TrackAppDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "contact"
            case CONTACT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TrackAppContract.ContactEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "profile"
            case PROFILE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TrackAppContract.ProfileEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "group"
            case GROUP: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TrackAppContract.GroupEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "chat"
            case CHAT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TrackAppContract.ChatEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "country"
            case COUNTRY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TrackAppContract.CountryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location"
            case LOCATION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TrackAppContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location"
            case TMP_LOCATION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TrackAppContract.TmpLocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "group chat"
            case GROUP_CHAT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TrackAppContract.GroupChatEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "group member"
            case GROUP_MEMBER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TrackAppContract.GroupMemberEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "subscription"
            case SUBSCRIPTION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TrackAppContract.SubscriptionEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "subscription member"
            case SUBSCRIPTION_MEMBER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TrackAppContract.SubscriptionMemberEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case LOCATION_USER_RECENT:{
                retCursor = getLocationByContact(uri, projection, sortOrder);
                break;
            }

            case CHAT_FROM_TO: {
                retCursor = getChatFromTo(uri, projection, sortOrder);
                break;
            }

            case GROUP_CHAT_TO: {
                retCursor = getGroupChatTo(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case CONTACT:
                return TrackAppContract.ContactEntry.CONTENT_TYPE;

            case PROFILE:
                return TrackAppContract.ProfileEntry.CONTENT_TYPE;

            case GROUP:
                return TrackAppContract.GroupEntry.CONTENT_TYPE;

            case CHAT:
                return TrackAppContract.ChatEntry.CONTENT_TYPE;

            case COUNTRY:
                return TrackAppContract.CountryEntry.CONTENT_TYPE;

            case LOCATION:
                return TrackAppContract.LocationEntry.CONTENT_TYPE;

            case TMP_LOCATION:
                return TrackAppContract.TmpLocationEntry.CONTENT_TYPE;

            case GROUP_CHAT:
                return TrackAppContract.GroupChatEntry.CONTENT_TYPE;

            case GROUP_MEMBER:
                return TrackAppContract.GroupMemberEntry.CONTENT_TYPE;

            case SUBSCRIPTION:
                return TrackAppContract.SubscriptionEntry.CONTENT_TYPE;

            case SUBSCRIPTION_MEMBER:
                return TrackAppContract.SubscriptionMemberEntry.CONTENT_TYPE;

            case CHAT_FROM_TO:
                return TrackAppContract.ChatEntry.CONTENT_TYPE;

            case GROUP_CHAT_TO:
                return TrackAppContract.GroupChatEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case CONTACT: {
                long _id = db.insert(TrackAppContract.ContactEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TrackAppContract.ContactEntry.buildContactUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PROFILE: {
                long _id = db.insert(TrackAppContract.ProfileEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TrackAppContract.ProfileEntry.buildProfileUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case GROUP: {
                long _id = db.insert(TrackAppContract.GroupEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TrackAppContract.GroupEntry.buildGroupUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CHAT: {
                long _id = db.insert(TrackAppContract.ChatEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TrackAppContract.ChatEntry.buildChatUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case COUNTRY: {
                long _id = db.insert(TrackAppContract.CountryEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TrackAppContract.CountryEntry.buildCountryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LOCATION: {
                long _id = db.insert(TrackAppContract.LocationEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TrackAppContract.LocationEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TMP_LOCATION: {
                long _id = db.insert(TrackAppContract.TmpLocationEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TrackAppContract.TmpLocationEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case GROUP_CHAT: {
                long _id = db.insert(TrackAppContract.GroupChatEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TrackAppContract.GroupChatEntry.buildChatUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case GROUP_MEMBER: {
                long _id = db.insert(TrackAppContract.GroupMemberEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TrackAppContract.GroupMemberEntry.buildGroupMemberUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SUBSCRIPTION: {
                long _id = db.insert(TrackAppContract.SubscriptionEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TrackAppContract.SubscriptionEntry.buildSubscriptionUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SUBSCRIPTION_MEMBER: {
                long _id = db.insert(TrackAppContract.SubscriptionMemberEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TrackAppContract.SubscriptionMemberEntry.buildSubscriptionMemberUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case CONTACT:
                rowsDeleted = db.delete(
                        TrackAppContract.ContactEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PROFILE:
                rowsDeleted = db.delete(
                        TrackAppContract.ProfileEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case GROUP:
                rowsDeleted = db.delete(
                        TrackAppContract.GroupEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CHAT:
                rowsDeleted = db.delete(
                        TrackAppContract.ChatEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COUNTRY:
                rowsDeleted = db.delete(
                        TrackAppContract.CountryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATION:
                rowsDeleted = db.delete(
                        TrackAppContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TMP_LOCATION:
                rowsDeleted = db.delete(
                        TrackAppContract.TmpLocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case GROUP_CHAT:
                rowsDeleted = db.delete(
                        TrackAppContract.GroupChatEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case GROUP_MEMBER:
                rowsDeleted = db.delete(
                        TrackAppContract.GroupMemberEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SUBSCRIPTION:
                rowsDeleted = db.delete(
                        TrackAppContract.SubscriptionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SUBSCRIPTION_MEMBER:
                rowsDeleted = db.delete(
                        TrackAppContract.SubscriptionMemberEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case CONTACT:
                rowsUpdated = db.update(TrackAppContract.ContactEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PROFILE:
                rowsUpdated = db.update(TrackAppContract.ProfileEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case GROUP:
                rowsUpdated = db.update(TrackAppContract.GroupEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CHAT:
                rowsUpdated = db.update(TrackAppContract.ChatEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case COUNTRY:
                rowsUpdated = db.update(TrackAppContract.CountryEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case LOCATION:
                rowsUpdated = db.update(TrackAppContract.LocationEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TMP_LOCATION:
                rowsUpdated = db.update(TrackAppContract.TmpLocationEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case GROUP_CHAT:
                rowsUpdated = db.update(TrackAppContract.GroupChatEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case GROUP_MEMBER:
                rowsUpdated = db.update(TrackAppContract.GroupMemberEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case SUBSCRIPTION:
                rowsUpdated = db.update(TrackAppContract.SubscriptionEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case SUBSCRIPTION_MEMBER:
                rowsUpdated = db.update(TrackAppContract.SubscriptionMemberEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}
