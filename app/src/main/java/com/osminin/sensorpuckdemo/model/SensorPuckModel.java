package com.osminin.sensorpuckdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by osminin on 09.11.2016.
 */

public final class SensorPuckModel implements Parcelable, Comparable<SensorPuckModel> {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SensorPuckModel> CREATOR = new Parcelable.Creator<SensorPuckModel>() {
        @Override
        public SensorPuckModel createFromParcel(Parcel in) {
            return new SensorPuckModel(in);
        }

        @Override
        public SensorPuckModel[] newArray(int size) {
            return new SensorPuckModel[size];
        }
    };
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
    private int mSignalStrength;

    public SensorPuckModel() {
    }

    protected SensorPuckModel(Parcel in) {
        mAddress = in.readString();
        mName = in.readString();
        mMeasurementMode = in.readInt();
        mSequence = in.readInt();
        mHumidity = in.readFloat();
        mTemperature = in.readFloat();
        mAmbientLight = in.readInt();
        mUVIndex = in.readInt();
        mBattery = in.readFloat();
        mHRMState = in.readInt();
        mHRMRate = in.readInt();
        if (in.readByte() == 0x01) {
            mHRMSample = new ArrayList<>();
            in.readList(mHRMSample, Integer.class.getClassLoader());
        } else {
            mHRMSample = null;
        }
        mHRMPrevSample = in.readInt();
        mPrevSequence = in.readInt();
        mRecvCount = in.readInt();
        mPrevCount = in.readInt();
        mUniqueCount = in.readInt();
        mLostAdv = in.readInt();
        mLostCount = in.readInt();
        mIdleCount = in.readInt();
        mSignalStrength = in.readInt();
    }

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
        if (mHRMSample == null) {
            mHRMSample = new ArrayList<>();
        }
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

    public int getSignalStrength() {
        return mSignalStrength;
    }

    public void setSignalStrength(int signalStrength) {
        mSignalStrength = signalStrength;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAddress);
        dest.writeString(mName);
        dest.writeInt(mMeasurementMode);
        dest.writeInt(mSequence);
        dest.writeFloat(mHumidity);
        dest.writeFloat(mTemperature);
        dest.writeInt(mAmbientLight);
        dest.writeInt(mUVIndex);
        dest.writeFloat(mBattery);
        dest.writeInt(mHRMState);
        dest.writeInt(mHRMRate);
        if (mHRMSample == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mHRMSample);
        }
        dest.writeInt(mHRMPrevSample);
        dest.writeInt(mPrevSequence);
        dest.writeInt(mRecvCount);
        dest.writeInt(mPrevCount);
        dest.writeInt(mUniqueCount);
        dest.writeInt(mLostAdv);
        dest.writeInt(mLostCount);
        dest.writeInt(mIdleCount);
        dest.writeInt(mSignalStrength);
    }

    @Override
    public int compareTo(SensorPuckModel model) {
        return mAddress.compareTo(model.getAddress());
    }

    @Override
    public String toString() {
        return "SensorPuckModel{" +
                "mHRMRate=" + mHRMRate +
                ", mHRMState=" + mHRMState +
                ", mSignalStrength=" + mSignalStrength +
                ", mHRMSample=" + mHRMSample +
                ", mBattery=" + mBattery +
                ", mUVIndex=" + mUVIndex +
                ", mAmbientLight=" + mAmbientLight +
                ", mTemperature=" + mTemperature +
                ", mHumidity=" + mHumidity +
                ", mSequence=" + mSequence +
                ", mMeasurementMode=" + mMeasurementMode +
                ", mName='" + mName + '\'' +
                ", mAddress='" + mAddress + '\'' +
                '}';
    }
}