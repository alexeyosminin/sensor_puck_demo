package com.osminin.sensorpuckdemo.presentation;

import android.os.Handler;

import com.osminin.sensorpuckdemo.ble.BleSPScanner;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;

import static com.osminin.sensorpuckdemo.Constants.SP_DISCOVERY_TIMEOUT;

/**
 * Created by osminin on 09.11.2016.
 */

public final class SPDetailsPresenter implements Presenter<SPDetailsView>, Observer<SensorPuckModel> {

    @Inject
    BleSPScanner mScanner;
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
        mTimeoutHandler.postDelayed(mDestroyViewTask, SP_DISCOVERY_TIMEOUT);
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
    }

    @Override
    public void onNext(SensorPuckModel sensorPuckModel) {
        mTimeoutHandler.removeCallbacksAndMessages(null);
        mTimeoutHandler.postDelayed(mDestroyViewTask, SP_DISCOVERY_TIMEOUT);
        mView.update(sensorPuckModel);
    }

    private Runnable mDestroyViewTask = new Runnable() {
        @Override
        public void run() {
            if (mView != null) {
                mView.showError();
            }
        }
    };
}
