package com.osminin.sensorpuckdemo.presentation.interfaces;

import com.osminin.sensorpuckdemo.model.SensorPuckModel;
import com.osminin.sensorpuckdemo.ui.views.SPDetailsView;

/**
 * Created by osminin on 1/23/2017.
 */

public interface SPDetailsPresenter extends BasePresenter<SPDetailsView> {
    void setModel(SensorPuckModel model);

    void startReceivingUpdates();

    void stopReceivingUpdates();
}
