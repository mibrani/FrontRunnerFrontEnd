package com.example.android.frontrunner.BackgroundTasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.frontrunner.Common.Common;
import com.example.android.frontrunner.Common.DBRelated;
import com.example.android.frontrunner.FRAdapter;
import com.example.android.frontrunner.MainActivity;
import com.example.android.frontrunner.MainFragment;
import com.example.android.frontrunner.data.DataContract;
import com.example.android.frontrunner.entities.Game;
import com.example.android.frontrunner.sync.SyncAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melos on 9/27/2014.
 */
public class OnDemandSync extends AsyncTask<Object, Void, Void> {
    Context appContext;

    public OnDemandSync(Context context) {
        super();
        appContext = context;
    }

    public void createGame(List<Integer> participants, String gameName) {
        Log.i("Inside","createGame");
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(DataContract.HTTP_BASE_URL + DataContract.CREATE_GAME_URL);

        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        String participantsString = "";

        //include yourself in the game with the ready status. Update locally
        participants.add(MainActivity.appUser.getId());


        for (Integer participant : participants) {
            //construct string representation of user arrayIDs
            participantsString += participant.toString() + ",";

        }


        parameters.add(new BasicNameValuePair("gameName", gameName));
        parameters.add(new BasicNameValuePair("participants", participantsString));
        parameters.add(new BasicNameValuePair("gameCreator",Integer.toString(MainActivity.appUser.getId())));


        try {
            post.setEntity(new UrlEncodedFormEntity(parameters));
            HttpResponse response = client.execute(post);
            FRAdapter.checkForStatus = true;
            Common.currentGame = gameName;



            //Sync changes immediately
            SyncAdapter.syncImmediately(appContext);



            //

        } catch (UnsupportedEncodingException exc) {
            exc.printStackTrace();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Object... params) {
        Log.i("OnDemandSync", "DoinBackground");

        String requestMode = (String) params[0];
        String gameName = (String) params[1];

        if (requestMode.equals(MainFragment.OnDemandSyncRequestType.createGame.toString())) {
            createGame(Common.selectedUsers, gameName);
        } else if (requestMode.equals(MainFragment.OnDemandSyncRequestType.toggleParticipantStatus.toString())) {
            Integer userID = (Integer) params[2];
            Integer newStatus = (Integer) params[3];
            toggleParticipantStatus(userID, newStatus, gameName);
        } else if (requestMode.equals(MainFragment.OnDemandSyncRequestType.updateLocation.toString())) {
            Integer gameID = (Integer) params[2];
            Location userLocation = (Location) params[4];
            updateLocation(gameID,userLocation);


        }
        return null;
    }

    public void toggleParticipantStatus(Integer userID, Integer newStatus, String gameName)
    {
        Log.i("OnDemandSync ","toggleParticipantStatus");
        //Get Game row ID
        Cursor cursor = appContext.getContentResolver().query(DataContract.Games.GAMES_CONTENT_URI.buildUpon().appendPath(gameName).appendPath(Integer.toString(MainActivity.appUser.getId())).build(), null, null, null, null);
        if(!cursor.moveToFirst())  return;

        Game currentUserAndGame = DBRelated.getGameFromCursor(cursor);

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(DataContract.HTTP_BASE_URL + DataContract.UPDATE_GAME_URL);

        List<NameValuePair> parameters = new ArrayList<NameValuePair>();

        String updateSQL = "update " + DataContract.Games.TABLE_NAME + " set " + DataContract.Games.COLUMN_PARTICIPANT_STATUS + " = ? WHERE " +
                DataContract.Games.COLUMN_GAME_ID + " = ? ";


        parameters.add(new BasicNameValuePair("sqlStatement", updateSQL));
        parameters.add(new BasicNameValuePair("whereRowID", Integer.toString(currentUserAndGame.getId())));
        parameters.add(new BasicNameValuePair("newParticipantStatus", newStatus.toString()));

        try {
            post.setEntity(new UrlEncodedFormEntity(parameters));
            HttpResponse response = client.execute(post);

            //update the local DB now
            ContentValues newValue = new ContentValues();
            newValue.put("participant_status", newStatus);
            int rowsUpdated = appContext.getContentResolver().update(DataContract.Games.GAMES_CONTENT_URI, newValue, DataContract.Games.COLUMN_GAME_NAME + " = ? AND " + DataContract.Games.COLUMN_PARTICIPANT + " = ?", new String[]{gameName, userID.toString()});

        }

        catch (UnsupportedEncodingException exc) {
            exc.printStackTrace();
        } catch (IOException exc) {
            exc.printStackTrace();
        }


    }

    public void updateLocation(int gameID, Location location)
    {
        Log.i("OnDemandSync","updateLocation");
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(DataContract.HTTP_BASE_URL + DataContract.UPDATE_GAME_URL);

        List<NameValuePair> parameters = new ArrayList<NameValuePair>();

        String updateSQL = "update " + DataContract.Games.TABLE_NAME + " set " + DataContract.Games.COLUMN_LATITUDE + " = ? , " +
                DataContract.Games.COLUMN_LONGITUDE + " = ? WHERE " + DataContract.Games.COLUMN_GAME_ID +
                " = ? ";


        parameters.add(new BasicNameValuePair("sqlStatement", updateSQL));
        parameters.add(new BasicNameValuePair("newLatitude", Double.toString(location.getLatitude())));
        parameters.add(new BasicNameValuePair("newLongitude", Double.toString(location.getLongitude())));
        parameters.add(new BasicNameValuePair("whereRowID", Integer.toString(gameID)));


        try {
            post.setEntity(new UrlEncodedFormEntity(parameters));
            HttpResponse response = client.execute(post);


        } catch (UnsupportedEncodingException exc) {
            exc.printStackTrace();
        } catch (IOException exc) {
            exc.printStackTrace();
        }

    }


}
