package com.example.eduardo.tabtest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.example.eduardo.tabtest.data.DbUtilities;
import com.example.eduardo.tabtest.data.TrackAppContract;
import com.example.eduardo.tabtest.service.SendLocationService;
import com.example.eduardo.tabtest.utils.NetworkUtilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class MapTabFragment extends SupportMapFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = MapTabFragment.class.getSimpleName();
    private static final int LOCATION_LOADER = 0;
    private static final int LOCATION_INTERVAL_SECONDS = 5;
    private static final float MULTIPLE_GROUP_COLOR = 90.0f;

//    private static final LatLng LIMA = new LatLng(-12.0158, -77.0758);

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;

    private static final String[] LOCATION_COLUMNS = {
            TrackAppContract.LocationEntry.TABLE_NAME + "." + TrackAppContract.LocationEntry._ID,
            TrackAppContract.LocationEntry.COLUMN_LONGITUDE,
            TrackAppContract.LocationEntry.COLUMN_LATITUDE,
            TrackAppContract.LocationEntry.COLUMN_CONTACT_KEY,
            "MAX( " + TrackAppContract.LocationEntry.COLUMN_CREATED + " )"
    };

    //static final int COL_LOCATION_ID = 0;
    static final int COL_LOCATION_LONGITUDE = 1;
    static final int COL_LOCATION_LATITUDE = 2;
    static final int COL_LOCATION_CONTACT_ID = 3;
    //static final int COL_LOCATION_MAX_CREATED = 4;

    private static final float[] MARKER_COLORS = {
            BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_ROSE,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_YELLOW
    };


    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    GoogleMap mGoogleMap;
    Map<Long, Marker> markerMap = new HashMap<Long, Marker>();

    public MapTabFragment() {
        super();
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        super.onActivityCreated(bundle);
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleMap == null) {
            getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Map is ready");
        mGoogleMap = googleMap;
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LIMA, 15));

        getLoaderManager().initLoader(LOCATION_LOADER, null, this);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LOCATION_INTERVAL_SECONDS * 1000);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng currentLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        CameraPosition currentCameraPosition = new CameraPosition.Builder()
                .target(currentLatLng)
                .zoom(17)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentCameraPosition));
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mGoogleMap.setMyLocationEnabled(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Map connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Map connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Double longitude = location.getLongitude();
        Double latitude = location.getLatitude();

        Log.d(TAG, "Location changed ( longitude =  " + longitude + ", latitude = " + latitude + " )");

        if(NetworkUtilities.isOnline(getActivity())){
            Intent sendLocationIntent = new Intent(getActivity(), SendLocationService.class);
            sendLocationIntent.putExtra(SendLocationService.EXTRA_LONGITUDE, longitude);
            sendLocationIntent.putExtra(SendLocationService.EXTRA_LATITUDE, latitude);
            getActivity().startService(sendLocationIntent);
        }
        else{
            DbUtilities.insertTmpLocation(getActivity(), longitude, latitude);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                TrackAppContract.LocationEntry.buildRecentLocation(),
                LOCATION_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(TAG, "Map cursor on load finish");
        if (data != null) {

            while (data.moveToNext()) {
                double longitude = data.getDouble(COL_LOCATION_LONGITUDE);
                double latitude = data.getDouble(COL_LOCATION_LATITUDE);
                long contactID = data.getLong(COL_LOCATION_CONTACT_ID);
                String contactName = "CONTACT ERROR";
                String groupName = "GROUP ERROR";
                float markerColor = MULTIPLE_GROUP_COLOR;

                Cursor cursor = getActivity().getContentResolver().query(TrackAppContract.ProfileEntry.CONTENT_URI,
                        new String[]{TrackAppContract.ProfileEntry.COLUMN_NAME},
                        TrackAppContract.ProfileEntry.TABLE_NAME + "." + TrackAppContract.ProfileEntry.COLUMN_CONTACT_KEY + " = ?",
                        new String[]{Long.toString(contactID)},
                        null);

                if(cursor != null){
                    if( cursor.moveToFirst() ){
                        contactName = cursor.getString(0);
                    }
                    cursor.close();
                }

                Cursor groupContactCursor = getActivity().getContentResolver().query(TrackAppContract.GroupMemberEntry.CONTENT_URI,
                        new String[]{TrackAppContract.GroupMemberEntry.COLUMN_GROUP_KEY},
                        TrackAppContract.GroupMemberEntry.COLUMN_CONTACT_KEY + " = ?",
                        new String[]{Long.toString(contactID)},
                        null);

                if( groupContactCursor != null ){
                    if( groupContactCursor.getCount() == 1 ){
                        if(groupContactCursor.moveToFirst()){
                            long groupID = groupContactCursor.getLong(0);
                            markerColor = MARKER_COLORS[(int)(groupID % MARKER_COLORS.length)];

                            Cursor groupCursor = getActivity().getContentResolver().query(TrackAppContract.GroupEntry.CONTENT_URI,
                                    new String[]{TrackAppContract.GroupEntry.COLUMN_NAME },
                                    TrackAppContract.GroupEntry._ID + " = ?",
                                    new String[]{Long.toString(groupID)},
                                    null);

                            if( groupCursor != null ){
                                if(groupCursor.moveToFirst()){
                                    groupName = groupCursor.getString(0);
                                    groupCursor.close();
                                }
                            }
                        }
                    }
                    else if( groupContactCursor.getCount() > 1){
                        groupName = "Multiple Group";
                    }

                    groupContactCursor.close();
                }

                Log.i(TAG, "Contact " + contactID + ": ( longitude = " + longitude + ", latitude = " + latitude + " )");

                String title = groupName + ": " + contactName;

                Marker marker =  markerMap.get(contactID);
                if( marker == null){
                    marker = mGoogleMap.addMarker( new MarkerOptions()
                            .position( new LatLng(latitude, longitude) )
                            .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                            .title(title) );
                    markerMap.put(contactID, marker);
                }
                else{
                    marker.setTitle( title);
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(markerColor));
                    marker.setPosition( new LatLng(latitude, longitude) );
                }

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        markerMap.clear();
        mGoogleMap.clear();
    }
}
