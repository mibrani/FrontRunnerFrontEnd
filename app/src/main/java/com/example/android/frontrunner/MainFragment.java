package com.example.android.frontrunner;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.frontrunner.Common.Common;
import com.example.android.frontrunner.Common.DBRelated;
import com.example.android.frontrunner.data.DataContract;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Melos on 9/21/2014.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static enum OnDemandSyncRequestType {updateLocation, toggleParticipantStatus, createGame}

    public static final int USER_LOADER = 1;
    public static final String EXTRA_FIELD_ID = "user";
    private int userListPosition = -1;
    private String USER_LIST_POSITION = "userListPosition";

    private FRAdapter userAdapter;
    private ListView userList;
    private Context appContext;


    public static TimerTask scheduleNotificationUpdates;

    public MainFragment() {
    }

    public interface Callback {

        public void onItemSelected(Integer uid);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(USER_LIST_POSITION, userListPosition);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(USER_LOADER, null, this);
        scheduleNotificationTask();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        this.setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int optionsId = item.getItemId();
        if (optionsId == R.id.action_refresh) {
            refreshData();
        }
        return super.onOptionsItemSelected(item);
    }


    void refreshData() {
        Common.startCurrentUpdateTask();
        restartLoader();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (savedInstanceState != null) {
            userListPosition = savedInstanceState.getInt(USER_LIST_POSITION);
        }
        appContext = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        userList = (ListView) rootView.findViewById(R.id.friend_list_LV);
        userAdapter = new FRAdapter(this.getActivity(), null, 0);

        if (DBRelated.authenticateUser()) userList.setAdapter(userAdapter);
        else
            Toast.makeText(appContext, "Login failed, please supply correct login information in settings", Toast.LENGTH_LONG).show();
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                userListPosition = position;


                Cursor cursor = userAdapter.getCursor();
                int userID = cursor.getInt(DBRelated.USER_ID_COL_INDEX);

                ((Callback) getActivity()).onItemSelected(userID);
            }
        });


        return rootView;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        return new CursorLoader(
                getActivity(),
                DataContract.Users.USERS_CONTENT_URI,
                DBRelated.userColumns,
                null,
                null,
                null
        );
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.appUser != null)
            if (userList.getAdapter() == null) userList.setAdapter(userAdapter);
        Common.startCurrentUpdateTask();
        restartLoader();

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        userAdapter.swapCursor(cursor);

        if (userListPosition != ListView.INVALID_POSITION && MainActivity.tabletMode)
            userList.setSelection(userListPosition);

    }

    public void restartLoader() {
        getLoaderManager().restartLoader(USER_LOADER, null, this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        userAdapter.swapCursor(null);

    }

    public void scheduleNotificationTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        scheduleNotificationUpdates = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            refreshData();

                        } catch (Exception e) {

                        }
                    }
                });
            }
        };
        timer.schedule(scheduleNotificationUpdates, 0, 20000);
    }
}

