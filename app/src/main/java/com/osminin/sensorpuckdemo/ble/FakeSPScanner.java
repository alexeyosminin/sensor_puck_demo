package com.osminin.sensorpuckdemo.ble;

import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.osminin.sensorpuckdemo.Constants.SP_DISCOVERY_TIMEOUT;

/**
 * Created by osminin on 29.11.2016.
 */

public final class FakeSPScanner implements SPScannerInterface {
    private Observable<Long> mIntervalProducer;
    private int mSPCount;
    private List<String> mMacAddress;
    private Func1<Long, SensorPuckModel> mDeviceMapper = new Func1<Long, SensorPuckModel>() {
        @Override
        public SensorPuckModel call(Long aLong) {
            return SensorPuckParser.generateRandomModel((int) (aLong % mSPCount), mMacAddress);
        }
    };

    @Inject
    @Singleton
    public FakeSPScanner() {
        mSPCount = 10; // default
        //all devices should be updated during SP_DISCOVERY_TIMEOUT
        mIntervalProducer = Observable.interval(SP_DISCOVERY_TIMEOUT / (mSPCount + 1), TimeUnit.MILLISECONDS);
        mMacAddress = new ArrayList<>(mSPCount);
        for (int i = 0; i < mSPCount; ++i) {
            mMacAddress.add(randomMACAddress(i));
        }
    }

    @Override
    public Subscription subscribe(Observer<? super SensorPuckModel> observer) {
        return mIntervalProducer
                .onBackpressureBuffer(1000)
                .subscribeOn(Schedulers.immediate())
                .map(mDeviceMapper)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public Subscription subscribe(Observer<? super SensorPuckModel> observer,
                                  Func1<? super SensorPuckModel, Boolean> predicate) {
        return mIntervalProducer
                .onBackpressureBuffer(1000)
                .subscribeOn(Schedulers.immediate())
                .map(mDeviceMapper)
                .filter(predicate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private String randomMACAddress(int i) {
        Random rand = new Random(System.currentTimeMillis() / (i + 1));
        byte[] macAddr = new byte[6];
        rand.nextBytes(macAddr);

        macAddr[0] = (byte) (macAddr[0] & (byte) 254);

        StringBuilder sb = new StringBuilder(18);
        for (byte b : macAddr) {

            if (sb.length() > 0)
                sb.append(":");

            sb.append(String.format("%02x", b));
        }


        return sb.toString().toUpperCase();
    }
}
