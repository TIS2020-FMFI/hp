package com.app.service.measurement;

import com.app.service.file.parameters.EnvironmentParameters;

import java.util.LinkedList;

public class Measurement {
    LinkedList<SingleValue> data;
    EnvironmentParameters parameters;
    int indexOfTheValueToSave = 0;
    MeasurementState state;

    public Measurement(EnvironmentParameters parameters) throws Exception {
        this.data = new LinkedList<SingleValue>();
        this.parameters = parameters;
        this.state = MeasurementState.WAITING;
    }

    public void addSingleValue(SingleValue singleValue){
        data.add(singleValue);
    }

    public void setParameters(EnvironmentParameters parameters) {
        this.parameters = parameters;
    }

    public void setIndexOfTheValueToSave(int indexOfTheValueToSave) {
        this.indexOfTheValueToSave = indexOfTheValueToSave;
    }

    public void setState(MeasurementState state) {
        this.state = state;
    }

    public LinkedList<SingleValue> getData() {
        return data;
    }

    public EnvironmentParameters getParameters() {
        return parameters;
    }

    public int getIndexOfTheValueToSave() {
        return indexOfTheValueToSave;
    }

    public MeasurementState getState() {
        return state;
    }
}
