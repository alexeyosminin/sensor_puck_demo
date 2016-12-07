package com.osminin.sensorpuckdemo;

import com.osminin.sensorpuckdemo.ui.MainActivity;
import com.osminin.sensorpuckdemo.ui.fragments.SPDetailsFragment;
import com.osminin.sensorpuckdemo.ui.fragments.SPListFragment;

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
    MainActivity inject(MainActivity activity);

    SPListFragment inject(SPListFragment fragment);

    SPDetailsFragment inject(SPDetailsFragment fragment);
}
