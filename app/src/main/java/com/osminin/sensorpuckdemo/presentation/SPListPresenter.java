package com.osminin.sensorpuckdemo.presentation;

import android.os.Handler;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.osminin.sensorpuckdemo.ble.SPScannerInterface;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;
import com.osminin.sensorpuckdemo.model.UiSpModel;
import com.osminin.sensorpuckdemo.presentation.interfaces.BasePresenter;
import com.osminin.sensorpuckdemo.presentation.interfaces.SPListView;

import java.util.LinkedList;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.osminin.sensorpuckdemo.Constants.SP_DISCOVERY_TIMEOUT;

/**
 * Created by osminin on 08.11.2016.
 */

public class SPListPresenter implements BasePresenter<SPListView>, Observer<UiSpModel>{
    private static final String TAG = SPListPresenter.class.getSimpleName();
    @Inject
    SPScannerInterface mScanner;
    private SPListView mView;
    private Subscription mSubscription;
    private LinkedList<SensorPuckModel> mSpList;

    @Inject
    SPListPresenter() {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "SPListPresenter()");
        mSpList = new LinkedList<>();
    }

    @Override
    public void setView(SPListView view) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "setView()");
        mView = view;
    }

    public void startScan() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "startScan()");
        mSubscription = mScanner
                .startObserve()
                .map(spModel -> uiMapper(spModel))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    public void stopScan() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "stopScan()");
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

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
        //TODO: handle errors
        mView.showError();
        FirebaseCrash.logcat(Log.ERROR, TAG, "onError()");
        FirebaseCrash.report(e);
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
            result.setCommand(UiSpModel.UiCommand.ADD_NEW);
            result.setIndex(mSpList.size());
            mSpList.add(result.getIndex(), result.getModel());
        }
        return result;
    }
}
