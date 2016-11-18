package com.osminin.sensorpuckdemo.presentation;

import com.osminin.sensorpuckdemo.ble.BleSPScanner;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;

/**
 * Created by osminin on 09.11.2016.
 */

public final class SPDetailsPresenter implements Presenter<SPDetailsView>, Observer<SensorPuckModel> {

    @Inject
    BleSPScanner mScanner;
    private SPDetailsView mView;
    private SensorPuckModel mModel;
    private Subscription mSubscription;

    @Inject
    public SPDetailsPresenter() {
    }

    @Override
    public void setView(SPDetailsView view) {
        mView = view;
    }

    public void setModel(SensorPuckModel model) {
        mModel = model;
    }

    public void startReceivingUpdates() {
        mSubscription = mScanner.subscribe(this);
    }

    public void stopReceivingUpdates() {
        mSubscription.unsubscribe();
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
        if (sensorPuckModel.equals(mModel)) {
            mView.update(sensorPuckModel);
        }
    }
}
