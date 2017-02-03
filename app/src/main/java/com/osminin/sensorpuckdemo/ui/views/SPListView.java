package com.osminin.sensorpuckdemo.ui.views;

import com.osminin.sensorpuckdemo.model.SensorPuckModel;
import com.osminin.sensorpuckdemo.ui.base.BaseView;

/**
 * Created by osminin on 08.11.2016.
 */

public interface SPListView extends BaseView {

    void showDetailsFragment(SensorPuckModel model);

    void updateItemInserted(int position, SensorPuckModel model);

    void updateItemRemoved(int position);

    void updateItemChanged(int position, SensorPuckModel model);

    void updateAllItemsRemoved();

    void showEnableBluetoothDialog();

    void showSettingsFragment();

    void onSettingsChanged(int resultCode);

    void restartWithNewConfig();

    void showAboutScreen();

    void showLocationPermissionDialog();

    void showEnableLocationDialog();
}
