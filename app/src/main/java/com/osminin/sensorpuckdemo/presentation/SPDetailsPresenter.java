package com.osminin.sensorpuckdemo.presentation;

import android.os.Handler;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.osminin.sensorpuckdemo.ble.SPScannerInterface;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

import static com.osminin.sensorpuckdemo.Constants.SP_DISCOVERY_TIMEOUT;

/**
 * Created by osminin on 09.11.2016.
 */

public final class SPDetailsPresenter implements BasePresenter<SPDetailsView>, Observer<SensorPuckModel> {
    private static final String TAG = SPDetailsPresenter.class.getSimpleName();

    @Inject
    SPScannerInterface mScanner;
    private SPDetailsView mView;
    private SensorPuckModel mModel;
    private Subscription mSubscription;

    @Inject
    public SPDetailsPresenter() {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "SPDetailsPresenter()");
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
