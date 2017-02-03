package com.osminin.sensorpuckdemo.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;

import com.osminin.sensorpuckdemo.R;
import com.osminin.sensorpuckdemo.error.SPError;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by osminin on 08.11.2016.
 */

public abstract class BaseFragment extends Fragment implements BaseView {

    protected View mRootView;
    protected Context mContext;
    protected Unbinder mUnbinder;

    private Snackbar mSnackbar;

    private CoordinatorLayout mCoordinatorLayout;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();
        mCoordinatorLayout = ButterKnife.findById(mRootView.getRootView(), R.id.content_main);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @Override
    public void showError(SPError error) {
        if (mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
        }
        int resId = 0;
        switch (error) {
            case CONNECTION_LOST:
                resId = R.string.error_connection_lost;
                break;
            case BLE_NOT_AVAILABLE:
                resId = R.string.error_ble_not_available;
                break;
            case LOCATION_NOT_ENABLED:
                resId = R.string.error_location_not_enabled;
                break;
            case BLE_NOT_ENABLED:
                resId = R.string.error_ble_not_enabled;
                break;
            case COMMON_ERROR:
                resId = R.string.error_common;
                break;
        }
        String errorMessage = getString(resId);
        mSnackbar = Snackbar.make(mCoordinatorLayout, errorMessage, Snackbar.LENGTH_LONG);
        mSnackbar.show();
    }

    protected boolean isErrorShown() {
        return mSnackbar != null && mSnackbar.isShown();
    }

    public abstract String getTitle();

    public String getFragmentTag() {
        return this.getClass().getName();
    }
}
