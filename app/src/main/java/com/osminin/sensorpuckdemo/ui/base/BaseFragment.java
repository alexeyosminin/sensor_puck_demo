package com.osminin.sensorpuckdemo.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;

import com.osminin.sensorpuckdemo.R;
import com.osminin.sensorpuckdemo.presentation.interfaces.BaseView;

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
    public void showError() {
        if (mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
        }
        mSnackbar = Snackbar.make(mCoordinatorLayout, "Connection lost", Snackbar.LENGTH_LONG);
        mSnackbar.show();
    }

    public abstract String getTitle();

    public String getFragmentTag() {
        return this.getClass().getName().toString();
    }
}
