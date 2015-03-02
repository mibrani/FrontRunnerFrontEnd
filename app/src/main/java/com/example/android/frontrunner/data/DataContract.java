package com.example.android.frontrunner.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Melos on 9/21/2014.
 */
public class DataContract {

    public static final String HTTP_BASE_URL = "http://beta-frontrunner.rhcloud.com/";
    public static final String USERS_URL = "GetUsers";
    public static final String CREATE_GAME_URL = "CreateGame";
    public static final String UPDATE_GAME_URL = "UpdateGame";
    public static final String GAMES_URL = "GetGames";
    public static final String CONTENT_AUTHORITY = "com.example.android.frontrunner";
    //public static final String HTTP_APP_NAME = "FrontRunnerBackEnd/";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public final static class Users implements BaseColumns {

        public static final String HTTP_USERS_URL = HTTP_BASE_URL + USERS_URL;

        public static final String COLUMN_USER_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_NICKNAME = "nickname";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String TABLE_NAME = "users";
        public static final String PATH_LOCATION = "users";
        public final static  Uri USERS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_DIR_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static Uri appendID(long ID)
        {
            return ContentUris.withAppendedId(USERS_CONTENT_URI,ID);
        }

        public static Uri appendUsernameAndPassword(String username, String password)
        {
            return USERS_CONTENT_URI.buildUpon().appendPath(username).appendPath(password).build();
        }

    }

    public final static class Games implements BaseColumns {
        public static final String HTTP_GAMES_URL = HTTP_BASE_URL +  GAMES_URL;

        public static final String COLUMN_GAME_ID = "_id";
        public static final String COLUMN_GAME_NAME = "game_name";
        public static final String COLUMN_PARTICIPANT = "participant";
        public static final String COLUMN_PARTICIPANT_STATUS = "participant_status";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_GAME_CREATOR = "gameCreator";



        public static final String TABLE_NAME = "games";
        public static final String PATH_LOCATION = "games";

        public final static  Uri GAMES_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_DIR_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static Uri appendID(long ID)
        {
            return ContentUris.withAppendedId(GAMES_CONTENT_URI,ID);
        }

    }


}
