package com.example.android.frontrunner.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Melos on 9/28/2014.
 */
public class SyncService extends Service {
    private static final Object adapterLock = new Object();
    private static SyncAdapter syncAdapter = null;
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (adapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }
}
