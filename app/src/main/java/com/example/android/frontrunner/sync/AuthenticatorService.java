package com.example.android.frontrunner.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Melos on 9/21/2014.
 */
public class AuthenticatorService extends Service {
    private Authenticator authenticator;
    @Override
    public void onCreate() {
        authenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
