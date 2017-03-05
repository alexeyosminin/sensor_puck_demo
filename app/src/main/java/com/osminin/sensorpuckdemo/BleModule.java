package com.osminin.sensorpuckdemo;

import android.content.Context;
import android.content.SharedPreferences;

import com.osminin.sensorpuckdemo.ble.BleSPScanner;
import com.osminin.sensorpuckdemo.ble.FakeSPScanner;
import com.osminin.sensorpuckdemo.ble.SPScannerInterface;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * TODO: Add a class header comment!
 */

@Module
public class BleModule {

    @Provides
    @Singleton
    SPScannerInterface provideSPScanner(Context context, SharedPreferences sharedPreferences) {
        String realMode = context.getString(R.string.settings_mode_real);
        String mode = sharedPreferences.getString(context.getString(R.string.settings_mode_key), realMode);
        if (realMode.equals(mode)) {
            return new BleSPScanner(context);
        } else {
            String fakeCount = sharedPreferences.getString(context.getString(R.string.settings_fake_count_key), "10");
            return new FakeSPScanner(Integer.parseInt(fakeCount));
        }
    }
}
