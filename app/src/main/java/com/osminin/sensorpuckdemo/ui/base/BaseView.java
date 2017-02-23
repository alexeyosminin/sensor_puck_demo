package com.osminin.sensorpuckdemo.ui.base;

import android.content.Context;

import com.osminin.sensorpuckdemo.error.SPError;

/**
 * Created by osminin on 29.11.2016.
 */

public interface BaseView {
    Context getContext();

    void showError(SPError error);
}
