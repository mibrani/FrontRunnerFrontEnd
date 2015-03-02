package com.example.android.frontrunner.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Melos on 9/21/2014.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "FrontRunner.db";
    private static int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_USERS_TABLE_SQL = "create table " + DataContract.Users.TABLE_NAME + "(" + DataContract.Users.COLUMN_USER_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT," + DataContract.Users.COLUMN_NAME + " TEXT NOT NULL," + DataContract.Users.COLUMN_NICKNAME +
                " TEXT, " + DataContract.Users.COLUMN_PHOTO + " TEXT, " + DataContract.Users.COLUMN_USERNAME + " TEXT, " +
        DataContract.Users.COLUMN_PASSWORD + ")";

        final String CREATE_GAMES_TABLE_SQL = "create table " + DataContract.Games.TABLE_NAME + "(" + DataContract.Games.COLUMN_GAME_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT," + DataContract.Games.COLUMN_GAME_NAME + " TEXT NOT NULL, " + DataContract.Games.COLUMN_PARTICIPANT + " INTEGER NOT NULL," + DataContract.Games.COLUMN_PARTICIPANT_STATUS +
                " INTEGER NOT NULL, " + DataContract.Games.COLUMN_LATITUDE + " DOUBLE, " + DataContract.Games.COLUMN_LONGITUDE + " DOUBLE, " +
                DataContract.Games.COLUMN_GAME_CREATOR + " INTEGER NOT NULL," +
                 "FOREIGN KEY (" + DataContract.Games.COLUMN_PARTICIPANT + ") REFERENCES " + DataContract.Users.TABLE_NAME
                + "(" + DataContract.Users._ID + "))";

        db.execSQL(CREATE_USERS_TABLE_SQL);
        db.execSQL(CREATE_GAMES_TABLE_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.Users.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.Games.TABLE_NAME);
        onCreate(db);
    }
}
