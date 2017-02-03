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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
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

public class SPListPresenterImpl implements SPListPresenter, Observer<UiSpModel>{
    private static final String TAG = SPListPresenterImpl.class.getSimpleName();
    private final SPScannerInterface mScanner;
    private SPListView mView;
    private Subscription mSubscription;
    private final LinkedList<SensorPuckModel> mSpList;

    @Inject
    public SPListPresenterImpl(SPScannerInterface scannerInterface) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "SPListPresenterImpl()");
        mSpList = new LinkedList<>();
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
        mSubscription = mScanner
                .startObserve()
                .timeout(SP_DISCOVERY_TIMEOUT, TimeUnit.MILLISECONDS)
                .map(this::uiMapper)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void stopScan() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "stopScan()");
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        mSpList.clear();
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
        if (TimeoutException.class.equals(e.getClass())) {
            restartAfterTimeout();
        } else if (BleScanException.class.equals(e.getClass())) {
            BleScanException bleScanException = (BleScanException) e;
            handleBleException(bleScanException);
        } else {
            FirebaseCrash.logcat(Log.ERROR, TAG, "onError()");
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void onNext(UiSpModel uiSpModel) {
        switch (uiSpModel.getCommand()) {
            case ADD_NEW:
                mView.updateItemInserted(uiSpModel.getIndex(), uiSpModel.getModel());
                break;
            case REPLACE:
                mView.updateItemChanged(uiSpModel.getIndex(), uiSpModel.getModel());
                break;
            case REMOVE:
                mView.updateItemRemoved(uiSpModel.getIndex());
                break;
        }
    }

    private UiSpModel uiMapper(SensorPuckModel spModel) {
        int index = mSpList.indexOf(spModel);
        UiSpModel result = new UiSpModel();
        result.setModel(spModel);
        result.setIndex(index);
        if (index != -1) {
            // spModel is present in mSpList so it should be updated or removed if
            // its discovery time is more than SP_DISCOVERY_TIMEOUT
            SensorPuckModel cur = mSpList.get(index);
            long currentTime = System.currentTimeMillis();
            if (currentTime - cur.getTimestamp() > SP_DISCOVERY_TIMEOUT) {
                result.setCommand(UiSpModel.UiCommand.REMOVE);
                mSpList.remove(result.getIndex());
            } else {
                result.setCommand(UiSpModel.UiCommand.REPLACE);
                mSpList.remove(result.getIndex());
                mSpList.add(result.getIndex(), result.getModel());
            }
        } else {
            // it means that we have new one sp device so it should be just added
            result.setCommand(ADD_NEW);
            result.setIndex(mSpList.size());
            mSpList.add(result.getIndex(), result.getModel());
        }
        return result;
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

    private void restartAfterTimeout() {
        if (mSpList.size() != 0) {
            mSpList.clear();
            mView.updateAllItemsRemoved();
            mView.showError(SPError.CONNECTION_LOST);
        }
        startScan();
    }
}
