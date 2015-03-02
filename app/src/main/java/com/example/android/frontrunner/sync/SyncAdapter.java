package com.example.android.frontrunner.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.android.frontrunner.Common.DBRelated;
import com.example.android.frontrunner.Common.JSONOperations;
import com.example.android.frontrunner.MainActivity;
import com.example.android.frontrunner.R;
import com.example.android.frontrunner.data.DataContract;
import com.example.android.frontrunner.entities.Game;
import com.example.android.frontrunner.entities.User;

import java.util.List;

/**
 * Created by Melos on 9/28/2014.
 * Parts of it copied from developer.android.com
 * USED ONLY FOR PERIODIC FULL TABLE SYNC.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {


    public static final int SYNC_INTERVAL = 60 * 360;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private Context appContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        appContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.i("Performing", "Full User and Game table Sync");
        getContext().getContentResolver().delete(DataContract.Games.GAMES_CONTENT_URI,null,null);
        getContext().getContentResolver().delete(DataContract.Users.USERS_CONTENT_URI,null,null);


            String userJsonString = JSONOperations.fetchJSONFromString( DataContract.Users.HTTP_USERS_URL);
            List<User> listUsers = JSONOperations.convertJSONToUsers(userJsonString);
            for (User user : listUsers) {
                DBRelated.syncUser(getContext(), user);
            }

        //check if login is incorrect
        if (MainActivity.appUser == null) {

            DBRelated.authenticateUser();
        } else//if there is an authenticated user, do the rest
        //Sync Users
        {
            //Sync Games

            String gameJsonString = JSONOperations.fetchJSONFromString(DataContract.Games.HTTP_GAMES_URL);

            List<Game> games = JSONOperations.convertJSONtoGames(gameJsonString);
            for (Game game : games) DBRelated.syncGame(game);


        }
    }
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }


    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {
            // Add the account and account type, no password or user data
            // If successful, return the Account object, otherwise report an error.
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            // If you don't set android:syncable="true" in
            // in your <provider> element in the manifest,
            // then call context.setIsSyncable(account, AUTHORITY, 1)
            // here.
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {

        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);


        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        syncImmediately(context);
    }
    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
