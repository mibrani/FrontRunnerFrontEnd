package com.example.android.frontrunner.BackgroundTasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.frontrunner.Common.Common;
import com.example.android.frontrunner.Common.DBRelated;
import com.example.android.frontrunner.Common.JSONOperations;
import com.example.android.frontrunner.MainActivity;
import com.example.android.frontrunner.MainFragment;
import com.example.android.frontrunner.Maps.MapsActivity;
import com.example.android.frontrunner.R;
import com.example.android.frontrunner.data.DataContract;
import com.example.android.frontrunner.entities.Game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Melos on 10/11/2014.
 */
public class gameCreated extends AsyncTask {

    public gameCreated() {
        super();

    }

    @Override
    protected Object doInBackground(Object[] params) {
        String updateGameJsonString = JSONOperations.fetchJSONFromString(DataContract.Games.HTTP_GAMES_URL + "?game_name=" + Common.currentGame);

        List<Game> updatedGames = JSONOperations.convertJSONtoGames(updateGameJsonString);
        for (Game game : updatedGames)
        {
            DBRelated.syncGame(game);
            //Update participant statuses
            Common.participantsReady.put(game.getParticipant(), game.getParticipant_status());

        }
        return null;

    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        Button btn = (Button) MainActivity.mainActivity.findViewById(R.id.start_race_BTN);
        if (!checkIfAllAreReady()) {
            btn.setText("Waiting");
            btn.setEnabled(false);
        } else {
            btn.setText("START");

            btn.setEnabled(true);

            //Register START BUTTON click handler

            Button startGame = (Button) MainActivity.mainActivity.findViewById(R.id.start_race_BTN);
            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.mainActivity, MapsActivity.class);
                    MainActivity.mainActivity.startActivityForResult(intent,0);


                }
            });
        }
    }

    public boolean checkIfAllAreReady()
    {

        if(Common.participantsReady.size() > 0)
        {
            for(Map.Entry<Integer, Integer> participant : Common.participantsReady.entrySet())
            {
                if (participant.getValue() == DBRelated.PARTICIPANT_STATUS_INVITED) {

                    return false;

                }
                else
                if(Common.participantsReady.size() == 2 && participant.getValue() == DBRelated.PARTICIPANT_STATUS_REJECTED)
                {
                    Common.restartGameState();
                    Common.currentUpdateState = new waitingForInvites();
                    Common.currentUpdateState.execute();
                    return false;
                }

                return true;
            }
        }



            return false;
    }
    }

