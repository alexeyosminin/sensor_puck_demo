package com.osminin.sensorpuckdemo.ble;

import android.bluetooth.le.ScanResult;

import com.osminin.sensorpuckdemo.model.SensorPuckModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by osminin on 17.11.2016.
 */

final class SensorPuckParser {
    /* Sensor Data types */
    public static final int SD_MODE = 0;
    public static final int SD_SEQUENCE = 1;
    public static final int SD_HUMIDITY = 2;
    public static final int SD_TEMPERATURE = 3;
    public static final int SD_AMB_LIGHT = 4;
    public static final int SD_UV_LIGHT = 5;
    public static final int SD_BATTERY = 6;
    public static final int SD_HRM_STATE = 16;
    public static final int SD_HRM_RATE = 17;
    public static final int SD_HRM_SAMPLE = 18;
    public static final int ENVIRONMENTAL_MODE = 0;
    public static final int BIOMETRIC_MODE = 1;
    private static final String TAG = SensorPuckParser.class.getSimpleName();

    static boolean isSensorPuckRecord(ScanResult result) {
        byte[] data = result.getScanRecord().getBytes();
        return (data[4] == (-1)) && ((data[5] == 0x34) || (data[5] == 0x35)) && (data[6] == 0x12);
    }

    static SensorPuckModel parse(ScanResult result) {
        byte[] data = result.getScanRecord().getBytes();
        SensorPuckModel spModel = new SensorPuckModel();
        spModel.setAddress(result.getDevice().getAddress());
        spModel.setName(defaultName(spModel.getAddress()));
        spModel.setSignalStrength(result.getRssi());
        if (data[5] == 0x34) {
            // If its an old style advertisement
        } else if (data[5] == 0x35) {
            // If its a new style advertisement
            if (data[7] == ENVIRONMENTAL_MODE) {
                parseEnvironmental(spModel, data);
            }
            if (data[7] == BIOMETRIC_MODE) {
                parseBiometric(spModel, data);
            }
        }
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

    private static void parseEnvironmental(SensorPuckModel spModel, byte[] data) {
        spModel.setMeasurementMode(ENVIRONMENTAL_MODE);
        spModel.setSequence(Int8(data[5 + 3]));
        spModel.setHumidity(((float) Int16(data[8 + 3], data[9 + 3])) / 10.0f);
        spModel.setTemperature(((float) Int16(data[10 + 3], data[11 + 3])) / 10.0f);
        spModel.setAmbientLight(Int16(data[7 + 3], data[13 + 3]) * 7);
        spModel.setUVIndex(Int8(data[14 + 3]));
        spModel.setBattery(((float) Int8(data[15 + 3])) / 10.0f);
    }

    private static void parseBiometric(SensorPuckModel spModel, byte[] data) {
        spModel.setMeasurementMode(BIOMETRIC_MODE);
        spModel.setSequence(Int8(data[8]));
        spModel.setHRMState(Int8(data[11]));
        spModel.setHRMRate(Int8(data[12]));
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
