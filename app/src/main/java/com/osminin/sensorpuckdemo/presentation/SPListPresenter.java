package com.osminin.sensorpuckdemo.presentation;

import android.os.Handler;

import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by osminin on 08.11.2016.
 */

public class SPListPresenter implements Presenter<SPListView> {

    private SPListView mView;

    @Inject
    SPListPresenter() {

    }

    @Override
    public void setView(SPListView view) {
        mView = view;
    }

    public void startScan() {
        //TODO: start ble device scanning
        //fake data:
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<SensorPuckModel> list = new ArrayList<>();
                for (int i = 0; i < 10; ++i) {
                    list.add(new SensorPuckModel());
                }
                mView.updateDeviceList(list);
            }
        }, 3000);
    }

    public void onDeviceSelected(SensorPuckModel model) {
        mView.showDetailsFragment(model);
    }
}
