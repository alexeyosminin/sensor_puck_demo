package com.osminin.sensorpuckdemo.presentation;

import android.content.SharedPreferences;

import com.osminin.sensorpuckdemo.R;
import com.osminin.sensorpuckdemo.presentation.interfaces.SettingsPresenter;
import com.osminin.sensorpuckdemo.ui.views.SettingsView;

import javax.inject.Inject;

/**
 * Created by osminin on 1/30/2017.
 */

public class SettingsPresenterImpl implements SettingsPresenter {

    private final SharedPreferences mSharedPreferences;
    private SettingsView mView;

    @Inject
    public SettingsPresenterImpl(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    @Override
    public void setView(SettingsView view) {
        mView = view;
    }

    @Override
    public String[] getModesArray() {
        return mView.getContext().getResources().getStringArray(R.array.mode_types);
    }

    @Override
    public String getCurrentMode() {
        String settingsModeKey = mView.getContext().getResources().getString(R.string.settings_mode_key);
        return mSharedPreferences.getString(settingsModeKey, getModesArray()[DEFAULT_MODE_INDEX]);
    }

    @Override
    public void fillInitialValues() {
        String settingsFakeCountKey = mView.getContext().getResources().getString(R.string.settings_fake_count_key);
        mView.setInitialValues(R.array.fake_sp_count, settingsFakeCountKey, DEFAULT_FAKE_COUNT_INDEX);
    }

    @Override
    public int getCurrentModeIndex() {
        String[] modesArray = getModesArray();
        String currentMode = getCurrentMode();
        return modesArray[DEFAULT_MODE_INDEX].equals(currentMode) ? DEFAULT_MODE_INDEX : 0;
    }
}
