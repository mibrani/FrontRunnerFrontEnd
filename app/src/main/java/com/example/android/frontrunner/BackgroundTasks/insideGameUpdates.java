package com.example.android.frontrunner.BackgroundTasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.frontrunner.Common.Common;
import com.example.android.frontrunner.Common.DBRelated;
import com.example.android.frontrunner.Common.JSONOperations;
import com.example.android.frontrunner.MainActivity;
import com.example.android.frontrunner.MainFragment;
import com.example.android.frontrunner.Maps.MapsActivity;
import com.example.android.frontrunner.data.DataContract;
import com.example.android.frontrunner.entities.Game;
import com.example.android.frontrunner.entities.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dev on 30/09/2014.
 */
public class insideGameUpdates extends AsyncTask {

    public static HashMap<Integer, Marker> userMarkers = new HashMap<Integer, Marker>();
    private List<Game> currentGameList = new ArrayList<Game>();
    public static HashMap<Integer,String> participantNames = new HashMap<Integer, String>();

    @Override
    protected Void doInBackground(Object[] param) {

        String updateLocationJsonString = JSONOperations.fetchJSONFromString(DataContract.Games.HTTP_GAMES_URL + "?game_name=" + Common.currentGame);

        currentGameList = JSONOperations.convertJSONtoGames(updateLocationJsonString);

        if(participantNames.size() !=0) return null;


        for (Game game : currentGameList) {//Only current game members, 1 ROW 1 MEMBER!
            if (game.getParticipant() != MainActivity.appUser.getId()) //EXCEPT MYSELF.
            {
                Cursor cursor = MainActivity.mainActivity.getContentResolver().query(DataContract.Users.USERS_CONTENT_URI,null,DataContract.Users.COLUMN_USER_ID + " = ?",new String[]{Integer.toString(game.getParticipant())},null);
                cursor.moveToFirst();
                User user = DBRelated.getUserFromCursor(cursor);
                participantNames.put(game.getParticipant(), user.getNickname());

            }

        }

        return null;
    }



    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        //Update Markers :-)
        Log.i("InsideGameUpdates","onPostExecute");

        for (Game game : currentGameList) {

            if (!userMarkers.containsKey(game.getParticipant()) || userMarkers.get(game.getParticipant()) == null) {
                Marker newMarker = MapsActivity.mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(game.getLatitude(), game.getLongitude()))
                        .title(participantNames.get(game.getParticipant())));
                userMarkers.put(game.getParticipant(), newMarker);
                newMarker.showInfoWindow();
            } else {//marker exists, update location

                Marker marker = userMarkers.get(game.getParticipant());

                marker.setPosition(new LatLng(game.getLatitude(), game.getLongitude()));
                marker.showInfoWindow();

            }

        }
    }
}
