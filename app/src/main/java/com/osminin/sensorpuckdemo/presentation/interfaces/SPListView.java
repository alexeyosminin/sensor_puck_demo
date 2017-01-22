package com.osminin.sensorpuckdemo.presentation.interfaces;

import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import java.util.List;

/**
 * Created by osminin on 08.11.2016.
 */

public interface SPListView extends BaseView {

    void showDetailsFragment(SensorPuckModel model);

    void updateItemInserted(int position, SensorPuckModel model);

    void updateItemRemoved(int position);

    void updateItemChanged(int position, SensorPuckModel model);

    void showError();
}
