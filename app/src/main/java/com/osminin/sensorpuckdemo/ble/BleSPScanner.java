package com.osminin.sensorpuckdemo.ble;

import android.content.Context;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;
import com.polidea.rxandroidble.RxBleClient;

import javax.inject.Singleton;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by osminin on 16.11.2016.
 */

@Singleton
public final class BleSPScanner implements SPScannerInterface {
    private static final String TAG = BleSPScanner.class.getSimpleName();
    private RxBleClient rxBleClient;

    public BleSPScanner(final Context context) {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "BleSPScanner(): ");
        rxBleClient = RxBleClient.create(context);
    }

    @Override
    public Observable<SensorPuckModel> startObserve() {
        return rxBleClient
                .scanBleDevices()
                .observeOn(Schedulers.computation())
                .filter(SensorPuckParser::isSensorPuckRecord)
                .map(SensorPuckParser::parse);
    }
}
