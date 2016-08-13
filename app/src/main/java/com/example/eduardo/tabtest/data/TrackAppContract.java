package com.example.eduardo.tabtest.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TrackAppContract {

    public static final String CONTENT_AUTHORITY = "com.example.eduardo.tabtest.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CONTACT = "contact";
    public static final String PATH_PROFILE = "profile";
    public static final String PATH_GROUP = "group";
    public static final String PATH_CHAT = "chat";
    public static final String PATH_COUNTRY = "country";
    public static final String PATH_LOCATION = "location";
    public static final String PATH_TMP_LOCATION = "tmp_location";
    public static final String PATH_GROUP_CHAT = "group_chat";
    public static final String PATH_GROUP_MEMBER = "group_member";
    public static final String PATH_SUBSCRIPTION = "subscription";
    public static final String PATH_SUBSCRIPTION_MEMBER = "subscription_member";

    public static final class CountryEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTACT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COUNTRY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COUNTRY;

        public static final String TABLE_NAME = "country";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COUNTRY_CODE = "country_code";


        public static Uri buildCountryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ContactEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTACT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACT;

        public static final String TABLE_NAME = "contact";

        public static final String COLUMN_CLOUD_ID = "user_cloud_id";
        public static final String COLUMN_COUNTRY_CODE = "country_code";
        public static final String COLUMN_PHONE_NUMBER = "phone_number";

        public static Uri buildContactUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ProfileEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PROFILE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROFILE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROFILE;

        public static final String TABLE_NAME = "profile";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_CONTACT_KEY = "contact_id";
        public static final String COLUMN_IS_SUPERVISOR = "is_supervisor";

        public static Uri buildProfileUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class GroupEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GROUP).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GROUP;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GROUP;

        public static final String TABLE_NAME = "app_group";

        public static final String COLUMN_CLOUD_ID = "group_cloud_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ICON = "icon";

        public static Uri buildGroupUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ChatEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHAT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHAT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHAT;

        public static final String TABLE_NAME = "chat";

        public static final String COLUMN_FROM = "ffrom";
        public static final String COLUMN_TO = "tto";
        public static final String COLUMN_CREATED = "created";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_IS_SEND = "is_send";

        public static Uri buildChatUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildChatFromTo(long from, long to) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(from))
                    .appendPath(Long.toString(to)).build();
        }

        public static long getFromFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getToFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }

    public static final class GroupChatEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GROUP_CHAT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GROUP_CHAT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GROUP_CHAT;

        public static final String TABLE_NAME = "group_chat";

        public static final String COLUMN_FROM = "ffrom";
        public static final String COLUMN_TO = "tto";
        public static final String COLUMN_CREATED = "created";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_IS_SEND = "is_send";

        public static Uri buildChatUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildChatTo(long to) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(to)).build();
        }

        public static long getToFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static final class LocationEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location";

        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_USER_CLOUD_ID = "user_cloud_id";
        public static final String COLUMN_CONTACT_KEY = "contact_id";
        public static final String COLUMN_CREATED = "created";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildRecentLocation() {
            return CONTENT_URI.buildUpon().appendPath("RECENT").build();
        }

    }

    public static final class TmpLocationEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TMP_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TMP_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TMP_LOCATION;

        public static final String TABLE_NAME = "tmp_location";

        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_CREATED = "created";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class GroupMemberEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GROUP_MEMBER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GROUP_MEMBER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GROUP_MEMBER;

        public static final String TABLE_NAME = "group_contact";

        public static final String COLUMN_CONTACT_KEY = "contact_id";
        public static final String COLUMN_GROUP_KEY = "group_id";

        public static Uri buildGroupMemberUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class SubscriptionEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBSCRIPTION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBSCRIPTION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBSCRIPTION;

        public static final String TABLE_NAME = "subscription";

        public static final String COLUMN_CREATED = "created";
        public static final String COLUMN_IS_ACTIVE = "is_active";
        public static final String COLUMN_SUBSCRIBER_KEY = "subscriber_id";
        public static final String COLUMN_CLOUD_ID = "cloud_id";

        public static Uri buildSubscriptionUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class SubscriptionMemberEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBSCRIPTION_MEMBER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBSCRIPTION_MEMBER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBSCRIPTION_MEMBER;

        public static final String TABLE_NAME = "subscription_member";

        public static final String COLUMN_CONTACT_KEY = "contact_id";
        public static final String COLUMN_SUBSCRIPTION_KEY = "subscription_id";
        public static final String COLUMN_IS_ADMINISTRATOR = "is_administrator";
        public static final String COLUMN_IS_SUPERVISOR = "is_supervisor";

        public static Uri buildSubscriptionMemberUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
