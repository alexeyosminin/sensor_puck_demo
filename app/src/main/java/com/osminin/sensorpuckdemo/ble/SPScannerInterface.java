package com.osminin.sensorpuckdemo.ble;

import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import rx.Observable;

/**
 * Created by osminin on 29.11.2016.
 */

public interface SPScannerInterface {
    Observable<SensorPuckModel> startObserve();
    void stopObserve();
    boolean isPermissionGranted();
    boolean isEnabled();
}
