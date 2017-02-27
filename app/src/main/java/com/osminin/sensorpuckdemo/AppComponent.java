package com.osminin.sensorpuckdemo;

import com.osminin.sensorpuckdemo.ui.fragments.SPDetailsFragment;
import com.osminin.sensorpuckdemo.ui.fragments.SPListFragment;
import com.osminin.sensorpuckdemo.ui.fragments.SettingsFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by osminin on 08.11.2016.
 */

@Component(
        modules = AppModule.class
)
@Singleton
public interface AppComponent {
    void inject(SPListFragment fragment);

    void inject(SPDetailsFragment fragment);

    void inject(SettingsFragment fragment);
}
