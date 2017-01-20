package com.osminin.sensorpuckdemo.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.osminin.sensorpuckdemo.exceptions.BleNotEnabledException;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by osminin on 16.11.2016.
 */

@Singleton
public final class BleSPScanner implements SPScannerInterface {
    private static final String TAG = BleSPScanner.class.getSimpleName();
    private PublishSubject<SensorPuckModel> mSubject = PublishSubject.create();
    private ScanCallback mScanCallback;
    private BluetoothLeScanner mScanner;
    private boolean isRunning;

    @Inject
    public BleSPScanner(final BluetoothManager bluetoothManager) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "BleSPScanner(): ");
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        mScanner = adapter.getBluetoothLeScanner();
        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                FirebaseCrash.logcat(Log.VERBOSE, TAG, "onScanResult: " + result.getDevice().getAddress());
                if (SensorPuckParser.isSensorPuckRecord(result)) {
                    SensorPuckModel spModel = SensorPuckParser.parse(result);
                    mSubject.onNext(spModel);
                }
                stopScanIfNoObservers();
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                FirebaseCrash.logcat(Log.VERBOSE, TAG, "onScanResult: ");
                List<SensorPuckModel> spModels = SensorPuckParser.parseBatchResult(results);
                for (SensorPuckModel spModel : spModels) {
                    mSubject.onNext(spModel);
                }
                stopScanIfNoObservers();
            }

            @Override
            public void onScanFailed(int errorCode) {
                FirebaseCrash.logcat(Log.ERROR, TAG, "onScanFailed: " + errorCode);
                mSubject.onError(new Exception("error: " + errorCode));
                stopScanIfNoObservers();
            }
        };
    }

    @Override
    public Observable<SensorPuckModel> startObserve() {
        return mSubject
                .asObservable()
                .doOnSubscribe(() -> {
                    if (mScanner == null) {
                        throw new BleNotEnabledException();
                    }
                    if (!isRunning) {
                        mScanner.startScan(mScanCallback);
                        isRunning = true;
                    }
                })
                .subscribeOn(Schedulers.immediate());
    }

    private void stopScanIfNoObservers() {
        if (!mSubject.hasObservers()) {
            mScanner.stopScan(mScanCallback);
            isRunning = false;
            FirebaseCrash.logcat(Log.DEBUG, TAG, "stopped");
        }
    }
}
