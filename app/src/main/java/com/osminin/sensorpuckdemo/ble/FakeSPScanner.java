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
import rx.schedulers.Schedulers;

import static com.osminin.sensorpuckdemo.Constants.SP_DISCOVERY_TIMEOUT;

/**
 * Created by osminin on 29.11.2016.
 */

@Singleton
public final class FakeSPScanner implements SPScannerInterface {
    private static final String TAG = FakeSPScanner.class.getSimpleName();
    private static final long BACKPRESSURE_BUFFER_CAPACITY = 100000;
    private final Observable<Long> mIntervalProducer;
    private final int mSPCount;
    private List<String> mMacAddress;

    @Inject
    public FakeSPScanner(int fakeSPCount) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "FakeSPScanner()");
        mSPCount = fakeSPCount;
        //all devices should be updated during SP_DISCOVERY_TIMEOUT
        mIntervalProducer = Observable.interval(SP_DISCOVERY_TIMEOUT / (mSPCount + 1), TimeUnit.MILLISECONDS);
    }

    @Override
    public Observable<SensorPuckModel> startObserve() {
        return mIntervalProducer
                .onBackpressureBuffer(BACKPRESSURE_BUFFER_CAPACITY)
                .doOnSubscribe(this::generateMacAddresses)
                .subscribeOn(Schedulers.computation())
                .map(rndNumber -> SensorPuckParser.generateRandomModel((int) (rndNumber % mSPCount), mMacAddress))
                .observeOn(Schedulers.computation());
    }

    private void generateMacAddresses() {
        if (mMacAddress == null || mMacAddress.isEmpty()) {
            mMacAddress = new ArrayList<>(mSPCount);
            for (int i = 0; i < mSPCount; ++i) {
                mMacAddress.add(randomMACAddress(i));
            }
        }
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
