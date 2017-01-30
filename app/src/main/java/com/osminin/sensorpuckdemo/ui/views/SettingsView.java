package com.osminin.sensorpuckdemo.ui.views;

import com.osminin.sensorpuckdemo.ui.base.BaseView;

/**
 * Created by osminin on 1/30/2017.
 */

public interface SettingsView extends BaseView {
    void setInitialValues(int valuesId, String key, int defaultIndex);
}
