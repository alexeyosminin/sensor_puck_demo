package com.osminin.sensorpuckdemo.presentation;

import android.os.Handler;
import android.util.Log;

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
                mSpList.remove(0);
                mView.updateItemRemoved(0);
            }
        }
    };

    @Inject
    SPListPresenter() {
        mTimeoutHandler = new Handler();
        mSpList = new LinkedList<>();
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
        mTimeoutHandler.removeCallbacks(mTimeoutTask);
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
        Log.d(TAG, "onNext item received : " + sensorPuckModel.getAddress());
        mTimeoutHandler.removeCallbacks(mTimeoutTask);
        if (mSpList.size() == 0) {
            mSpList.add(sensorPuckModel);
            mView.updateDeviceList(mSpList);
        } else {
            Iterator<SensorPuckModel> it = mSpList.iterator();
            int i = 0;
            int positionToReplace = -1;
            while (it.hasNext()) {
                long currentTime = System.currentTimeMillis();
                SensorPuckModel cur = it.next();
                if (cur.equals(sensorPuckModel)) {
                    positionToReplace = i;
                }
                if (currentTime - cur.getTimestamp() > SP_DISCOVERY_TIMEOUT) {
                    mView.updateItemRemoved(i);
                    it.remove();
                } else {
                    ++i;
                }
            }
            if (positionToReplace > -1) {
                mSpList.remove(positionToReplace);
                mSpList.add(positionToReplace, sensorPuckModel);
                mView.updateItemChanged(positionToReplace);
            } else if (!mSpList.contains(sensorPuckModel)) {
                mSpList.add(sensorPuckModel);
                mView.updateItemInserted(mSpList.size() - 1);
            }
        }
        mTimeoutHandler.postDelayed(mTimeoutTask, SP_DISCOVERY_TIMEOUT);
    }
}
