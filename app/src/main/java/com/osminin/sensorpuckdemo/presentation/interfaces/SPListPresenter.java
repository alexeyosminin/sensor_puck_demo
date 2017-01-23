package com.osminin.sensorpuckdemo.presentation.interfaces;

import com.osminin.sensorpuckdemo.model.SensorPuckModel;
import com.osminin.sensorpuckdemo.ui.views.SPListView;

/**
 * TODO: Add a class header comment!
 */

public interface SPListPresenter extends BasePresenter<SPListView> {
    void startScan();
    void stopScan();
    void onDeviceSelected(SensorPuckModel model);
    void destroy();
    void onScannerFunctionalityEnabled();
}
