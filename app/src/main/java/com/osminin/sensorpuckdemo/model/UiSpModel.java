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

    public UiSpModel setModel(SensorPuckModel model) {
        this.mModel = model;
        return this;
    }

    public int getIndex() {
        return mIndex;
    }

    public UiSpModel setIndex(int index) {
        this.mIndex = index;
        return this;
    }

    public UiCommand getCommand() {
        return mCommand;
    }

    public UiSpModel setCommand(UiCommand command) {
        this.mCommand = command;
        return this;
    }

    public enum UiCommand {
        ADD_NEW,
        REPLACE,
        REMOVE
    }
}
