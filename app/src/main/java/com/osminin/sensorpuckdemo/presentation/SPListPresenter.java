package com.osminin.sensorpuckdemo.presentation;

import android.os.Handler;
import android.util.Log;

import com.osminin.sensorpuckdemo.ble.BleSPScanner;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;

import static com.osminin.sensorpuckdemo.Constants.SP_DISCOVERY_TIMEOUT;

/**
 * Created by osminin on 08.11.2016.
 */

public class SPListPresenter implements Presenter<SPListView>, Observer<SensorPuckModel> {
    private static final String TAG = SPListPresenter.class.getSimpleName();
    @Inject
    BleSPScanner mScanner;
    private SPListView mView;
    private Map<SensorPuckModel, Handler> mFoundSP;
    private Subscription mSubscription;

    @Inject
    SPListPresenter() {
        mFoundSP = new HashMap<>();
    }

    @Override
    public void setView(SPListView view) {
        mView = view;
    }

    public void startScan() {
        mSubscription = mScanner.subscribe(this);
    }

    public void stopScan() {
        mSubscription.unsubscribe();
        freeDeviceMap();
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
    public void onNext(final SensorPuckModel sensorPuckModel) {
        Log.d(TAG, "onNext()");
        if (mFoundSP.containsKey(sensorPuckModel)) {
            Handler handler = mFoundSP.remove(sensorPuckModel);
            handler.removeCallbacksAndMessages(null);
        }
        Handler selfRemoveHandler = new Handler();
        selfRemoveHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFoundSP.remove(sensorPuckModel);
                if (mView != null) {
                    mView.updateDeviceList(new ArrayList<>(mFoundSP.keySet()));
                }
            }
        }, SP_DISCOVERY_TIMEOUT);
        mFoundSP.put(sensorPuckModel, selfRemoveHandler);

        mView.updateDeviceList(new ArrayList<>(mFoundSP.keySet()));
    }

    private void freeDeviceMap() {
        Iterator<Map.Entry<SensorPuckModel, Handler>> it = mFoundSP.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().removeCallbacksAndMessages(null);
            it.remove();
        }
    }
}
