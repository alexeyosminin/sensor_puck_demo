package com.osminin.sensorpuckdemo.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observer;
import rx.Subscription;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by osminin on 16.11.2016.
 */

public final class BleSPScanner {
    private PublishSubject<SensorPuckModel> mSubject = PublishSubject.create();
    private ScanCallback mScanCallback;
    private BluetoothLeScanner mScanner;
    private boolean isRunning;

    @Inject
    @Singleton
    public BleSPScanner(final BluetoothManager bluetoothManager) {
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        mScanner = adapter.getBluetoothLeScanner();
        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                if (SensorPuckParser.isSensorPuckRecord(result)) {
                    SensorPuckModel spModel = SensorPuckParser.parse(result);
                    mSubject.onNext(spModel);
                }
                stopScanIfNoObservers();
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                List<SensorPuckModel> spModels = SensorPuckParser.parseBatchResult(results);
                for (SensorPuckModel spModel : spModels) {
                    mSubject.onNext(spModel);
                }
                stopScanIfNoObservers();
            }

            @Override
            public void onScanFailed(int errorCode) {
                mSubject.onError(new Exception("error: " + errorCode));
                stopScanIfNoObservers();
            }
        };
    }

    public Subscription subscribe(Observer<? super SensorPuckModel> observer) {
        return mSubject
                .asObservable()
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (!isRunning) {
                            mScanner.startScan(mScanCallback);
                            isRunning = true;
                        }
                    }
                })
                .subscribeOn(Schedulers.immediate())
                .subscribe(observer);
    }

    private void stopScanIfNoObservers() {
        if (!mSubject.hasObservers()) {
            mScanner.stopScan(mScanCallback);
            isRunning = false;
        }
    }
}
