package com.osminin.sensorpuckdemo.ble;

import android.bluetooth.le.ScanResult;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;
import com.polidea.rxandroidble.RxBleScanResult;

import java.util.List;
import java.util.Random;

/**
 * Created by osminin on 17.11.2016.
 */

final class SensorPuckParser {
    private static final int ENVIRONMENTAL_MODE = 0;
    private static final int BIOMETRIC_MODE = 1;
    private static final int HRM_SAMPLE_COUNT = 5;

    private static final int ADVERTISEMENT_OLD = 0x34;
    private static final int ADVERTISEMENT_NEW = 0x35;

    private static final int ADVERTISEMENT_STYLE_INDEX = 5;
    private static final int MODE_INDEX = 7;
    private static final int SEQUENCE_INDEX = 8;
    private static final int LIGHT_INDEX_LSB = 10;
    private static final int HUNIDITY_INDEX_LSB = 11;
    private static final int HUNIDITY_INDEX_MSB = 12;
    private static final int HRM_INDEX_LSB = 11;
    private static final int HRM_INDEX_MSB = 12;
    private static final int TEMPERATURE_INDEX_LSB = 13;
    private static final int TEMPERATURE_INDEX_MSB = 14;
    private static final int LIGHT_INDEX_MSB = 16;
    private static final int UV_INDEX = 17;
    private static final int BATTERY_INDEX = 18;


    //min and max values for fake data
    private static final int HRM_RATE_MAX = 180;
    private static final int HRM_RATE_MIN = 40;
    private static final int SIGNAL_STRENGTH_MAX = -30;
    private static final int SIGNAL_STRENGTH_MIN = -70;
    private static final int TEMPERATURE_MAX = 30;
    private static final int TEMPERATURE_MIN = 20;
    private static final int HUMIDITY_MAX = 30;
    private static final int HUMIDITY_MIN = 20;
    private static final float BATTERY_MAX = 3.1f;
    private static final float BATTERY_MIN = 2.5f;
    private static final int LIGHT_MAX = 1000;
    private static final int LIGHT_MIN = 700;

    private static final int MAC_LAST_PAIR = 5;
    private static final int MAC_PRE_LAST_PAIR = 4;

    private static final String TAG = SensorPuckParser.class.getSimpleName();

    static boolean isSensorPuckRecord(RxBleScanResult result) {
        byte[] data = result.getScanRecord();
        boolean res = (data[4] == (-1))
                && ((data[ADVERTISEMENT_STYLE_INDEX] == ADVERTISEMENT_OLD)
                || (data[ADVERTISEMENT_STYLE_INDEX] == ADVERTISEMENT_NEW))
                && (data[6] == 0x12);
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "isSensorPuckRecord: "
                + result.getBleDevice().getMacAddress() + " - " + res);
        return res;
    }

    static SensorPuckModel parse(RxBleScanResult result) {
        byte[] data = result.getScanRecord();
        SensorPuckModel spModel = new SensorPuckModel();
        spModel.setAddress(result.getBleDevice().getMacAddress());
        spModel.setName(defaultName(spModel.getAddress()));
        spModel.setRssi(result.getRssi());
        spModel.setTimestamp(System.currentTimeMillis());
        //noinspection StatementWithEmptyBody
        if (data[ADVERTISEMENT_STYLE_INDEX] == ADVERTISEMENT_OLD) {
            // If its an old style advertisement
            //TODO:
        } else if (data[ADVERTISEMENT_STYLE_INDEX] == ADVERTISEMENT_NEW) {
            // If its a new style advertisement
            if (data[MODE_INDEX] == ENVIRONMENTAL_MODE) {
                parseEnvironmental(spModel, data);
            }
            if (data[MODE_INDEX] == BIOMETRIC_MODE) {
                parseBiometric(spModel, data);
            }
        }
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "parsed: " + spModel.getName());
        return spModel;
    }

    static SensorPuckModel generateRandomModel(int seed, List<String> address) {
        SensorPuckModel spModel = new SensorPuckModel();
        spModel.setAddress(address.get(seed));
        spModel.setName(defaultName(spModel.getAddress()));
        Random rnd = new Random(System.currentTimeMillis() / (seed + 1));
        spModel.setTemperature(rnd.nextFloat() * (TEMPERATURE_MAX - TEMPERATURE_MIN) + TEMPERATURE_MIN);
        spModel.setHumidity(rnd.nextFloat() * (HUMIDITY_MAX - HUMIDITY_MIN) + HUMIDITY_MIN);
        spModel.setBattery(rnd.nextFloat() * (BATTERY_MAX - BATTERY_MIN) + BATTERY_MIN);
        spModel.setAmbientLight(rnd.nextInt(LIGHT_MAX - LIGHT_MIN) + LIGHT_MIN);
        spModel.setRssi(rnd.nextInt(SIGNAL_STRENGTH_MAX - SIGNAL_STRENGTH_MIN) + SIGNAL_STRENGTH_MIN);
        spModel.setTimestamp(System.currentTimeMillis());
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "generateRandomModel: " + spModel.getName());
        return spModel;
    }

    private static void parseEnvironmental(SensorPuckModel spModel, byte[] data) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "parseEnvironmental: " + spModel.getName());
        spModel.setMeasurementMode(ENVIRONMENTAL_MODE);
        spModel.setSequence(Int8(data[SEQUENCE_INDEX]));
        spModel.setHumidity(((float) Int16(data[HUNIDITY_INDEX_LSB], data[HUNIDITY_INDEX_MSB])) / 10.0f);
        spModel.setTemperature(((float) Int16(data[TEMPERATURE_INDEX_LSB], data[TEMPERATURE_INDEX_MSB])) / 10.0f);
        spModel.setAmbientLight(Int16(data[LIGHT_INDEX_LSB], data[LIGHT_INDEX_MSB]) * 7);
        spModel.setUVIndex(Int8(data[UV_INDEX]));
        spModel.setBattery(((float) Int8(data[BATTERY_INDEX])) / 10.0f);
        spModel.setTimestamp(System.currentTimeMillis());
    }

    private static void parseBiometric(SensorPuckModel spModel, byte[] data) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "parseBiometric: " + spModel.getName());
        spModel.setMeasurementMode(BIOMETRIC_MODE);
        spModel.setSequence(Int8(data[SEQUENCE_INDEX]));
        spModel.setHRMState(Int8(data[HRM_INDEX_LSB]));
        spModel.setHRMRate(Int8(data[HRM_INDEX_MSB]));
        spModel.setTimestamp(System.currentTimeMillis());

        for (int i = 0; i < HRM_SAMPLE_COUNT; i++) {
            spModel.getHRMSample().add(Int16(data[HRM_SAMPLE_COUNT + (i * 2) + SEQUENCE_INDEX],
                    data[HRM_SAMPLE_COUNT + 1 + (i * 2) + SEQUENCE_INDEX]));
        }
    }

    private static int Int8(byte data) {
        return ((char) data) & 0xff;
    }

    private static int Int16(byte lsb, byte msb) {
        return Int8(lsb) | (Int8(msb) << 8);
    }

    private static String defaultName(String address) {
        String[] part = address.split(":");
        return "Puck_" + part[MAC_PRE_LAST_PAIR] + part[MAC_LAST_PAIR];
    }
}
