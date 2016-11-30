package com.osminin.sensorpuckdemo.presentation;

import android.os.Handler;

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

    @Inject
    SPScannerInterface mScanner;
    private SPDetailsView mView;
    private SensorPuckModel mModel;
    private Subscription mSubscription;
    private Handler mTimeoutHandler;

    @Inject
    public SPDetailsPresenter() {
        mTimeoutHandler = new Handler();
    }

    @Override
    public void setView(SPDetailsView view) {
        mView = view;
    }

    public void setModel(SensorPuckModel model) {
        mModel = model;
        mTimeoutHandler.postDelayed(mTimeoutTask, SP_DISCOVERY_TIMEOUT);
    }

    public void startReceivingUpdates() {
        mSubscription = mScanner.subscribe(this, new Func1<SensorPuckModel, Boolean>() {
            @Override
            public Boolean call(SensorPuckModel sensorPuckModel) {
                return sensorPuckModel.equals(mModel);
            }
        });
    }

    public void stopReceivingUpdates() {
        mSubscription.unsubscribe();
        mTimeoutHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onCompleted() {
        //todo:
    }

    @Override
    public void onError(Throwable e) {
        //todo:
        e.printStackTrace();
    }

    @Override
    public void onNext(SensorPuckModel sensorPuckModel) {
        mTimeoutHandler.removeCallbacksAndMessages(null);
        mTimeoutHandler.postDelayed(mTimeoutTask, SP_DISCOVERY_TIMEOUT);
        if (sensorPuckModel.getHRMSample().size() > 0) {
            sensorPuckModel.setHRMPrevSample(sensorPuckModel.getHRMSample().get(sensorPuckModel.getHRMSample().size() - 1));
        }
        mModel = sensorPuckModel;
        mView.update(sensorPuckModel);
    }

    private Runnable mTimeoutTask = new Runnable() {
        @Override
        public void run() {
            if (mView != null) {
                mView.showError();
            }
        }
    };
}
