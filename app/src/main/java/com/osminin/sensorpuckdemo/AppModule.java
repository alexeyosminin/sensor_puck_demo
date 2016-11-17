package com.osminin.sensorpuckdemo;

import android.app.Application;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.LayoutInflaterFactory;

import com.osminin.sensorpuckdemo.ble.BleSPScanner;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Provides
    @Singleton
    public BleSPScanner provideBleSPScanner() {
        return new BleSPScanner((BluetoothManager) app.getSystemService(Context.BLUETOOTH_SERVICE));
    }
}
