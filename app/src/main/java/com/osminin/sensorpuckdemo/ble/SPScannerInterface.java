package com.osminin.sensorpuckdemo.ble;

import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by osminin on 29.11.2016.
 */

public interface SPScannerInterface {
    Subscription subscribe(Observer<? super SensorPuckModel> observer);
    Subscription subscribe(Observer<? super SensorPuckModel> observer,
                           Func1<? super SensorPuckModel, Boolean> predicate);
}
