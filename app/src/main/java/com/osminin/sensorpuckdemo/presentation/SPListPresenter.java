package com.osminin.sensorpuckdemo.presentation;

import javax.inject.Inject;

/**
 * Created by osminin on 08.11.2016.
 */

public class SPListPresenter implements Presenter<SPListView> {

    private SPListView mView;

    @Inject
    SPListPresenter() {
        
    }

    @Override
    public void setView(SPListView view) {
        mView = view;
    }
}
