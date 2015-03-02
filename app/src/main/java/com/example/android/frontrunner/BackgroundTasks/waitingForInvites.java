package com.example.android.frontrunner.BackgroundTasks;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.android.frontrunner.Common.Common;
import com.example.android.frontrunner.Common.DBRelated;
import com.example.android.frontrunner.Common.JSONOperations;
import com.example.android.frontrunner.MainActivity;
import com.example.android.frontrunner.MainFragment;
import com.example.android.frontrunner.Maps.MapsActivity;
import com.example.android.frontrunner.data.DataContract;
import com.example.android.frontrunner.entities.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melos on 10/11/2014.
 */
public class waitingForInvites extends AsyncTask {
    static List<String> invitationShown = new ArrayList<String>();

    public waitingForInvites() {
        super();
        Common.setMainButtonCreateGame();
    }

    @Override
    protected Void doInBackground(Object[] params) {


            String invitationJsonString = JSONOperations.fetchJSONFromString(DataContract.Games.HTTP_GAMES_URL + "?participant=" + MainActivity.appUser.getId() + "&participant_status=0");

            List<Game> gamesInvited = JSONOperations.convertJSONtoGames(invitationJsonString);
            if (gamesInvited.size() > 0) {
                for (Game currentGame : gamesInvited) {
                    DBRelated.syncGame(currentGame);
                }
            }
        return null;
    }



    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        //NOw check from the local DB if user is challenged for a game and display dialog box accordingly(UI Thread)
        final Cursor cursor = MainActivity.mainActivity.getContentResolver()
                .query(DataContract.Games.GAMES_CONTENT_URI.buildUpon().appendPath(Integer.toString(MainActivity.appUser.getId())).appendPath("0").build(), null, null, null, null);

        if (cursor.moveToLast())//get the LAST game invited
        {
            System.out.println("Challenged!");

            final String gameName = cursor.getString(DBRelated.GAMES_GAMENAME_COL_INDEX);


            //show invitation only once
            if (!invitationShown.contains(gameName))
            {


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.mainActivity);
                invitationShown.add(gameName);
                builder
                        .setTitle("You have been challenged for a race in a game!: " + gameName)
                        .setMessage("Accept the challenge?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //toggle participant_status
                                ContentValues newValues = new ContentValues();
                                Object[] values = new Object[4];
                                values[0] = MainFragment.OnDemandSyncRequestType.toggleParticipantStatus.toString();
                                values[1] = gameName;
                                values[2] = MainActivity.appUser.getId();
                                values[3] = DBRelated.PARTICIPANT_STATUS_ACCEPTED;

                                //Execute the other AsyncTask in order to toggle current user status to accepted
                                new OnDemandSync(MainActivity.mainActivity).execute(values);

                                Common.currentGame = gameName;

                                Intent displayMap = new Intent(MainActivity.mainActivity, MapsActivity.class);
                                MainActivity.mainActivity.startActivityForResult(displayMap,0);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //toggle participant_status
                                ContentValues newValues = new ContentValues();
                                Object[] values = new Object[4];
                                values[0] = MainFragment.OnDemandSyncRequestType.toggleParticipantStatus.toString();
                                values[1] = gameName;
                                values[2] = MainActivity.appUser.getId();
                                values[3] = DBRelated.PARTICIPANT_STATUS_REJECTED;

                                //Execute the other AsyncTask in order to toggle current user status to rejected
                                new OnDemandSync(MainActivity.mainActivity).execute(values);

                            }
                        })                        //Do nothing on no
                        .show();

            }
        }
    }
}
