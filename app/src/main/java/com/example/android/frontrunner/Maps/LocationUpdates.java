package com.example.android.frontrunner.Maps;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.android.frontrunner.BackgroundTasks.OnDemandSync;
import com.example.android.frontrunner.Common.Common;
import com.example.android.frontrunner.Common.DBRelated;
import com.example.android.frontrunner.MainActivity;
import com.example.android.frontrunner.MainFragment;
import com.example.android.frontrunner.data.DataContract;
import com.example.android.frontrunner.entities.Game;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by dev on 29/09/2014.
 */
public class LocationUpdates implements LocationListener {


    Context appContext;
    public static LocationUpdates instance = null;

    public static LocationUpdates getInstance()
    {
        return instance;
    }
    public LocationUpdates(Context context, Game myGame){
        myGameRow = myGame;
        appContext = context;
        instance = this;
    }

    public static Marker myMarker;
    private Game myGameRow;

    @Override
    public void onLocationChanged(Location location) {//Update my location on the map, cloud and local DB
        if(MainActivity.appUser != null && !Common.currentGame.equals("")) {

            if(myGameRow == null) return;



            myGameRow.setLatitude(location.getLatitude());
            myGameRow.setLongitude(location.getLongitude());

            //Sync to cloud
            AsyncTask syncLocation =  new OnDemandSync(appContext);
            if(syncLocation.getStatus() != AsyncTask.Status.RUNNING)
                syncLocation.execute(MainFragment.OnDemandSyncRequestType.updateLocation.toString(), null, myGameRow.getId(), null, location);;
            //Sync locally
            DBRelated.syncGame(myGameRow);
            LatLng myLocation = new LatLng(location.getLatitude(),location.getLongitude());

            //center camera
            if(MapsActivity.mMap != null)
            {
                myMarker.setPosition(myLocation);

                MapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17));

            }
        }
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {


    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
