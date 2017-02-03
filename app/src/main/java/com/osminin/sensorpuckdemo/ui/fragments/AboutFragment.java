package com.osminin.sensorpuckdemo.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osminin.sensorpuckdemo.R;
import com.osminin.sensorpuckdemo.ui.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by osminin on 1/31/2017.
 */

public final class AboutFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_about, container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        return mRootView;
    }

    @OnClick(R.id.about_github_link)
    void onGithubLinkClicked() {
        String url = getString(R.string.github_link);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public String getTitle() {
        return getString(R.string.about_title);
    }
}
