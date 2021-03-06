package com.osminin.sensorpuckdemo.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

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
    private int mRssi;
    private long mTimestamp;

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
        mRssi = in.readInt();
        mTimestamp = in.readLong();
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

    public int getRssi() {
        return mRssi;
    }

    public void setRssi(int rssi) {
        mRssi = rssi;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    public SignalStrength getSignalStrength() {
        SignalStrength signalStrength;
        if (getRssi() > -30) {
            signalStrength = SignalStrength.VERY_HIGH;
        } else if (getRssi() > -40) {
            signalStrength = SignalStrength.HIGH;
        } else if (getRssi() > -50) {
            signalStrength = SignalStrength.MEDIUM;
        } else if (getRssi() > -60) {
            signalStrength = SignalStrength.LOW;
        } else {
            signalStrength = SignalStrength.VERY_LOW;
        }
        return signalStrength;
    }

    public BatteryLevel getBatteryLevel() {
        BatteryLevel level;
        if (getBattery() > 2.9) {
            level = BatteryLevel.VERY_GOOD;
        } else if (getBattery() > 2.8) {
            level = BatteryLevel.GOOD;
        } else if (getBattery() > 2.7) {
            level = BatteryLevel.MEDIUM;
        } else if (getBattery() > 2.6) {
            level = BatteryLevel.BAD;
        } else {
            level = BatteryLevel.VERY_BAD;
        }
        return level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SensorPuckModel that = (SensorPuckModel) o;

        return getAddress().equals(that.getAddress()) && getName().equals(that.getName());

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
        dest.writeInt(mRssi);
        dest.writeLong(mTimestamp);
    }

    @Override
    public int compareTo(@NonNull SensorPuckModel model) {
        return mAddress.compareTo(model.getAddress());
    }

    @Override
    public String toString() {
        return "SensorPuckModel{" +
                "mHRMRate=" + mHRMRate +
                ", mHRMState=" + mHRMState +
                ", mRssi=" + mRssi +
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