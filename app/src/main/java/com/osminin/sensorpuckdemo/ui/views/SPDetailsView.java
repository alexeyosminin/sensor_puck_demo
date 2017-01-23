package com.osminin.sensorpuckdemo.ui.views;

import com.osminin.sensorpuckdemo.model.SensorPuckModel;
import com.osminin.sensorpuckdemo.ui.base.BaseView;

/**
 * Created by osminin on 09.11.2016.
 */

public interface SPDetailsView extends BaseView {

    void update(SensorPuckModel model);

    void showError();
}
