package com.example.android.frontrunner.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Melos on 9/21/2014.
 */
public class appContentProvider extends ContentProvider {

    private static final int USERS_QUERY = 1;
    private static final int USER_ID_QUERY = 2;
    private static final int USER_LOGIN = 3;
    private static final int USER_BY_USERNAME_QUERY = 4;
    private static final int GAMES_QUERY = 10;
    private static final int GAMES_ID_QUERY = 11;
    private static final int GAME_BY_USER = 20;
    private static final int GAME_BY_USER_BY_STATUS = 21;
    private static final int GAMEID_BY_GAME_AND_USER = 22;

    private static final UriMatcher uriMatcher = matchUri();
    private DBHelper dbHelper;

    private static UriMatcher matchUri() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.Users.PATH_LOCATION, USERS_QUERY);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.Users.PATH_LOCATION + "/*/*", USER_LOGIN);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.Users.PATH_LOCATION + "/#", USER_ID_QUERY);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.Users.PATH_LOCATION + "/*", USER_BY_USERNAME_QUERY);

        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.Games.PATH_LOCATION, GAMES_QUERY);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.Games.PATH_LOCATION + "/#", GAMES_ID_QUERY);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.Games.PATH_LOCATION + "/by_User_ID/#", GAME_BY_USER);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.Games.PATH_LOCATION + "/#/#", GAME_BY_USER_BY_STATUS);

        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.Games.PATH_LOCATION + "/*/#", GAMEID_BY_GAME_AND_USER);

        return uriMatcher;

    }

    @Override
    public boolean onCreate() {


        dbHelper = new DBHelper(getContext());


        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;
        //Join Games and Users table
        SQLiteQueryBuilder gameUsers = new SQLiteQueryBuilder();
        gameUsers.setTables(DataContract.Games.TABLE_NAME + " INNER JOIN " + DataContract.Users.TABLE_NAME + " ON " +
                DataContract.Games.TABLE_NAME + "." + DataContract.Games.COLUMN_PARTICIPANT + " = " + DataContract.Users.TABLE_NAME +
                "." + DataContract.Users._ID);


        switch (uriMatcher.match(uri)) {
            case USERS_QUERY: {
                returnCursor = dbHelper.getReadableDatabase().query(
                        DataContract.Users.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case USER_LOGIN: {
                returnCursor = dbHelper.getReadableDatabase().query(
                        DataContract.Users.TABLE_NAME,
                        projection,
                        DataContract.Users.COLUMN_USERNAME + "= ? AND " + DataContract.Users.COLUMN_PASSWORD + " = ?",
                        new String[]{
                                uri.getPathSegments().get(1), uri.getPathSegments().get(2)},
                        null,
                        null,
                        sortOrder);
                break;


            }

            case USER_ID_QUERY: {
                returnCursor = dbHelper.getReadableDatabase().query(
                        DataContract.Users.TABLE_NAME,
                        projection,
                        DataContract.Users._ID + "= '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case USER_BY_USERNAME_QUERY: {
                returnCursor = dbHelper.getReadableDatabase().query(
                        DataContract.Users.TABLE_NAME,
                        projection,
                        DataContract.Users.COLUMN_USERNAME + " = '" + uri.getPathSegments().get(1) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case GAMES_QUERY: {
                returnCursor = dbHelper.getReadableDatabase().query(
                        DataContract.Games.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case GAMES_ID_QUERY: {
                returnCursor = dbHelper.getReadableDatabase().query(
                        DataContract.Games.TABLE_NAME,
                        projection,
                        DataContract.Games._ID + "= '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case GAME_BY_USER: {
                Long userID = ContentUris.parseId(uri);
                String newSelection = DataContract.Users.TABLE_NAME + "." + DataContract.Users.COLUMN_USER_ID + " = ?";
                String newSelectionArgs[] = {userID.toString()};
                returnCursor = gameUsers.query(
                        dbHelper.getReadableDatabase(),
                        projection,
                        newSelection,
                        newSelectionArgs,
                        null,
                        null,
                        sortOrder)
                ;
                break;
            }
            case GAME_BY_USER_BY_STATUS: {
                returnCursor = dbHelper.getReadableDatabase().query(
                        DataContract.Games.TABLE_NAME,
                        projection,
                        DataContract.Games.COLUMN_PARTICIPANT + " = ? AND " + DataContract.Games.COLUMN_PARTICIPANT_STATUS + " = ?",
                        new String[]{uri.getPathSegments().get(1),uri.getPathSegments().get(2)},
                        null,
                        null,
                        sortOrder
                );

                break;
            }
            case GAMEID_BY_GAME_AND_USER: {
                returnCursor = dbHelper.getReadableDatabase().query(
                        DataContract.Games.TABLE_NAME,
                        projection,
                        DataContract.Games.COLUMN_GAME_NAME + " = ? AND " + DataContract.Games.COLUMN_PARTICIPANT + " = ?",
                        new String[]{uri.getPathSegments().get(1),uri.getPathSegments().get(2)},
                        null,
                        null,
                        sortOrder
                );

                break;
            }
            default:
                return null;


        }
        returnCursor.setNotificationUri(this.getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Override
    public String getType(Uri uri) {
        int mimeType = uriMatcher.match(uri);

        switch (mimeType) {
            case USERS_QUERY:
                return DataContract.Users.CONTENT_DIR_TYPE;
            case USER_ID_QUERY:
                return DataContract.Users.CONTENT_ITEM_TYPE;
            case GAMES_QUERY:
                return DataContract.Games.CONTENT_DIR_TYPE;
            case GAMES_ID_QUERY:
                return DataContract.Games.CONTENT_ITEM_TYPE;
            case GAME_BY_USER:
                return DataContract.Games.CONTENT_ITEM_TYPE;

            default:
                return null;
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;
        switch (uriMatcher.match(uri)) {
            case USERS_QUERY: {
                long insertedID = db.insert(DataContract.Users.TABLE_NAME, null, values);
                returnUri = DataContract.Users.appendID(insertedID);

                break;

            }

            case GAMES_QUERY: {
                long insertedID = db.insert(DataContract.Games.TABLE_NAME, null, values);
                returnUri = DataContract.Games.appendID(insertedID);
                break;
            }
            default:
                returnUri = null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;
        int rowsAffected = 0;
        switch (uriMatcher.match(uri)) {
            case GAMES_QUERY:
                rowsAffected = db.delete(DataContract.Games.TABLE_NAME, selection, selectionArgs);
                break;
            case USERS_QUERY:
                rowsAffected = db.delete(DataContract.Users.TABLE_NAME, selection, selectionArgs);
        }
        if (rowsAffected > 0) getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;
        int rowsAffected = 0;
        switch (uriMatcher.match(uri)) {
            case GAMES_QUERY:
                rowsAffected = db.update(DataContract.Games.TABLE_NAME, values, selection, selectionArgs);
                break;
            case USERS_QUERY:
                rowsAffected = db.update(DataContract.Users.TABLE_NAME, values, selection, selectionArgs);
        }
        if (rowsAffected > 0) getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;
        int rowsAffected = 0;
        switch (uriMatcher.match(uri)) {
            case USERS_QUERY: {
                try {

                    db.beginTransaction();

                    for (ContentValues value : values)
                        rowsAffected += db.insert(DataContract.Users.TABLE_NAME, null, value);
                } finally {
                    db.setTransactionSuccessful();
                    db.endTransaction();
                }

                break;
            }
        }
        return rowsAffected;
    }
}
