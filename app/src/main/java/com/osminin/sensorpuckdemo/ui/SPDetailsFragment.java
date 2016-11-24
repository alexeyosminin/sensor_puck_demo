package com.osminin.sensorpuckdemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
    /* Heart Rate Monitor state */
    private static final int HRM_STATE_IDLE      = 0;
    private static final int HRM_STATE_NOSIGNAL  = 1;
    private static final int HRM_STATE_ACQUIRING = 2;
    private static final int HRM_STATE_ACTIVE    = 3;
    private static final int HRM_STATE_INVALID   = 4;
    private static final int HRM_STATE_ERROR     = 5;

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
    @BindView(R.id.sp_details_hr)
    TextView mHeartRate;
    @BindView(R.id.sp_details_battery)
    TextView mBattery;

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
        mBattery.setText(Float.toString(model.getBattery()));
        String hrmText = null;
        switch (model.getHRMState()) {
            case HRM_STATE_IDLE:
                hrmText = mContext.getString(R.string.hrm_state_idle);
                break;
            case HRM_STATE_NOSIGNAL:
                hrmText = mContext.getString(R.string.hrm_state_no_signal);
                break;
            case HRM_STATE_ACQUIRING:
                hrmText = mContext.getString(R.string.hrm_state_acquiring);
                break;
            case HRM_STATE_ACTIVE:
                hrmText = Integer.toString(model.getHRMRate()) + " bpm";
                break;
            case HRM_STATE_INVALID:
                hrmText = mContext.getString(R.string.hrm_state_reposition);
                break;
            case HRM_STATE_ERROR:
                hrmText = mContext.getString(R.string.hrm_state_error);
                break;
        }
        mHeartRate.setText(hrmText);
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

    @Override
    public void showError() {
        //TODO:
    }

    @Override
    public String getTitle() {
        return mModel.getName();
    }
}
