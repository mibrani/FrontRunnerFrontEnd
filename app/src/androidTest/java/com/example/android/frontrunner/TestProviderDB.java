package com.example.android.frontrunner;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.android.frontrunner.data.DBHelper;
import com.example.android.frontrunner.data.DataContract;

/**
 * Created by Melos on 9/21/2014.
 */
public class TestProviderDB extends AndroidTestCase {
    public void testGetType()
    {
        final String type = mContext.getContentResolver().getType(DataContract.Users.USERS_CONTENT_URI);
        assertEquals(DataContract.Users.CONTENT_DIR_TYPE,type);
    }
}
