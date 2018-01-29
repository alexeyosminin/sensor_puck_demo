package com.osminin.sensorpuckdemo.presentation;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.osminin.sensorpuckdemo.ble.SPScannerInterface;
import com.osminin.sensorpuckdemo.error.SPError;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;
import com.osminin.sensorpuckdemo.presentation.interfaces.SPDetailsPresenter;
import com.osminin.sensorpuckdemo.ui.views.SPDetailsView;

import java.util.concurrent.TimeUnit;

import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.osminin.sensorpuckdemo.Constants.SP_DISCOVERY_TIMEOUT;

/**
 * Created by osminin on 09.11.2016.
 */

public final class SPDetailsPresenterImpl extends Subscriber<SensorPuckModel> implements SPDetailsPresenter {
    private static final String TAG = SPDetailsPresenterImpl.class.getSimpleName();

    private final SPScannerInterface mScanner;
    private SPDetailsView mView;
    private SensorPuckModel mModel;

    public SPDetailsPresenterImpl(SPScannerInterface scanner) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "SPDetailsPresenterImpl()");
        mScanner = scanner;
    }

    @Override
    public void bind(SPDetailsView view) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "bind");
        mView = view;
    }

    @Override
    public void setModel(SensorPuckModel model) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "setModel: " + model.getName());
        mModel = model;
    }

    @Override
    public void startReceivingUpdates() {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "startReceivingUpdates");
        mScanner
            .startObserve()
            .timeout(SP_DISCOVERY_TIMEOUT, TimeUnit.MILLISECONDS)
            .filter(sensorPuckModel -> sensorPuckModel.equals(mModel))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this);
    }

    @Override
    public void stopReceivingUpdates() {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "stopReceivingUpdates");
        if (!isUnsubscribed()) {
            unsubscribe();
        }
    }

    @Override
    public void onCompleted() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        mView.showError(SPError.CONNECTION_LOST);
        FirebaseCrash.logcat(Log.ERROR, TAG, "onError");
        FirebaseCrash.report(e);
    }

    @Override
    public void onNext(SensorPuckModel spModel) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "onNext: " + spModel.getName());
        mModel = spModel;
        mView.update(spModel);
    }
}
