package com.example.android.frontrunner.Common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.frontrunner.BackgroundTasks.OnDemandSync;
import com.example.android.frontrunner.BackgroundTasks.gameCreated;
import com.example.android.frontrunner.BackgroundTasks.idleNotificationUpdates;
import com.example.android.frontrunner.BackgroundTasks.insideGameUpdates;
import com.example.android.frontrunner.BackgroundTasks.waitingForInvites;
import com.example.android.frontrunner.MainActivity;
import com.example.android.frontrunner.MainFragment;
import com.example.android.frontrunner.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Melos on 10/11/2014.
 */
public class Common {
    public static HashMap<Integer, Integer> participantsReady = new HashMap<Integer, Integer>(); //list of participant that have accepted the game
    public static AsyncTask currentUpdateState = null;
    public static HashMap<Integer, Integer> positionToUserID = new HashMap<Integer, Integer>(); //workaround for checkbox inside listview issue.
    public static ArrayList<Integer> selectedUsers = new ArrayList<Integer>();//Users selected for race.
    public static String currentGame;


    public static void restartGameState()
    {
        selectedUsers.clear();
        positionToUserID.clear();


        //Set the updates to insideGameUpdates
        currentUpdateState = new waitingForInvites();
        currentUpdateState.execute();

        setMainButtonCreateGame();

        currentGame = null;

        participantsReady.clear();

        setMainButtonCreateGame();

        insideGameUpdates.participantNames.clear();

        insideGameUpdates.userMarkers.clear();
    }



    public static void startCurrentUpdateTask() {//Get the type used by the currentUpdateState and use it to re create an object of the same type
        if (currentUpdateState == null) {//It should only be null on app start. So set it to idle

            //Start in Idle mode.
            currentUpdateState = new idleNotificationUpdates();
            currentUpdateState.execute();
        }

        if (currentUpdateState instanceof gameCreated) {
            currentUpdateState = new gameCreated().execute();
        } else if (currentUpdateState instanceof idleNotificationUpdates) {
            currentUpdateState = new idleNotificationUpdates().execute();

        } else if (currentUpdateState instanceof insideGameUpdates) {
            currentUpdateState = new insideGameUpdates().execute();
        } else if (currentUpdateState instanceof waitingForInvites) {
            currentUpdateState = new waitingForInvites().execute();
        }
    }

    public static void setMainButtonCreateGame()
    {
        final Button startGame = (Button) MainActivity.mainActivity.findViewById(R.id.start_race_BTN);
        startGame.setText("Start Race");
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText gameNameET = new EditText(MainActivity.mainActivity);
                new AlertDialog.Builder(MainActivity.mainActivity)
                        .setTitle("Please input game name")
                        .setMessage("Please input game name")
                        .setView(gameNameET)
                        .setPositiveButton("Create Game", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String gameName = gameNameET.getText().toString();

                                new OnDemandSync(MainActivity.mainActivity).execute(MainFragment.OnDemandSyncRequestType.createGame.toString(), gameName);
                                Common.currentUpdateState = new gameCreated();
                                Common.currentUpdateState.execute();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();


            }
        });
    }

}
