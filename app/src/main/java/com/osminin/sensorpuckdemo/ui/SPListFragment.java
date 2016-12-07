package com.osminin.sensorpuckdemo.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.osminin.sensorpuckdemo.App;
import com.osminin.sensorpuckdemo.R;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;
import com.osminin.sensorpuckdemo.presentation.SPListPresenter;
import com.osminin.sensorpuckdemo.presentation.SPListView;
import com.osminin.sensorpuckdemo.ui.base.BaseFragment;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

import static com.osminin.sensorpuckdemo.Constants.SP_MODEL_EXTRA;

/**
 * Created by osminin on 08.11.2016.
 */

public final class SPListFragment extends BaseFragment implements SPListView, Observer<SensorPuckModel> {

    @Inject
    SPListPresenter mPresenter;

    @BindView(R.id.sp_list)
    RecyclerView mRecyclerView;

    private List<SensorPuckModel> mDevices;
    private SPAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent(getActivity()).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_sp_list, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        initList();
        mPresenter.setView(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.setView(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.startScan();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.stopScan();
    }

    @Override
    public void updateDeviceList(List<SensorPuckModel> list) {
        mDevices = list;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.app_name);
    }

    @Override
    public void showDetailsFragment(SensorPuckModel model) {
        BaseFragment fragment = new SPDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(SP_MODEL_EXTRA, model);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = ((AppCompatActivity) mContext).
                getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.content_main, fragment);
        transaction.addToBackStack(fragment.getFragmentTag());
        transaction.commit();
    }

    @Override
    public void updateItemInserted(int position) {
        mAdapter.notifyItemInserted(position);
    }

    @Override
    public void updateItemRemoved(int position) {
        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public void updateItemChanged(int position) {
        mAdapter.notifyItemChanged(position);
    }

    private void initList() {
        mAdapter = new SPAdapter();
        RecyclerView.LayoutManager layoutManager;
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(getContext(), 2);
        } else {
            layoutManager = new LinearLayoutManager(getContext());
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter.getPositionClicks()
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(SensorPuckModel sensorPuckModel) {
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnClickSubject.onNext(model);
                }
            });
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
