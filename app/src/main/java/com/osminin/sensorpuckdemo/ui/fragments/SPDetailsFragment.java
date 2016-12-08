package com.osminin.sensorpuckdemo.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
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
    private static final int GRAPH_RANGE_SIZE = 40000;
    private static final int GRAPH_DOMAIN_SIZE = 50;
    /* Heart Rate Monitor state */
    private static final int HRM_STATE_IDLE = 0;
    private static final int HRM_STATE_NOSIGNAL = 1;
    private static final int HRM_STATE_ACQUIRING = 2;
    private static final int HRM_STATE_ACTIVE = 3;
    private static final int HRM_STATE_INVALID = 4;
    private static final int HRM_STATE_ERROR = 5;
    private static final int BPF_ORDER = 4;
    private static final int BPF_FILTER_LEN = BPF_ORDER * 2 + 1;
    @Inject
    SPDetailsPresenter mPresenter;
    @BindView(R.id.details_temperature)
    TextView mTemperature;
    @BindView(R.id.details_humidity)
    TextView mHumidity;
    @BindView(R.id.details_uv)
    TextView mUv;
    @BindView(R.id.details_light)
    TextView mLight;
    @BindView(R.id.details_heart_rate)
    TextView mHeartRate;
    @BindView(R.id.details_battery)
    TextView mBattery;
    @BindView(R.id.details_battery_image)
    ImageView mBatteryImage;

    //@BindView(R.id.plot)
    XYPlot mPlot;
    private SensorPuckModel mModel;
    private Snackbar mSnackbar;
    private SimpleXYSeries mLine;
    private int PrevDelta = 0;
    private int MaxDelta = 0;
    private int Gain = 1;
    private short[] BPF_In = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private double[] BPF_Out = {0, 0, 0, 0, 0, 0, 0, 0, 0};

    private double[] BPF_a =
            { //Fs=25; [BPF_b,BPF_a] = butter(4,[45 200]/60/Fs*2);
                    1.000000000000000e+000,
                    -5.805700439644110e+000,
                    1.514036628292202e+001,
                    -2.323300817159229e+001,
                    2.298582338785502e+001,
                    -1.502165263561143e+001,
                    6.331004788861760e+000,
                    -1.573336063098673e+000,
                    1.767891944741809e-001
            };

    private double[] BPF_b =
            {
                    5.392924554970057e-003,
                    0,
                    -2.157169821988023e-002,
                    0,
                    3.235754732982035e-002,
                    0,
                    -2.157169821988023e-002,
                    0,
                    5.392924554970057e-003
            };


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
        //initializePlot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.setView(null);
    }

    @Override
    public void update(SensorPuckModel model) {
        mModel = model;
        mTemperature.setText(Float.toString(model.getTemperature()));
        mLight.setText(Integer.toString(model.getAmbientLight()));
        mUv.setText(Integer.toString(model.getUVIndex()));
        mHumidity.setText(Float.toString(model.getHumidity()));
        mBattery.setText(Float.toString(model.getBattery()));
        updateBatteryImage();
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
                //updateHRMPlot();
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
        if (mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
        }
        mSnackbar = Snackbar.make(mRootView, "Connection lost", Snackbar.LENGTH_LONG);
        mSnackbar.show();
    }

    @Override
    public String getTitle() {
        return mModel.getName();
    }

    private void initializePlot() {
        mLine = new SimpleXYSeries("Heart Beat");
        mLine.useImplicitXVals();

         /* Configure the graph */
        mPlot.clear();
        mPlot.setRangeBoundaries(0, GRAPH_RANGE_SIZE, BoundaryMode.FIXED);
        mPlot.setDomainBoundaries(0, GRAPH_DOMAIN_SIZE, BoundaryMode.FIXED);
        LineAndPointFormatter formatter = new LineAndPointFormatter(Color.RED, Color.RED, null, null);
        formatter.setPointLabelFormatter(new PointLabelFormatter(Color.WHITE));
        mPlot.addSeries(mLine, formatter);
        mPlot.getLegend().setVisible(false);
        mPlot.getBackgroundPaint().setColor(Color.WHITE);
        mPlot.getBorderPaint().setColor(Color.WHITE);
        mPlot.getGraph().getRangeCursorPaint().setColor(Color.WHITE);
        mPlot.setBorderStyle(Plot.BorderStyle.NONE, 0f, 0f);
        mPlot.getGraph().getGridBackgroundPaint().setColor(Color.WHITE);
    }

    private void updateHRMPlot() {
        int Delta;
        int AbsDelta;

        for (int Sample : mModel.getHRMSample()) {
            if (mLine.size() > GRAPH_DOMAIN_SIZE)
                mLine.removeFirst();

               /* Get the delta from the band pass filter */
            Delta = BPF_FilterProcess(Sample);

               /* Find the absolute value of the delta */
            if (Delta > 0)
                AbsDelta = Delta;
            else
                AbsDelta = -Delta;

               /* Find the maximum delta for the cycle */
            if (AbsDelta > MaxDelta)
                MaxDelta = AbsDelta;

               /* Adjust the gain once per cycle when crossing the x axis */
            if (PrevDelta < 0 && Delta > 0) {
                if (MaxDelta > 2000)
                    Gain = 4;               /* Burst:             >2000 */
                else if (MaxDelta > 1000)
                    Gain = 10;              /* High:       1000 to 2000 */
                else if (MaxDelta > 200)
                    Gain = 20;              /* Normal-high: 200 to 1000 */
                else if (MaxDelta > 20)
                    Gain = 100;             /* Normal-low:   20 to 200  */
                else
                    Gain = 500;             /* Low:                 <20 */
                  /* Gain = 10000 / MaxDelta; */

                MaxDelta = 0;
            }

               /* Note the previous delta */
            PrevDelta = Delta;

               /* Add the amplified delta to the end of the line */
            mLine.addLast(null, (Delta * Gain) + (GRAPH_RANGE_SIZE / 2));

        }
        mPlot.redraw();
    }

    private short BPF_FilterProcess(int raw_value) {
        //BPF: [BPF_b,BPF_a] = butter(4,[60 300]/60/Fs*2);
          /*
          The filter is a "Direct Form II Transposed" implementation of the standard difference equation:
          a(1)*y(n) = b(1)*x(n) + b(2)*x(n-1) + ... + b(nb+1)*x(n-nb)
                                - a(2)*y(n-1) - ... - a(na+1)*y(n-na)
          */

          /* Shift the BPF in/out data buffers and add the new input sample */
        for (int i = BPF_FILTER_LEN - 1; i > 0; i--) {
            BPF_In[i] = BPF_In[i - 1];
            BPF_Out[i] = BPF_Out[i - 1];
        }

          /* Add the new input sample */
        BPF_In[0] = (short) raw_value;
        BPF_Out[0] = 0;

          /* a(1)=1, y(n) = b(1)*x(n) + b(2)*x(n-1) + ... + b(nb+1)*x(n-nb) */
        for (int j = 0; j < BPF_FILTER_LEN; j++)
            BPF_Out[0] += BPF_b[j] * BPF_In[j];

          /* y =y(n)- a(2)*y(n-1) - ... - a(na+1)*y(n-na) */
        for (int j = 1; j < BPF_FILTER_LEN; j++)
            BPF_Out[0] -= BPF_a[j] * BPF_Out[j];

        return (short) (BPF_Out[0] + 0.5); //0.5=roundup
    }

    private void updateBatteryImage() {
        int resId = 0;
        switch (mModel.getBatteryLevel()) {
            case VERY_GOOD:
                resId = R.drawable.battery;
                break;
            case GOOD:
                resId = R.drawable.battery_80;
                break;
            case MEDIUM:
                resId = R.drawable.battery_60;
                break;
            case BAD:
                resId = R.drawable.battery_40;
                break;
            case VERY_BAD:
                resId = R.drawable.battery_20;
                break;
        }
        mBatteryImage.setImageResource(resId);
    }
}