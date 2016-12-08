package com.osminin.sensorpuckdemo.ble;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
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
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static com.osminin.sensorpuckdemo.Constants.SP_DISCOVERY_TIMEOUT;

/**
 * Created by osminin on 29.11.2016.
 */

public final class FakeSPScanner implements SPScannerInterface {
    private static final String TAG = FakeSPScanner.class.getSimpleName();
    private static final long BACKPRESSURE_BUFFER_CAPACITY = 1000;
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
    public FakeSPScanner(int fakeSPCount) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "FakeSPScanner()");
        mSPCount = fakeSPCount;
        //all devices should be updated during SP_DISCOVERY_TIMEOUT
        mIntervalProducer = Observable.interval(SP_DISCOVERY_TIMEOUT / (mSPCount + 1), TimeUnit.MILLISECONDS);
        mMacAddress = new ArrayList<>(mSPCount);
        for (int i = 0; i < mSPCount; ++i) {
            mMacAddress.add(randomMACAddress(i));
        }
    }

    @Override
    public Subscription subscribe(Observer<? super SensorPuckModel> observer) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "subscribe: " + observer.getClass().getName());
        return mIntervalProducer
                .onBackpressureBuffer(BACKPRESSURE_BUFFER_CAPACITY)
                .subscribeOn(Schedulers.immediate())
                .map(mDeviceMapper)
                .observeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public Subscription subscribe(Observer<? super SensorPuckModel> observer,
                                  Func1<? super SensorPuckModel, Boolean> predicate) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "subscribe: " + observer.getClass().getName());
        return mIntervalProducer
                .onBackpressureBuffer(BACKPRESSURE_BUFFER_CAPACITY)
                .subscribeOn(Schedulers.immediate())
                .map(mDeviceMapper)
                .filter(predicate)
                .observeOn(Schedulers.computation())
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

        String result = sb.toString().toUpperCase();
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "randomMACAddress: " + result);
        return result;
    }
}
