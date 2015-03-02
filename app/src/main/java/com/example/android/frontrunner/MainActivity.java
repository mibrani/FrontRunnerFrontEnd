package com.example.android.frontrunner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.frontrunner.BackgroundTasks.idleNotificationUpdates;
import com.example.android.frontrunner.BackgroundTasks.waitingForInvites;
import com.example.android.frontrunner.Common.Common;
import com.example.android.frontrunner.entities.User;
import com.example.android.frontrunner.sync.SyncAdapter;


public class MainActivity extends ActionBarActivity implements MainFragment.Callback {


    public static Context appContext;
    public static Activity mainActivity;
    public static User appUser;
    public static boolean tabletMode;

    @Override
    public void onItemSelected(Integer userID) {

        if (tabletMode) {
            UserDetailFragment detailFragment = new UserDetailFragment();
            Bundle arguments = new Bundle();
            arguments.putInt(MainFragment.EXTRA_FIELD_ID, userID);
            detailFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.additional_container, detailFragment).commit();
        } else {

            Intent detailActivity = new Intent(this, UserDetailActivity.class).putExtra(MainFragment.EXTRA_FIELD_ID, userID);

            startActivity(detailActivity);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();
        SyncAdapter.syncImmediately(appContext);
        mainActivity = this;
        setContentView(R.layout.activity_main);
        SyncAdapter.initializeSyncAdapter(this);
        if (findViewById(R.id.additional_container) != null) {
            tabletMode = true;
            if (savedInstanceState == null) getSupportFragmentManager().beginTransaction().replace(
                    R.id.additional_container, new UserDetailFragment()).commit();

        } else tabletMode = false;


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Common.restartGameState();

        Common.currentUpdateState = new waitingForInvites();
        Common.currentUpdateState.execute();
    }


    /**
     * A placeholder fragment containing a simple view.
     */

}
