package com.osminin.sensorpuckdemo.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.osminin.sensorpuckdemo.exceptions.BleNotEnabledException;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by osminin on 16.11.2016.
 */

@Singleton
public final class BleSPScanner implements SPScannerInterface {
    private static final String TAG = BleSPScanner.class.getSimpleName();
    private final PublishSubject<ScanResult> mSubject = PublishSubject.create();
    private final ScanCallback mScanCallback;
    private BluetoothLeScanner mScanner;
    private final BluetoothAdapter mAdapter;
    private boolean isRunning;

    @Inject
    public BleSPScanner(final BluetoothManager bluetoothManager) {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "BleSPScanner(): ");
        mAdapter = bluetoothManager.getAdapter();
        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                FirebaseCrash.logcat(Log.VERBOSE, TAG, "onScanResult: " + result.getDevice().getAddress());
                mSubject.onNext(result);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                FirebaseCrash.logcat(Log.VERBOSE, TAG, "onBatchScanResults()");
                for (ScanResult result : results) {
                    mSubject.onNext(result);
                }
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
                .observeOn(Schedulers.computation())
                .doOnSubscribe(this::enableBluetooth)
                .filter(scanResult -> (SensorPuckParser.isSensorPuckRecord(scanResult)))
                .map(SensorPuckParser::parse);
    }

    private void enableBluetooth() {
        if (!isRunning) {
            FirebaseCrash.logcat(Log.DEBUG, TAG, "enableBluetooth()");
            mScanner = mAdapter.getBluetoothLeScanner();
            if (mScanner == null) {
                throw new BleNotEnabledException();
            }
            ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
            scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
            ScanSettings scanSettings = scanSettingsBuilder.build();
            mScanner.startScan(mScanCallback);
            isRunning = true;
        }
    }

    @Override
    public void stopObserve() {
        if (!mSubject.hasObservers() && isRunning &&
                mAdapter.getState() == BluetoothAdapter.STATE_ON) {
            FirebaseCrash.logcat(Log.VERBOSE, TAG, "stopObserve()");
            mScanner.stopScan(mScanCallback);
            isRunning = false;
            FirebaseCrash.logcat(Log.DEBUG, TAG, "stopped");
        }
    }

    @Override
    public boolean isEnabled() {
        return mAdapter.isEnabled();
    }
}
