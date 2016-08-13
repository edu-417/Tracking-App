package com.example.eduardo.tabtest.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class TrackAppDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "trackapp.db";

    public TrackAppDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_COUNTRY_TABLE = "CREATE TABLE " + TrackAppContract.CountryEntry.TABLE_NAME + " (" +
                TrackAppContract.CountryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrackAppContract.CountryEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                TrackAppContract.CountryEntry.COLUMN_COUNTRY_CODE + " TEXT NOT NULL " +
                " );";

        Log.d("CREATE DB", SQL_CREATE_COUNTRY_TABLE);

        final String SQL_CREATE_CONTACT_TABLE = "CREATE TABLE " + TrackAppContract.ContactEntry.TABLE_NAME + " (" +
                TrackAppContract.ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrackAppContract.ContactEntry.COLUMN_CLOUD_ID + " INTEGER UNIQUE NOT NULL, " +
                TrackAppContract.ContactEntry.COLUMN_COUNTRY_CODE + " TEXT NOT NULL, " +
                TrackAppContract.ContactEntry.COLUMN_PHONE_NUMBER + " TEXT NOT NULL " +
                " );";

        Log.d("CREATE DB", SQL_CREATE_CONTACT_TABLE);

        final String SQL_CREATE_PROFILE_TABLE = "CREATE TABLE " + TrackAppContract.ProfileEntry.TABLE_NAME + " (" +
                TrackAppContract.ProfileEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrackAppContract.ProfileEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                TrackAppContract.ProfileEntry.COLUMN_PHOTO + " TEXT NOT NULL, " +
                TrackAppContract.ProfileEntry.COLUMN_CONTACT_KEY + " INTEGER NOT NULL, " +
                TrackAppContract.ProfileEntry.COLUMN_IS_SUPERVISOR + " INTEGER DEFAULT 0 NOT NULL, " +
                " FOREIGN KEY (" + TrackAppContract.ProfileEntry.COLUMN_CONTACT_KEY + ") REFERENCES " +
                TrackAppContract.ContactEntry.TABLE_NAME + " (" + TrackAppContract.ContactEntry._ID + ") " +
                " );";

        Log.d("CREATE DB", SQL_CREATE_PROFILE_TABLE);

        final String SQL_CREATE_GROUP_TABLE = "CREATE TABLE " + TrackAppContract.GroupEntry.TABLE_NAME + " (" +
                TrackAppContract.GroupEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrackAppContract.GroupEntry.COLUMN_CLOUD_ID + " INTEGER UNIQUE NOT NULL, " +
                TrackAppContract.GroupEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                TrackAppContract.GroupEntry.COLUMN_ICON + " TEXT NOT NULL " +
                " );";

        Log.d("CREATE DB", SQL_CREATE_GROUP_TABLE);

        final String SQL_CREATE_CHAT_TABLE = "CREATE TABLE " + TrackAppContract.ChatEntry.TABLE_NAME + " (" +
                TrackAppContract.ChatEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrackAppContract.ChatEntry.COLUMN_FROM + " INTEGER NOT NULL, " +
                TrackAppContract.ChatEntry.COLUMN_TO + " INTEGER NOT NULL, " +
                TrackAppContract.ChatEntry.COLUMN_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                TrackAppContract.ChatEntry.COLUMN_IS_SEND + " INTEGER NOT NULL, " +
                TrackAppContract.ChatEntry.COLUMN_CONTENT + " TEXT NOT NULL " +
                " );";

        Log.d("CREATE DB", SQL_CREATE_CHAT_TABLE);

        final String SQL_CREATE_GROUP_CHAT_TABLE = "CREATE TABLE " + TrackAppContract.GroupChatEntry.TABLE_NAME + " (" +
                TrackAppContract.GroupChatEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrackAppContract.GroupChatEntry.COLUMN_FROM + " INTEGER NOT NULL, " +
                TrackAppContract.GroupChatEntry.COLUMN_TO + " INTEGER NOT NULL, " +
                TrackAppContract.GroupChatEntry.COLUMN_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                TrackAppContract.GroupChatEntry.COLUMN_IS_SEND + " INTEGER NOT NULL, " +
                TrackAppContract.GroupChatEntry.COLUMN_CONTENT + " TEXT NOT NULL " +
                " );";

        Log.d("CREATE DB", SQL_CREATE_GROUP_CHAT_TABLE);

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + TrackAppContract.LocationEntry.TABLE_NAME + " (" +
                TrackAppContract.LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrackAppContract.LocationEntry.COLUMN_LONGITUDE + " REAL NOT NULL, " +
                TrackAppContract.LocationEntry.COLUMN_LATITUDE + " REAL NOT NULL, " +
                TrackAppContract.LocationEntry.COLUMN_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                TrackAppContract.LocationEntry.COLUMN_USER_CLOUD_ID + " INTEGER NOT NULL, " +
                TrackAppContract.LocationEntry.COLUMN_CONTACT_KEY + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + TrackAppContract.LocationEntry.COLUMN_CONTACT_KEY + ") REFERENCES " +
                TrackAppContract.ContactEntry.TABLE_NAME + " (" + TrackAppContract.ContactEntry._ID + ") " +
                " );";

        Log.d("CREATE DB", SQL_CREATE_LOCATION_TABLE);

        final String SQL_CREATE_TMP_LOCATION_TABLE = "CREATE TABLE " + TrackAppContract.TmpLocationEntry.TABLE_NAME + " (" +
                TrackAppContract.TmpLocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrackAppContract.TmpLocationEntry.COLUMN_LONGITUDE + " REAL NOT NULL, " +
                TrackAppContract.TmpLocationEntry.COLUMN_LATITUDE + " REAL NOT NULL, " +
                TrackAppContract.TmpLocationEntry.COLUMN_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL " +
                " );";

        Log.d("CREATE DB", SQL_CREATE_TMP_LOCATION_TABLE);

        final String SQL_CREATE_GROUP_MEMBER_TABLE = "CREATE TABLE " + TrackAppContract.GroupMemberEntry.TABLE_NAME + " (" +
                TrackAppContract.GroupMemberEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrackAppContract.GroupMemberEntry.COLUMN_CONTACT_KEY + " INTEGER NOT NULL, " +
                TrackAppContract.GroupMemberEntry.COLUMN_GROUP_KEY + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + TrackAppContract.GroupMemberEntry.COLUMN_CONTACT_KEY + ") REFERENCES " +
                TrackAppContract.ContactEntry.TABLE_NAME + " (" + TrackAppContract.ContactEntry._ID + ") " +
                " FOREIGN KEY (" + TrackAppContract.GroupMemberEntry.COLUMN_GROUP_KEY + ") REFERENCES " +
                TrackAppContract.GroupEntry.TABLE_NAME + " (" + TrackAppContract.GroupEntry._ID + ") " +
                " );";

        Log.d("CREATE DB", SQL_CREATE_GROUP_MEMBER_TABLE);

        final String SQL_CREATE_SUBSCRIPTION_TABLE = "CREATE TABLE " + TrackAppContract.SubscriptionEntry.TABLE_NAME + " (" +
                TrackAppContract.SubscriptionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrackAppContract.SubscriptionEntry.COLUMN_CLOUD_ID + " INTEGER NOT NULL, " +
                TrackAppContract.SubscriptionEntry.COLUMN_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                TrackAppContract.SubscriptionEntry.COLUMN_IS_ACTIVE + " INTEGER DEFAULT 1 NOT NULL, " +
                TrackAppContract.SubscriptionEntry.COLUMN_SUBSCRIBER_KEY + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + TrackAppContract.SubscriptionEntry.COLUMN_SUBSCRIBER_KEY + ") REFERENCES " +
                TrackAppContract.ContactEntry.TABLE_NAME + " (" + TrackAppContract.ContactEntry._ID + ") " +
                " );";

        Log.d("CREATE DB", SQL_CREATE_SUBSCRIPTION_TABLE);

        final String SQL_CREATE_SUBSCRIPTION_MEMBER_TABLE = "CREATE TABLE " + TrackAppContract.SubscriptionMemberEntry.TABLE_NAME + " (" +
                TrackAppContract.SubscriptionMemberEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrackAppContract.SubscriptionMemberEntry.COLUMN_CONTACT_KEY + " INTEGER NOT NULL, " +
                TrackAppContract.SubscriptionMemberEntry.COLUMN_SUBSCRIPTION_KEY + " INTEGER NOT NULL, " +
                TrackAppContract.SubscriptionMemberEntry.COLUMN_IS_ADMINISTRATOR + " INTEGER DEFAULT 0 NOT NULL, " +
                TrackAppContract.SubscriptionMemberEntry.COLUMN_IS_SUPERVISOR + " INTEGER DEFAULT 0 NOT NULL, " +
                " FOREIGN KEY (" + TrackAppContract.SubscriptionMemberEntry.COLUMN_CONTACT_KEY + ") REFERENCES " +
                TrackAppContract.ContactEntry.TABLE_NAME + " (" + TrackAppContract.ContactEntry._ID + ") " +
                " FOREIGN KEY (" + TrackAppContract.SubscriptionMemberEntry.COLUMN_SUBSCRIPTION_KEY + ") REFERENCES " +
                TrackAppContract.SubscriptionEntry.TABLE_NAME + " (" + TrackAppContract.SubscriptionEntry._ID + ") " +
                " );";

        Log.d("CREATE DB", SQL_CREATE_SUBSCRIPTION_MEMBER_TABLE);

        db.execSQL(SQL_CREATE_COUNTRY_TABLE);
        db.execSQL(SQL_CREATE_CONTACT_TABLE);
        db.execSQL(SQL_CREATE_PROFILE_TABLE);
        db.execSQL(SQL_CREATE_GROUP_TABLE);
        db.execSQL(SQL_CREATE_CHAT_TABLE);
        db.execSQL(SQL_CREATE_GROUP_CHAT_TABLE);
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_TMP_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_GROUP_MEMBER_TABLE);
        db.execSQL(SQL_CREATE_SUBSCRIPTION_TABLE);
        db.execSQL(SQL_CREATE_SUBSCRIPTION_MEMBER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TrackAppContract.CountryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrackAppContract.ContactEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrackAppContract.ProfileEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrackAppContract.GroupEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrackAppContract.ChatEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrackAppContract.GroupChatEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrackAppContract.LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrackAppContract.TmpLocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrackAppContract.GroupMemberEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrackAppContract.SubscriptionEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrackAppContract.SubscriptionMemberEntry.TABLE_NAME);
        onCreate(db);
    }
}
