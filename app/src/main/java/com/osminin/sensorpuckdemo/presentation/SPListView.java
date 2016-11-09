package com.osminin.sensorpuckdemo.presentation;

import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import java.util.List;

/**
 * Created by osminin on 08.11.2016.
 */

public interface SPListView {

    void updateDeviceList(List<SensorPuckModel> list);

    void showDetailsFragment(SensorPuckModel model);
}
