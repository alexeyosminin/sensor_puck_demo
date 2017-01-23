package com.osminin.sensorpuckdemo.presentation;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.osminin.sensorpuckdemo.ble.SPScannerInterface;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;
import com.osminin.sensorpuckdemo.presentation.interfaces.BasePresenter;
import com.osminin.sensorpuckdemo.presentation.interfaces.SPDetailsPresenter;
import com.osminin.sensorpuckdemo.ui.views.SPDetailsView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.osminin.sensorpuckdemo.Constants.SP_DISCOVERY_TIMEOUT;

/**
 * Created by osminin on 09.11.2016.
 */

public final class SPDetailsPresenterImpl implements SPDetailsPresenter, Observer<SensorPuckModel> {
    private static final String TAG = SPDetailsPresenterImpl.class.getSimpleName();

    private final SPScannerInterface mScanner;
    private SPDetailsView mView;
    private SensorPuckModel mModel;
    private Subscription mSubscription;

    @Inject
    public SPDetailsPresenterImpl(SPScannerInterface scanner) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "SPDetailsPresenterImpl()");
        mScanner = scanner;
    }

    @Override
    public void setView(SPDetailsView view) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "setView");
        mView = view;
    }

    public void setModel(SensorPuckModel model) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "setModel: " + model.getName());
        mModel = model;
    }

    public void startReceivingUpdates() {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "startReceivingUpdates");
        mSubscription = mScanner
                .startObserve()
                .filter(sensorPuckModel -> sensorPuckModel.equals(mModel))
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(SP_DISCOVERY_TIMEOUT, TimeUnit.MILLISECONDS)
                .subscribe(this);
    }

    public void stopReceivingUpdates() {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "stopReceivingUpdates");
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void onCompleted() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        mView.showError();
        FirebaseCrash.logcat(Log.ERROR, TAG, "onError");
        FirebaseCrash.report(e);
    }

    @Override
    public void onNext(SensorPuckModel spModel) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "onNext: " + spModel.getName());
        if (spModel.getHRMSample().size() > 0) {
            spModel.setHRMPrevSample(spModel.getHRMSample().get(spModel.getHRMSample().size() - 1));
        }
        mModel = spModel;
        mView.update(spModel);
    }
}