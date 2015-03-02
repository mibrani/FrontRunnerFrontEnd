package com.example.android.frontrunner;

import android.content.Context;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Melos on 9/24/2014.
 */
public class UserDetailActivity extends ActionBarActivity {
    Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();
        setContentView(R.layout.user_detail_activity);

        UserDetailFragment userDetailfragment = new UserDetailFragment();
        Integer userID = getIntent().getIntExtra(MainFragment.EXTRA_FIELD_ID,-1);
        Bundle bundle = new Bundle();

        bundle.putInt(MainFragment.EXTRA_FIELD_ID,userID);
        userDetailfragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.additional_container, userDetailfragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}


