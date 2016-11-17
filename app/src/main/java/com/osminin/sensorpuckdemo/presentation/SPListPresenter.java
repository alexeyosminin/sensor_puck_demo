package com.osminin.sensorpuckdemo.presentation;

import android.util.Log;

import com.osminin.sensorpuckdemo.ble.BleSPScanner;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import rx.Subscriber;

/**
 * Created by osminin on 08.11.2016.
 */

public class SPListPresenter extends Subscriber<SensorPuckModel> implements Presenter<SPListView> {
    private static final String TAG = SPListPresenter.class.getSimpleName();
    @Inject
    BleSPScanner mScanner;
    private SPListView mView;
    private Set<SensorPuckModel> mFoundSP;

    @Inject
    SPListPresenter() {
        mFoundSP = new HashSet<>();
    }

    @Override
    public void setView(SPListView view) {
        mView = view;
    }

    public void startScan() {
        add(mScanner.subscribe(this));
    }

    public void stopScan() {
        unsubscribe();
    }

    public void onDeviceSelected(SensorPuckModel model) {
        mView.showDetailsFragment(model);
    }

    @Override
    public void onCompleted() {
        Log.d(TAG, "onCompleted()");
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, e.toString());
    }

    @Override
    public void onNext(SensorPuckModel sensorPuckModel) {
        Log.d(TAG, "onNext()");
        mFoundSP.add(sensorPuckModel);
        mView.updateDeviceList(new ArrayList<>(mFoundSP));
    }
}
