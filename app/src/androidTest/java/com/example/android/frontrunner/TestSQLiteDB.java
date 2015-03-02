package com.example.android.frontrunner;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import com.example.android.frontrunner.data.DBHelper;

/**
 * Created by Melos on 9/21/2014.
 */
public class TestSQLiteDB extends AndroidTestCase {
    public void testCreateDB() {
        mContext.deleteDatabase(DBHelper.DATABASE_NAME);
        SQLiteDatabase db = new DBHelper(mContext).getWritableDatabase();
        assertEquals(true,db.isOpen());



    }
}
