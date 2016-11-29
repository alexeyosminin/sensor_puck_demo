package com.osminin.sensorpuckdemo;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.osminin.sensorpuckdemo.ble.BleSPScanner;
import com.osminin.sensorpuckdemo.ble.FakeSPScanner;
import com.osminin.sensorpuckdemo.ble.SPScannerInterface;

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
    public SPScannerInterface provideSPScanner() {
        //TODO: factory
        int mode = 1;
        switch (mode) {
            case 0:
                return new BleSPScanner((BluetoothManager) app.getSystemService(Context.BLUETOOTH_SERVICE));
            case 1:
                return new FakeSPScanner();
        }
        return null;
    }

    @Provides
    @Singleton
    public BluetoothManager provideBluetoothManager() {
        return (BluetoothManager) app.getSystemService(Context.BLUETOOTH_SERVICE);
    }
}
