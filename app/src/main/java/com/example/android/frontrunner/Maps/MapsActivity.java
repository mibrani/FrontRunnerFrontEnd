package com.example.android.frontrunner.Maps;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.frontrunner.BackgroundTasks.insideGameUpdates;
import com.example.android.frontrunner.Common.Common;
import com.example.android.frontrunner.Common.DBRelated;
import com.example.android.frontrunner.MainActivity;
import com.example.android.frontrunner.MainFragment;
import com.example.android.frontrunner.R;
import com.example.android.frontrunner.data.DataContract;
import com.example.android.frontrunner.entities.Game;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity {
    public static int LOCATION_UPDATE_INTERVAL = 20 * 1000; // 5 * 60 * 1000 = 5 minutes



    public static GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mMap = null;// Has to be recreated in order to properly attach it to MapFragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        insideGameUpdates.userMarkers.clear();//because on revisit they never get shown/


        setUpMapIfNeeded();
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //Get the current game/user row id
        Cursor cursor = this.getContentResolver().query(DataContract.Games.GAMES_CONTENT_URI.buildUpon().appendPath(Common.currentGame).appendPath(Integer.toString(MainActivity.appUser.getId())).build(), null, null, null, null);
        if(!cursor.moveToFirst()) return;
        Game myGameRow = DBRelated.getGameFromCursor(cursor);

        // Define a listener that responds to location updates
       LocationUpdates locationUpdates = new LocationUpdates(getApplicationContext(),myGameRow);


        //for (String provider : locationManager.getAllProviders()) { locationManager.requestLocationUpdates(  provider, LOCATION_UPDATE_INTERVAL, 0, locationUpdates); }


        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVAL, 0, locationUpdates);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        locationManager.requestLocationUpdates(LOCATION_UPDATE_INTERVAL, 0, criteria, locationUpdates, null);

        //Try to get the current location, otherwise default to 0,0
Location loc;
        LatLng myLocation;
            loc = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        if(loc == null)
            loc = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        if(loc != null) {

            myLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
        }
        else myLocation = new LatLng(0,0);
        if(mMap != null)
        {
            LocationUpdates.myMarker = MapsActivity.mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMap = mapFragment.getMap();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(LocationUpdates.getInstance());
        locationManager = null;
    }


    private void setUpMapIfNeeded()
    {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null)
        {
            // Try to obtain the map from the SupportMapFragment.
            int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            if(result== ConnectionResult.SERVICE_DISABLED || result== ConnectionResult.SERVICE_MISSING || result== ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED)
            {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(result,this,0);
                dialog.show();
            }
            else {
                mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                        .getMap();

                //Set the updates to insideGameUpdates
                Common.currentUpdateState = new insideGameUpdates();
                Common.currentUpdateState.execute();
            }

        }
    }
}
