package com.osminin.sensorpuckdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.osminin.sensorpuckdemo.ble.SPScannerInterface;
import com.osminin.sensorpuckdemo.presentation.SPDetailsPresenterImpl;
import com.osminin.sensorpuckdemo.presentation.SPListPresenterImpl;
import com.osminin.sensorpuckdemo.presentation.SettingsPresenterImpl;
import com.osminin.sensorpuckdemo.presentation.interfaces.SPDetailsPresenter;
import com.osminin.sensorpuckdemo.presentation.interfaces.SPListPresenter;
import com.osminin.sensorpuckdemo.presentation.interfaces.SettingsPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    Context provideContext() {
        return app;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Provides
    SPListPresenter provideSPListPresenter(SPScannerInterface scanner) {
        return new SPListPresenterImpl(scanner);
    }

    @Provides
    SPDetailsPresenter provideSPDetailsPresenter(SPScannerInterface scanner) {
        return new SPDetailsPresenterImpl(scanner);
    }

    @Provides
    SettingsPresenter provideSettingsPresenter(SharedPreferences sharedPreferences) {
        return new SettingsPresenterImpl(sharedPreferences);
    }
}
