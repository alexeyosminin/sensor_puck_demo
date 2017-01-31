package com.osminin.sensorpuckdemo.ui.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.osminin.sensorpuckdemo.App;
import com.osminin.sensorpuckdemo.R;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;
import com.osminin.sensorpuckdemo.presentation.interfaces.SPListPresenter;
import com.osminin.sensorpuckdemo.ui.MainActivity;
import com.osminin.sensorpuckdemo.ui.views.SPListView;
import com.osminin.sensorpuckdemo.ui.base.BaseFragment;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

import static android.app.Activity.RESULT_OK;
import static com.osminin.sensorpuckdemo.Constants.REQUEST_ENABLE_BT;
import static com.osminin.sensorpuckdemo.Constants.SETTINGS_REQUEST_CODE;
import static com.osminin.sensorpuckdemo.Constants.SP_MODEL_EXTRA;

/**
 * Created by osminin on 08.11.2016.
 */

public final class SPListFragment extends BaseFragment implements SPListView, Observer<SensorPuckModel> {
    private static final String TAG = SPListFragment.class.getSimpleName();
    private static final int CLICK_TIMEOUT = 500;

    @Inject
    SPListPresenter mPresenter;

    @BindView(R.id.sp_list)
    RecyclerView mRecyclerView;

    private List<SensorPuckModel> mDevices;
    private SPAdapter mAdapter;
    private Subscription mAdapterSubscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onCreate");
        super.onCreate(savedInstanceState);
        App.getAppComponent(getActivity()).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onCreateView");
        mRootView = inflater.inflate(R.layout.fragment_sp_list, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        initList();
        mPresenter.setView(this);
    }

    @Override
    public void onDestroyView() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onDestroyView");
        super.onDestroyView();
        mPresenter.setView(null);
    }

    @Override
    public void onStart() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onStart");
        super.onStart();
        mPresenter.startScan();
    }

    @Override
    public void onStop() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onStop");
        super.onStop();
        mPresenter.stopScan();
        mDevices.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onDestroy");
        super.onDestroy();
        mPresenter.destroy();
        mAdapterSubscription.unsubscribe();
    }

    @Override
    public String getTitle() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "getTitle");
        return mContext.getString(R.string.app_name);
    }

    @Override
    public void showDetailsFragment(SensorPuckModel model) {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "showDetailsFragment");
        BaseFragment fragment = new SPDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(SP_MODEL_EXTRA, model);
        fragment.setArguments(bundle);
        showFragment(fragment);
    }

    @Override
    public void updateItemInserted(int position, SensorPuckModel model) {
        mDevices.add(position, model);
        mAdapter.notifyItemInserted(position);
    }

    @Override
    public void updateItemRemoved(int position) {
        mDevices.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public void updateItemChanged(int position, SensorPuckModel model) {
        mDevices.remove(position);
        mDevices.add(position, model);
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void updateAllItemsRemoved() {
        int count = mDevices.size();
        mDevices.clear();
        mAdapter.notifyItemRangeRemoved(0, count);
    }

    @Override
    public void showEnableBluetoothDialog() {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "showEnableBluetoothDialog");
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    @Override
    public void showSettingsFragment() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "showSettingsFragment");
        BaseFragment settingsFragment = new SettingsFragment();
        settingsFragment.setTargetFragment(this, SETTINGS_REQUEST_CODE);
        showFragment(settingsFragment);
    }

    @Override
    public void onSettingsChanged(int resultCode) {
        if (resultCode == RESULT_OK) {
            mPresenter.onSettingsChanged();
        }
    }

    @Override
    public void restartWithNewConfig() {
        ((MainActivity) mContext).restartConfiguration();
    }

    @Override
    public void showAboutScreen() {
        BaseFragment about = new AboutFragment();
        showFragment(about);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "showEnableBluetoothDialog");
        if (requestCode == REQUEST_ENABLE_BT) {
            mPresenter.onScannerFunctionalityEnabled(resultCode == RESULT_OK);
        }
    }

    private void initList() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onViewCreated");
        mDevices = new LinkedList<>();
        mAdapter = new SPAdapter();
        RecyclerView.LayoutManager layoutManager;
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(getContext(), 2);
        } else {
            layoutManager = new LinearLayoutManager(getContext());
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapterSubscription = mAdapter.getPositionClicks()
                .throttleFirst(CLICK_TIMEOUT, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showFragment(BaseFragment fragment) {
        FragmentManager fragmentManager = ((AppCompatActivity) mContext).
                getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.content_main, fragment);
        transaction.addToBackStack(fragment.getFragmentTag());
        transaction.commit();
    }

    @Override
    public void onCompleted() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onViewCreated");
    }

    @Override
    public void onError(Throwable e) {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onViewCreated");
    }

    @Override
    public void onNext(SensorPuckModel sensorPuckModel) {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onViewCreated");
        mPresenter.onDeviceSelected(sensorPuckModel);
    }

    class SPAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final PublishSubject<SensorPuckModel> mOnClickSubject = PublishSubject.create();

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sp_card_layout, parent,
                    false);
            return new SPViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final SensorPuckModel model = mDevices.get(position);
            ((SPViewHolder) holder).mCardName.setText(model.getName());
            ((SPViewHolder) holder).mSignal.setText(model.getRssi() + " dBm");
            ((SPViewHolder) holder).mAddress.setText(model.getAddress());
            int rssiImageId = 0;
            switch (model.getSignalStrength()) {
                case VERY_LOW:
                    rssiImageId = R.drawable.ic_signal_cellular_0_bar_black_24dp;
                    break;
                case LOW:
                    rssiImageId = R.drawable.ic_signal_cellular_1_bar_black_24dp;
                    break;
                case MEDIUM:
                    rssiImageId = R.drawable.ic_signal_cellular_2_bar_black_24dp;
                    break;
                case HIGH:
                    rssiImageId = R.drawable.ic_signal_cellular_3_bar_black_24dp;
                    break;
                case VERY_HIGH:
                    rssiImageId = R.drawable.ic_signal_cellular_4_bar_black_24dp;
                    break;
            }
            ((SPViewHolder) holder).mSignalImage.setImageResource(rssiImageId);
            holder.itemView.setOnClickListener(view -> mOnClickSubject.onNext(model));
        }

        @Override
        public long getItemId(int position) {
            return mDevices.get(position).hashCode();
        }

        @Override
        public int getItemCount() {
            return mDevices == null ? 0 : mDevices.size();
        }

        public Observable<SensorPuckModel> getPositionClicks() {
            return mOnClickSubject.asObservable();
        }

        class SPViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.sp_card_name)
            TextView mCardName;
            @BindView(R.id.sp_card_signal)
            TextView mSignal;
            @BindView(R.id.sp_card_address)
            TextView mAddress;
            @BindView(R.id.sp_card_rssi)
            ImageView mSignalImage;

            public SPViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}