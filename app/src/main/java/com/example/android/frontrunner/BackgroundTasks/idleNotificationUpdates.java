package com.example.android.frontrunner.BackgroundTasks;

import android.os.AsyncTask;

import com.example.android.frontrunner.Common.Common;
import com.example.android.frontrunner.Common.DBRelated;
import com.example.android.frontrunner.MainActivity;
import com.example.android.frontrunner.MainFragment;

/**
 * Created by Melos on 10/11/2014.
 */
public class idleNotificationUpdates extends AsyncTask {


    public idleNotificationUpdates() {
        super();

        Common.setMainButtonCreateGame();
    }

    @Override
    protected Boolean doInBackground(Object[] params) {
        //check if login is incorrect
        if (MainActivity.appUser == null) {

            DBRelated.authenticateUser();
        }

        else {

            Common.currentUpdateState = new waitingForInvites();
            Common.currentUpdateState.execute();

        }

        return (MainActivity.appUser != null);

    }
}
