package com.osminin.sensorpuckdemo.model;

/**
 * TODO: Add a class header comment!
 */

public class UiSpModel {
    private SensorPuckModel mModel;
    private int mIndex;
    private UiCommand mCommand;

    public SensorPuckModel getModel() {
        return mModel;
    }

    public void setModel(SensorPuckModel model) {
        this.mModel = model;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }

    public UiCommand getCommand() {
        return mCommand;
    }

    public void setCommand(UiCommand command) {
        this.mCommand = command;
    }

    public enum UiCommand {
        ADD_NEW,
        REPLACE,
        REMOVE
    }
}
