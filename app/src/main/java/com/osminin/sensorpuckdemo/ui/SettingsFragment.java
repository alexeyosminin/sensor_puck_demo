package com.osminin.sensorpuckdemo.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.TextUtils;

import com.osminin.sensorpuckdemo.R;

import java.util.Arrays;


/**
 * Created by osminin on 30.11.2016.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int DEFAULT_MODE_INDEX = 1;
    private static final int DEFAULT_FAKE_COUNT_INDEX = 1;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

        String[] modesArray = getResources().getStringArray(R.array.mode_types);
        String currentMode = sp.getString(getString(R.string.settings_mode_key), modesArray[DEFAULT_MODE_INDEX]);
        ListPreference modePref = (ListPreference) findPreference(getString(R.string.settings_mode_key));
        modePref.setSummary(currentMode);
        modePref.setValueIndex(modesArray[DEFAULT_MODE_INDEX].equals(currentMode) ? DEFAULT_MODE_INDEX : 0);

        setInitialValues(R.array.fake_sp_count, getString(R.string.settings_fake_count_key), DEFAULT_FAKE_COUNT_INDEX);
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

    private void setInitialValues(int valuesId, String key, int defaultIndex) {
        String[] valArray = getResources().getStringArray(valuesId);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        String currentValue = sp.getString(key, valArray[defaultIndex]);
        ListPreference listPreference = (ListPreference) findPreference(key);
        listPreference.setSummary(currentValue);
        int currentIndex = Arrays.asList(valArray).indexOf(currentValue);
        listPreference.setValueIndex(valArray[defaultIndex].equals(currentValue) ? defaultIndex : currentIndex);
    }
}
