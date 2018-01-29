package com.osminin.sensorpuckdemo.presentation;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.osminin.sensorpuckdemo.ble.SPScannerInterface;
import com.osminin.sensorpuckdemo.error.SPError;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;
import com.osminin.sensorpuckdemo.model.UiSpModel;
import com.osminin.sensorpuckdemo.presentation.interfaces.SPListPresenter;
import com.osminin.sensorpuckdemo.ui.views.SPListView;
import com.polidea.rxandroidble.exceptions.BleScanException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

import static com.osminin.sensorpuckdemo.Constants.REQUEST_ENABLE_BT;
import static com.osminin.sensorpuckdemo.Constants.REQUEST_ENABLE_LOCATION;
import static com.osminin.sensorpuckdemo.Constants.SP_DISCOVERY_TIMEOUT;
import static com.osminin.sensorpuckdemo.model.UiSpModel.UiCommand.ADD_NEW;
import static com.polidea.rxandroidble.exceptions.BleScanException.BLUETOOTH_CANNOT_START;
import static com.polidea.rxandroidble.exceptions.BleScanException.BLUETOOTH_DISABLED;
import static com.polidea.rxandroidble.exceptions.BleScanException.BLUETOOTH_NOT_AVAILABLE;
import static com.polidea.rxandroidble.exceptions.BleScanException.LOCATION_PERMISSION_MISSING;
import static com.polidea.rxandroidble.exceptions.BleScanException.LOCATION_SERVICES_DISABLED;

/**
 * Created by osminin on 08.11.2016.
 */

public class SPListPresenterImpl extends Subscriber<List<SensorPuckModel>> implements SPListPresenter {
    private static final String TAG = SPListPresenterImpl.class.getSimpleName();
    private final SPScannerInterface mScanner;
    private SPListView mView;

    public SPListPresenterImpl(SPScannerInterface scannerInterface) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "SPListPresenterImpl()");
        mScanner = scannerInterface;
    }

    @Override
    public void setView(SPListView view) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "setView()");
        mView = view;
    }

    @Override
    public void startScan() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "startScan()");
        mScanner.startObserve()
                .filter(spModel -> spModel != null)
                .buffer(SP_DISCOVERY_TIMEOUT, TimeUnit.MILLISECONDS)
                .flatMap(list -> Observable.from(list).distinct()
                        .toSortedList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void stopScan() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "stopScan()");
        if (!isUnsubscribed()) {
            unsubscribe();
        }
    }

    @Override
    public void onScannerFunctionalityEnabled(int requestCode, boolean isEnabled) {
        if (isEnabled) {
            startScan();
        } else {
            switch (requestCode) {
                case REQUEST_ENABLE_BT:
                    mView.showError(SPError.BLE_NOT_ENABLED);
                    break;
                case REQUEST_ENABLE_LOCATION:
                    mView.showError(SPError.LOCATION_NOT_ENABLED);
                    break;
            }
        }
    }

    @Override
    public void onSettingsChanged() {
        mView.restartWithNewConfig();
    }

    @Override
    public void onDeviceSelected(SensorPuckModel model) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "onDeviceSelected: " + model.getName());
        mView.showDetailsFragment(model);
    }

    @Override
    public void onCompleted() {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "onCompleted()");
    }

    @Override
    public void onError(Throwable e) {
        if (BleScanException.class.equals(e.getClass())) {
            BleScanException bleScanException = (BleScanException) e;
            handleBleException(bleScanException);
        } else {
            mView.showError(SPError.COMMON_ERROR);
            FirebaseCrash.logcat(Log.ERROR, TAG, "onError()");
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void onNext(List<SensorPuckModel> list) {
        mView.updateItems(list);
    }

    private void handleBleException(BleScanException e) {
        switch (e.getReason()) {
            case BLUETOOTH_CANNOT_START:
                mView.showError(SPError.COMMON_ERROR);
                break;
            case BLUETOOTH_DISABLED:
                mView.showEnableBluetoothDialog();
                break;
            case BLUETOOTH_NOT_AVAILABLE:
                mView.showError(SPError.BLE_NOT_AVAILABLE);
                break;
            case LOCATION_PERMISSION_MISSING:
                mView.showLocationPermissionDialog();
                break;
            case LOCATION_SERVICES_DISABLED:
                mView.showEnableLocationDialog();
                break;
        }
    }
}
