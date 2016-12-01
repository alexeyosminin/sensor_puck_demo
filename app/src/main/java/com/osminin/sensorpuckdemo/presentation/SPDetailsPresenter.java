package com.osminin.sensorpuckdemo.presentation;

import android.os.Handler;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.osminin.sensorpuckdemo.ble.SPScannerInterface;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
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
    private Handler mTimeoutHandler;

    @Inject
    public SPDetailsPresenter() {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "SPDetailsPresenter()");
        mTimeoutHandler = new Handler();
    }

    @Override
    public void setView(SPDetailsView view) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "setView");
        mView = view;
    }

    public void setModel(SensorPuckModel model) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "setModel: " + model.getName());
        mModel = model;
        mTimeoutHandler.postDelayed(mTimeoutTask, SP_DISCOVERY_TIMEOUT);
    }

    public void startReceivingUpdates() {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "startReceivingUpdates");
        mSubscription = mScanner.subscribe(this, new Func1<SensorPuckModel, Boolean>() {
            @Override
            public Boolean call(SensorPuckModel sensorPuckModel) {
                return sensorPuckModel.equals(mModel);
            }
        });
    }

    public void stopReceivingUpdates() {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "stopReceivingUpdates");
        mSubscription.unsubscribe();
        mTimeoutHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onCompleted() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        FirebaseCrash.logcat(Log.ERROR, TAG, "onError");
        FirebaseCrash.report(e);
    }

    @Override
    public void onNext(SensorPuckModel spModel) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "onNext: " + spModel.getName());
        mTimeoutHandler.removeCallbacksAndMessages(null);
        mTimeoutHandler.postDelayed(mTimeoutTask, SP_DISCOVERY_TIMEOUT);
        if (spModel.getHRMSample().size() > 0) {
            spModel.setHRMPrevSample(spModel.getHRMSample().get(spModel.getHRMSample().size() - 1));
        }
        mModel = spModel;
        mView.update(spModel);
    }

    private Runnable mTimeoutTask = new Runnable() {
        @Override
        public void run() {
            FirebaseCrash.logcat(Log.DEBUG, TAG, "mTimeoutTask");
            if (mView != null) {
                mView.showError();
            }
        }
    };
}
