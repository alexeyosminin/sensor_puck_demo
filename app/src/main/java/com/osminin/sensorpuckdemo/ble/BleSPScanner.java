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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by osminin on 16.11.2016.
 */

@Singleton
public final class BleSPScanner implements SPScannerInterface {
    private static final String TAG = BleSPScanner.class.getSimpleName();
    private PublishSubject<ScanResult> mSubject = PublishSubject.create();
    private ScanCallback mScanCallback;
    private BluetoothLeScanner mScanner;
    private BluetoothManager mBluetoothManager;
    private boolean isRunning;

    @Inject
    public BleSPScanner(final BluetoothManager bluetoothManager) {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "BleSPScanner(): ");
        mBluetoothManager = bluetoothManager;
        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                FirebaseCrash.logcat(Log.VERBOSE, TAG, "onScanResult: " + result.getDevice().getAddress());
                mSubject.onNext(result);
            }

            @Override
            public void onScanFailed(int errorCode) {
                FirebaseCrash.logcat(Log.ERROR, TAG, "onScanFailed: " + errorCode);
                mSubject.onError(new Exception("error: " + errorCode));
            }
        };
    }

    @Override
    public Observable<SensorPuckModel> startObserve() {
        return mSubject
                .asObservable()
                .doOnSubscribe(() -> enableBluetooth())
                .filter(scanResult -> (SensorPuckParser.isSensorPuckRecord(scanResult)))
                .map(scanResult -> SensorPuckParser.parse(scanResult))
                .subscribeOn(Schedulers.computation())
                .unsubscribeOn(AndroidSchedulers.mainThread());
    }

    private void enableBluetooth() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "enableBluetooth()");
        if (!isRunning) {
            BluetoothAdapter adapter = mBluetoothManager.getAdapter();
            mScanner = adapter.getBluetoothLeScanner();
            if (mScanner == null) {
                throw new BleNotEnabledException();
            }
            mScanner.startScan(mScanCallback);
            isRunning = true;
        }
    }

    public void stopScanIfNoObservers() {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "stopScanIfNoObservers()");
        if (!mSubject.hasObservers()) {
            mScanner.stopScan(mScanCallback);
            isRunning = false;
            FirebaseCrash.logcat(Log.DEBUG, TAG, "stopped");
        }
    }
}
