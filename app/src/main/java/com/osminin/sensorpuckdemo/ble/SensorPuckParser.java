package com.osminin.sensorpuckdemo.ble;

import android.bluetooth.le.ScanResult;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by osminin on 17.11.2016.
 */

final class SensorPuckParser {
    /* Sensor Data types */
    private static final int ADVERTISEMENT_STYLE_INDEX = 5;
    private static final int MODE_INDEX = 7;
    private static final int SD_SEQUENCE = 1;
    private static final int SD_HUMIDITY = 2;
    private static final int SD_TEMPERATURE = 3;
    private static final int SD_AMB_LIGHT = 4;
    private static final int SD_UV_LIGHT = 5;
    private static final int SD_BATTERY = 6;
    private static final int SD_HRM_STATE = 16;
    private static final int SD_HRM_RATE = 17;
    private static final int SD_HRM_SAMPLE = 18;
    private static final int ENVIRONMENTAL_MODE = 0;
    private static final int BIOMETRIC_MODE = 1;
    private static final int HRM_SAMPLE_COUNT = 5;

    private static final int ADVERTISEMENT_OLD = 0x34;
    private static final int ADVERTISEMENT_NEW = 0x35;

    //min and max values for fake data
    private static final int HRM_RATE_MAX = 120;
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

    private static final String TAG = SensorPuckParser.class.getSimpleName();

    static boolean isSensorPuckRecord(ScanResult result) {
        byte[] data = result.getScanRecord().getBytes();
        boolean res = (data[4] == (-1))
                && ((data[ADVERTISEMENT_STYLE_INDEX] == ADVERTISEMENT_OLD)
                || (data[ADVERTISEMENT_STYLE_INDEX] == ADVERTISEMENT_NEW))
                && (data[6] == 0x12);
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "isSensorPuckRecord: "
                + result.getDevice().getAddress() + " - " + res);
        return res;
    }

    static SensorPuckModel parse(ScanResult result) {
        byte[] data = result.getScanRecord().getBytes();
        SensorPuckModel spModel = new SensorPuckModel();
        spModel.setAddress(result.getDevice().getAddress());
        spModel.setName(defaultName(spModel.getAddress()));
        spModel.setSignalStrength(result.getRssi());
        spModel.setTimestamp(System.currentTimeMillis());
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

    static List<SensorPuckModel> parseBatchResult(List<ScanResult> results) {
        List<SensorPuckModel> spModels = new ArrayList<>(results.size());
        for (ScanResult result : results) {
            if (isSensorPuckRecord(result)) {
                spModels.add(parse(result));
            }
        }
        return spModels;
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
        spModel.setSignalStrength(rnd.nextInt(SIGNAL_STRENGTH_MAX - SIGNAL_STRENGTH_MIN) + SIGNAL_STRENGTH_MIN);
        spModel.setTimestamp(System.currentTimeMillis());
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "generateRandomModel: " + spModel.getName());
        return spModel;
    }

    private static void parseEnvironmental(SensorPuckModel spModel, byte[] data) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "parseEnvironmental: " + spModel.getName());
        spModel.setMeasurementMode(ENVIRONMENTAL_MODE);
        spModel.setSequence(Int8(data[5 + 3]));
        spModel.setHumidity(((float) Int16(data[8 + 3], data[9 + 3])) / 10.0f);
        spModel.setTemperature(((float) Int16(data[10 + 3], data[11 + 3])) / 10.0f);
        spModel.setAmbientLight(Int16(data[7 + 3], data[13 + 3]) * 7);
        spModel.setUVIndex(Int8(data[14 + 3]));
        spModel.setBattery(((float) Int8(data[15 + 3])) / 10.0f);
    }

    private static void parseBiometric(SensorPuckModel spModel, byte[] data) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "parseBiometric: " + spModel.getName());
        spModel.setMeasurementMode(BIOMETRIC_MODE);
        spModel.setSequence(Int8(data[8]));
        spModel.setHRMState(Int8(data[11]));
        spModel.setHRMRate(Int8(data[12]));

        for (int x = 0; x < HRM_SAMPLE_COUNT; x++) {
            spModel.getHRMSample().add(Int16(data[5 + (x * 2) + 8], data[6 + (x * 2) + 8]));
        }
    }

    private static int Int8(byte data) {
        return ((char) data) & 0xff;
    }

    private static int Int16(byte lsb, byte msb) {
        return Int8(lsb) + (Int8(msb) * 0x00000100);
    }

    private static String defaultName(String address) {
        String[] part = address.split(":");
        return "Puck_" + part[4] + part[5];
    }
}
