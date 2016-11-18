package com.osminin.sensorpuckdemo.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.Unbinder;

/**
 * Created by osminin on 08.11.2016.
 */

public class BaseFragment extends Fragment {

    protected View mRootView;
    protected Context mContext;
    protected Unbinder mUnbinder;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    public String getFragmentTag() {
        return this.getClass().getName().toString();
    }
}
