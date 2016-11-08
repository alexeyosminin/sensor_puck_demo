package com.osminin.sensorpuckdemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osminin.sensorpuckdemo.R;
import com.osminin.sensorpuckdemo.ui.base.BaseFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;
import com.osminin.sensorpuckdemo.presentation.SPListPresenter;
import com.osminin.sensorpuckdemo.presentation.SPListView;

/**
 * Created by osminin on 08.11.2016.
 */

public final class SPListFragment extends BaseFragment implements SPListView {

    @Inject
    SPListPresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_sp_list, container, false);
        return mRootView;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mPresenter.setView(this);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mPresenter.setView(null);
    }
}
