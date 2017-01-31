package com.osminin.sensorpuckdemo.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.osminin.sensorpuckdemo.App;
import com.osminin.sensorpuckdemo.R;
import com.osminin.sensorpuckdemo.presentation.interfaces.SettingsPresenter;
import com.osminin.sensorpuckdemo.ui.custom.CustomPreferenceFragment;
import com.osminin.sensorpuckdemo.ui.views.SPListView;
import com.osminin.sensorpuckdemo.ui.views.SettingsView;

import java.util.Arrays;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;
import static com.osminin.sensorpuckdemo.Constants.SETTINGS_REQUEST_CODE;


/**
 * Created by osminin on 30.11.2016.
 */

public class SettingsFragment extends CustomPreferenceFragment implements SettingsView,  SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = SettingsFragment.class.getName();

    @Inject
    SettingsPresenter mPresenter;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        App.getAppComponent(getActivity()).inject(this);
        mPresenter.setView(this);

        String currentMode = mPresenter.getCurrentMode();
        ListPreference modePref = (ListPreference) findPreference(getString(R.string.settings_mode_key));
        modePref.setSummary(currentMode);
        modePref.setValueIndex(mPresenter.getCurrentModeIndex());

        mPresenter.fillInitialValues();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference preference = findPreference(s);
        String value = sharedPreferences.getString(s, null);
        if (!TextUtils.isEmpty(value)) {
            preference.setSummary(value);
        }
    }

    @Override
    public void setInitialValues(int valuesId, String key, int defaultIndex) {
        String[] valArray = getResources().getStringArray(valuesId);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        String currentValue = sp.getString(key, valArray[defaultIndex]);
        ListPreference listPreference = (ListPreference) findPreference(key);
        listPreference.setSummary(currentValue);
        int currentIndex = Arrays.asList(valArray).indexOf(currentValue);
        listPreference.setValueIndex(valArray[defaultIndex].equals(currentValue) ? defaultIndex : currentIndex);
    }

    @Override
    public String getTitle() {
        return getString(R.string.settings_title);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        SPListView listView = (SPListView) getTargetFragment();
        int requestCode = getTargetRequestCode();
        if (requestCode == SETTINGS_REQUEST_CODE) {
            listView.onSettingsChanged(RESULT_OK);
        }
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onDestroy");
    }
}
