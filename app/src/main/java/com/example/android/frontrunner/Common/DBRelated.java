package com.example.android.frontrunner.Common;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.example.android.frontrunner.MainActivity;
import com.example.android.frontrunner.R;
import com.example.android.frontrunner.data.DataContract;
import com.example.android.frontrunner.entities.Game;
import com.example.android.frontrunner.entities.User;

/**
 * Created by Melos on 9/24/2014.
 */
public class DBRelated {

    //User table column to index mapping
    public static final String[] userColumns = {
            DataContract.Users.TABLE_NAME + "." + DataContract.Users.COLUMN_USER_ID,
            DataContract.Users.COLUMN_NAME,
            DataContract.Users.COLUMN_NICKNAME,
            DataContract.Users.COLUMN_PHOTO,
            DataContract.Users.COLUMN_USERNAME,
            DataContract.Users.COLUMN_PASSWORD};

    //User table column to index mapping
    public static final String[] gameColumns = {
            DataContract.Games.TABLE_NAME + "." + DataContract.Games.COLUMN_GAME_ID,
            DataContract.Games.COLUMN_GAME_NAME,
            DataContract.Games.COLUMN_PARTICIPANT,
            DataContract.Games.COLUMN_PARTICIPANT_STATUS,
    DataContract.Games.COLUMN_LATITUDE,
    DataContract.Games.COLUMN_LONGITUDE,
    DataContract.Games.COLUMN_GAME_CREATOR};


    public static final int USER_ID_COL_INDEX = 0;
    public static final int USER_NAME_COL_INDEX = 1;
    public static final int USER_NICKNAME_COL_INDEX = 2;
    public static final int USER_PHOTO_COL_INDEX = 3;
    public static final int USER_USERNAME_COL_INDEX = 4;
    public static final int USER_PASSWORD_COL_INDEX = 5;


    //Games table column to index mapping

    public static final int GAMES_ID_COL_INDEX = 0;
    public static final int GAMES_GAMENAME_COL_INDEX = 1;
    public static final int GAMES_PARTICIPANT_COL_INDEX = 2;
    public static final int GAMES_PARTICIPANTSTATUS_COL_INDEX = 3;
    public static final int GAMES_LATITUDE_COL_INDEX = 4;
    public static final int GAMES_LONGITUDE_COL_INDEX = 5;
    public static final int GAMES_GAME_CREATOR_COL_INDEX = 6;

    //Status text
    public static final String GAME_PARTICIPANT_STATUS_INVITED = "Invited";
    public static final String GAME_PARTICIPANT_STATUS_READY = "Ready";
    public static final String GAME_PARTICIPANT_STATUS_REJECTED = "Rejected";

    //List of participant status
    public static final int PARTICIPANT_STATUS_INVITED = 0;
    public static final int PARTICIPANT_STATUS_ACCEPTED = 1;
    public static final int PARTICIPANT_STATUS_REJECTED = 2;

    public static boolean authenticateUser() {
        String username = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.mainActivity).getString(MainActivity.mainActivity.getString(R.string.pref_key_username), MainActivity.mainActivity.getString(R.string.pref_default_username));
        String password = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.mainActivity).getString(MainActivity.mainActivity.getString(R.string.pref_key_password), MainActivity.mainActivity.getString(R.string.pref_default_password));

        Cursor cursor = MainActivity.mainActivity.getContentResolver().query(DataContract.Users.appendUsernameAndPassword(username, password), null, null, null, null);
        if (cursor.moveToFirst()) {
            //If user authenticated, assign application variable appUser
            MainActivity.appUser = DBRelated.getUserFromCursor(cursor);
            return true;
        } else

        {
            MainActivity.appUser = null;
            return false;
        }
    }

    public static boolean authenticateUser(Context appContext,String username, String password) {

        Cursor cursor = appContext.getContentResolver().query(DataContract.Users.appendUsernameAndPassword(username, password), null, null, null, null);
        if (cursor.moveToFirst()) {
            //If user authenticated, assign application variable appUser
            MainActivity.appUser = DBRelated.getUserFromCursor(cursor);
            return true;
        } else

        {
            MainActivity.appUser = null;
            return false;
        }
    }

    public static User getUserFromCursor(Cursor cursor) {
        try {
            User user = new User();
            user.setId(cursor.getInt(USER_ID_COL_INDEX));
            user.setName(cursor.getString(USER_NAME_COL_INDEX));
            user.setNickname(cursor.getString(USER_NICKNAME_COL_INDEX));
            user.setPhoto(cursor.getString(USER_PHOTO_COL_INDEX));
            user.setUserName(cursor.getString(USER_USERNAME_COL_INDEX));
            user.setPassword(cursor.getString(USER_PASSWORD_COL_INDEX));

            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static Game getGameFromCursor(Cursor cursor) {
        try {
            Game game = new Game();
            game.setId(cursor.getInt(GAMES_ID_COL_INDEX));
            game.setGame_name(cursor.getString(GAMES_GAMENAME_COL_INDEX));
            game.setParticipant(cursor.getInt(GAMES_PARTICIPANT_COL_INDEX));
            game.setParticipant_status(cursor.getInt(GAMES_PARTICIPANTSTATUS_COL_INDEX));
            game.setLatitude(cursor.getDouble(GAMES_LATITUDE_COL_INDEX));
            game.setLongitude(cursor.getDouble(GAMES_LONGITUDE_COL_INDEX));
            game.setGameCreator(cursor.getInt(GAMES_GAME_CREATOR_COL_INDEX));
            return game;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }


    public static void syncGame(Game game) {
        ContentValues gameEntries = new ContentValues();
        gameEntries.put(DataContract.Games.COLUMN_GAME_ID,game.getId());
        gameEntries.put(DataContract.Games.COLUMN_GAME_NAME, game.getGame_name());
        gameEntries.put(DataContract.Games.COLUMN_PARTICIPANT, game.getParticipant());
        gameEntries.put(DataContract.Games.COLUMN_PARTICIPANT_STATUS, game.getParticipant_status());
        gameEntries.put(DataContract.Games.COLUMN_GAME_CREATOR, game.getGameCreator());

        Cursor cursor = MainActivity.mainActivity.getContentResolver().query(DataContract.Games.GAMES_CONTENT_URI.buildUpon().appendPath(Integer.toString(game.getId())).build(),null,null,null,null);
        if(cursor.moveToFirst()) {//if the row exists, update values
            long nrRows = MainActivity.mainActivity.getContentResolver().update(DataContract.Games.GAMES_CONTENT_URI, gameEntries, DataContract.Games._ID + " = ?", new String[]{Integer.toString(game.getId())});
        }
        else
        {
            MainActivity.mainActivity.getContentResolver().insert(DataContract.Games.GAMES_CONTENT_URI, gameEntries);
        }


    }

    public static long syncUser(Context appContext, User user) {
        long nrRows;
        ContentValues userEntries = new ContentValues();
        userEntries.put(DataContract.Users._ID, user.getId());
        userEntries.put(DataContract.Users.COLUMN_NAME, user.getName());
        userEntries.put(DataContract.Users.COLUMN_NICKNAME, user.getNickname());
        userEntries.put(DataContract.Users.COLUMN_PHOTO, user.getPhoto());
        userEntries.put(DataContract.Users.COLUMN_USERNAME, user.getUserName());
        userEntries.put(DataContract.Users.COLUMN_PASSWORD, user.getPassword());

        Cursor cursor = appContext.getContentResolver().query(DataContract.Users.appendID(user.getId()), null, null, null, null);
        if(cursor.moveToFirst()) {//if the row exists, update values
            nrRows = appContext.getContentResolver().update(DataContract.Users.USERS_CONTENT_URI, userEntries, DataContract.Users._ID + " = ?", new String[]{Integer.toString(user.getId())});
        }
        else
        {//insert this row
            Uri locationInsertUri = appContext.getContentResolver()
                    .insert(DataContract.Users.USERS_CONTENT_URI, userEntries);

            nrRows =  ContentUris.parseId(locationInsertUri);
        }
        return nrRows;

    }

    public static String participantStatusCodeToText(Integer code) {
        String returnText ="";
        switch (code) {
            case PARTICIPANT_STATUS_INVITED:
                returnText =  GAME_PARTICIPANT_STATUS_INVITED;
                break;
            case PARTICIPANT_STATUS_ACCEPTED:
                returnText =  GAME_PARTICIPANT_STATUS_READY;
                break;
            case PARTICIPANT_STATUS_REJECTED:
                returnText =  GAME_PARTICIPANT_STATUS_REJECTED;
                break;
        }
        return returnText;
    }
}
