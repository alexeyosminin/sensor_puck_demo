package com.osminin.sensorpuckdemo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osminin.sensorpuckdemo.R;
import com.osminin.sensorpuckdemo.ui.custom.CustomPreferenceFragment;

/**
 * Created by osminin on 30.11.2016.
 */

public class SettingsFragment extends CustomPreferenceFragment {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

        return view;
    }

    @Override
    public String getTitle() {
        return getString(R.string.nav_menu_settings);
    }
}
