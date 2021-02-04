package com.app.service.measurement;

import com.app.service.file.parameters.Parameters;

import java.util.List;
import java.util.Vector;


public class Measurement {
    Vector<SingleValue> data;
    Parameters parameters;
    int indexOfTheValueToSave = 0;
    MeasurementState state;

    public Measurement(Parameters parameters) {
        this.data = new Vector<>();
        this.parameters = parameters;
        this.state = MeasurementState.WAITING;
    }

    public void addSingleValue(SingleValue singleValue){
        data.add(singleValue);
        System.out.println("new value added: " + singleValue);
        if(singleValue == null){
            state = MeasurementState.FINISHED;
        }
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public void setIndexOfTheValueToSave(int indexOfTheValueToSave) {
        this.indexOfTheValueToSave = indexOfTheValueToSave;
    }

    public void setState(MeasurementState state) {
        this.state = state;
    }

    public Vector<SingleValue> getData() {
        return data;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public int getIndexOfTheValueToSave() {
        return indexOfTheValueToSave;
    }

    public MeasurementState getState() {
        return state;
    }

    public boolean canLooseData() {
        return List.of(MeasurementState.STARTED, MeasurementState.WAITING, MeasurementState.FINISHED).contains(state);
    }
}
