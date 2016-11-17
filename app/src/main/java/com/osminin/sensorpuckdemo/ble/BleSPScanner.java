package com.osminin.sensorpuckdemo.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by osminin on 16.11.2016.
 */

public final class BleSPScanner {
    private Observable<SensorPuckModel> mBleScanner;

    @Inject
    public BleSPScanner(final BluetoothManager bluetoothManager) {
        mBleScanner = Observable.create(new Observable.OnSubscribe<SensorPuckModel>() {
            @Override
            public void call(final Subscriber<? super SensorPuckModel> subscriber) {
                BluetoothAdapter adapter = bluetoothManager.getAdapter();
                BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
                scanner.startScan(new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        if (SensorPuckParser.isSensorPuckRecord(result)) {
                            SensorPuckModel spModel = SensorPuckParser.parse(result);
                            subscriber.onNext(spModel);
                        }
                    }

                    @Override
                    public void onBatchScanResults(List<ScanResult> results) {
                        List<SensorPuckModel> spModels = SensorPuckParser.parseBatchResult(results);
                        for (SensorPuckModel spModel : spModels) {
                            subscriber.onNext(spModel);
                        }
                    }

                    @Override
                    public void onScanFailed(int errorCode) {
                        subscriber.onError(new Exception("error: " + errorCode));
                    }
                });
            }
        }).subscribeOn(Schedulers.computation());
    }

    public Subscription subscribe(Observer<? super SensorPuckModel> observer) {
        return mBleScanner.subscribe(observer);
    }
}
