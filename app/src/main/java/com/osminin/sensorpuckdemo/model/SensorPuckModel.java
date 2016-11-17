package com.osminin.sensorpuckdemo.model;

import android.content.Intent;

import java.util.List;

/**
 * Created by osminin on 09.11.2016.
 */

public final class SensorPuckModel {

    private String mAddress;
    private String mName;

    private int mMeasurementMode;
    private int mSequence;
    private float mHumidity;
    private float mTemperature;
    private int mAmbientLight;
    private int mUVIndex;
    private float mBattery;
    private int mHRMState;
    private int mHRMRate;
    private List<Integer> mHRMSample;
    private int mHRMPrevSample;

    private int mPrevSequence;
    private int mRecvCount;
    private int mPrevCount;
    private int mUniqueCount;
    private int mLostAdv;
    private int mLostCount;
    private int mIdleCount;

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getMeasurementMode() {
        return mMeasurementMode;
    }

    public void setMeasurementMode(int measurementMode) {
        mMeasurementMode = measurementMode;
    }

    public int getSequence() {
        return mSequence;
    }

    public void setSequence(int sequence) {
        mSequence = sequence;
    }

    public float getHumidity() {
        return mHumidity;
    }

    public void setHumidity(float humidity) {
        mHumidity = humidity;
    }

    public float getTemperature() {
        return mTemperature;
    }

    public void setTemperature(float temperature) {
        mTemperature = temperature;
    }

    public int getAmbientLight() {
        return mAmbientLight;
    }

    public void setAmbientLight(int ambientLight) {
        mAmbientLight = ambientLight;
    }

    public int getUVIndex() {
        return mUVIndex;
    }

    public void setUVIndex(int UVIndex) {
        mUVIndex = UVIndex;
    }

    public float getBattery() {
        return mBattery;
    }

    public void setBattery(float battery) {
        mBattery = battery;
    }

    public int getHRMState() {
        return mHRMState;
    }

    public void setHRMState(int HRMState) {
        mHRMState = HRMState;
    }

    public int getHRMRate() {
        return mHRMRate;
    }

    public void setHRMRate(int HRMRate) {
        mHRMRate = HRMRate;
    }

    public List<Integer> getHRMSample() {
        return mHRMSample;
    }

    public void setHRMSample(List<Integer> HRMSample) {
        mHRMSample = HRMSample;
    }

    public int getHRMPrevSample() {
        return mHRMPrevSample;
    }

    public void setHRMPrevSample(int HRMPrevSample) {
        mHRMPrevSample = HRMPrevSample;
    }

    public int getPrevSequence() {
        return mPrevSequence;
    }

    public void setPrevSequence(int prevSequence) {
        mPrevSequence = prevSequence;
    }

    public int getRecvCount() {
        return mRecvCount;
    }

    public void setRecvCount(int recvCount) {
        mRecvCount = recvCount;
    }

    public int getPrevCount() {
        return mPrevCount;
    }

    public void setPrevCount(int prevCount) {
        mPrevCount = prevCount;
    }

    public int getUniqueCount() {
        return mUniqueCount;
    }

    public void setUniqueCount(int uniqueCount) {
        mUniqueCount = uniqueCount;
    }

    public int getLostAdv() {
        return mLostAdv;
    }

    public void setLostAdv(int lostAdv) {
        mLostAdv = lostAdv;
    }

    public int getLostCount() {
        return mLostCount;
    }

    public void setLostCount(int lostCount) {
        mLostCount = lostCount;
    }

    public int getIdleCount() {
        return mIdleCount;
    }

    public void setIdleCount(int idleCount) {
        mIdleCount = idleCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SensorPuckModel that = (SensorPuckModel) o;

        if (!getAddress().equals(that.getAddress())) return false;
        return getName().equals(that.getName());

    }

    @Override
    public int hashCode() {
        int result = getAddress().hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }
}
