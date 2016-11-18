package com.osminin.sensorpuckdemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.osminin.sensorpuckdemo.App;
import com.osminin.sensorpuckdemo.R;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;
import com.osminin.sensorpuckdemo.presentation.SPDetailsPresenter;
import com.osminin.sensorpuckdemo.presentation.SPDetailsView;
import com.osminin.sensorpuckdemo.ui.base.BaseFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.osminin.sensorpuckdemo.Constants.SP_MODEL_EXTRA;

/**
 * Created by osminin on 09.11.2016.
 */

public final class SPDetailsFragment extends BaseFragment implements SPDetailsView {
    private static final String TAG = SPDetailsFragment.class.getSimpleName();

    @Inject
    SPDetailsPresenter mPresenter;

    @BindView(R.id.sp_details_temperature)
    TextView mTemperature;
    @BindView(R.id.sp_details_humidity)
    TextView mHumidity;
    @BindView(R.id.sp_details_uv)
    TextView mUv;
    @BindView(R.id.sp_details_light)
    TextView mLight;

    private SensorPuckModel mModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent(getActivity()).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_sp_details, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        mModel = getArguments().getParcelable(SP_MODEL_EXTRA);
        mPresenter.setModel(mModel);
        mPresenter.setView(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.setView(null);
    }

    @Override
    public void update(SensorPuckModel model) {
        mTemperature.setText(Float.toString(model.getTemperature()));
        mLight.setText(Integer.toString(model.getAmbientLight()));
        mUv.setText(Integer.toString(model.getUVIndex()));
        mHumidity.setText(Float.toString(model.getHumidity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.startReceivingUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.stopReceivingUpdates();
    }
}
