package com.osminin.sensorpuckdemo.presentation.interfaces;

import com.osminin.sensorpuckdemo.ui.views.SettingsView;

/**
 * Created by osminin on 1/30/2017.
 */

public interface SettingsPresenter extends BasePresenter <SettingsView> {
    int DEFAULT_MODE_INDEX = 1;
    int DEFAULT_FAKE_COUNT_INDEX = 1;

    String[] getModesArray();
    String getCurrentMode();
    void fillInitialValues();
    int getCurrentModeIndex();
}
