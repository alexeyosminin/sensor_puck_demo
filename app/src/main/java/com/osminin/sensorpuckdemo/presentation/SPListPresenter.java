package com.osminin.sensorpuckdemo.presentation;

import android.os.Handler;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.osminin.sensorpuckdemo.ble.SPScannerInterface;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;

import static com.osminin.sensorpuckdemo.Constants.SP_DISCOVERY_TIMEOUT;

/**
 * Created by osminin on 08.11.2016.
 */

public class SPListPresenter implements BasePresenter<SPListView>, Observer<SensorPuckModel> {
    private static final String TAG = SPListPresenter.class.getSimpleName();
    @Inject
    SPScannerInterface mScanner;
    private SPListView mView;
    private Handler mTimeoutHandler;
    private Subscription mSubscription;

    private List<SensorPuckModel> mSpList;
    private Runnable mTimeoutTask = new Runnable() {
        @Override
        public void run() {
            if (mView != null) {
                //remove last element by timeout
                SensorPuckModel spModel = mSpList.remove(0);
                mView.updateItemRemoved(0);
                FirebaseCrash.logcat(Log.VERBOSE, TAG, "last item is removed: " + spModel.getName());
            }
        }
    };

    @Inject
    SPListPresenter() {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "SPListPresenter()");
        mTimeoutHandler = new Handler();
        mSpList = new LinkedList<>();
    }

    @Override
    public void setView(SPListView view) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "setView()");
        mView = view;
    }

    public void startScan() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "startScan()");
        mSubscription = mScanner.subscribe(this);
    }

    public void stopScan() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "stopScan()");
        mSubscription.unsubscribe();
        mTimeoutHandler.removeCallbacks(mTimeoutTask);
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
        FirebaseCrash.logcat(Log.ERROR, TAG, "onError()");
        FirebaseCrash.report(e);
    }

    @Override
    public void onNext(final SensorPuckModel spModel) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "onNext item received : " + spModel.getName());
        //TODO: rework this code using rx!!
        mTimeoutHandler.removeCallbacks(mTimeoutTask);
        if (mSpList.size() == 0) {
            mSpList.add(spModel);
            mView.updateDeviceList(mSpList);
            FirebaseCrash.log("first item is added: " + spModel.getName());
        } else {
            Iterator<SensorPuckModel> it = mSpList.iterator();
            int i = 0;
            int positionToReplace = -1;
            while (it.hasNext()) {
                long currentTime = System.currentTimeMillis();
                SensorPuckModel cur = it.next();
                if (cur.equals(spModel)) {
                    positionToReplace = i;
                }
                if (currentTime - cur.getTimestamp() > SP_DISCOVERY_TIMEOUT) {
                    mView.updateItemRemoved(i);
                    it.remove();
                    positionToReplace = positionToReplace == i ? -1 : positionToReplace;
                    FirebaseCrash.log("item is removed: " + spModel.getName() +
                            " position: " + i);
                } else {
                    ++i;
                }
            }
            if (positionToReplace > -1) {
                mSpList.remove(positionToReplace);
                mSpList.add(positionToReplace, spModel);
                mView.updateItemChanged(positionToReplace);
                FirebaseCrash.log("item is replaced: " + spModel.getName() +
                        " position: " + positionToReplace);
            } else if (!mSpList.contains(spModel)) {
                mSpList.add(spModel);
                mView.updateItemInserted(mSpList.size() - 1);
                FirebaseCrash.log("new item is added: " + spModel.getName());
            }
        }
        mTimeoutHandler.postDelayed(mTimeoutTask, SP_DISCOVERY_TIMEOUT);
    }
}
