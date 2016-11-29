package com.osminin.sensorpuckdemo.presentation;

import com.osminin.sensorpuckdemo.model.SensorPuckModel;

/**
 * Created by osminin on 09.11.2016.
 */

public interface SPDetailsView extends BaseView{

    void update(SensorPuckModel model);

    void showError();
}
