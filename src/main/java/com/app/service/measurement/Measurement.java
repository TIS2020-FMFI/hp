package com.app.service.measurement;

import com.app.service.file.parameters.Parameters;

import java.util.List;
import java.util.Vector;


public class Measurement {
    Vector<SingleValue> data;
    Parameters parameters;
    int indexOfTheValueToSave = 0;
    MeasurementState state;

    /**
     * Constructor sets parameters, waiting state and initializes vector.
     *
     * @param parameters
     */
    public Measurement(Parameters parameters) {
        this.data = new Vector<>();
        this.parameters = parameters;
        this.state = MeasurementState.WAITING;
    }

    /**
     * Sets value to vector of values.
     * Sets finished state if singleValue is null.
     *
     * @param singleValue
     */
    public void addSingleValue(SingleValue singleValue){
        data.add(singleValue);
        System.out.println("new value added: " + singleValue);
        if(singleValue == null){
            state = MeasurementState.FINISHED;
        }
    }

    /**
     *
     * @param parameters
     */
    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    /**
     *
     * @param indexOfTheValueToSave
     */
    public void setIndexOfTheValueToSave(int indexOfTheValueToSave) {
        this.indexOfTheValueToSave = indexOfTheValueToSave;
    }

    /**
     *
     * @param state
     */
    public void setState(MeasurementState state) {
        this.state = state;
    }

    /**
     *
     * @return
     */
    public Vector<SingleValue> getData() {
        return data;
    }

    /**
     *
     * @return
     */
    public Parameters getParameters() {
        return parameters;
    }

    /**
     *
     * @return
     */
    public int getIndexOfTheValueToSave() {
        return indexOfTheValueToSave;
    }

    /**
     *
     * @return
     */
    public MeasurementState getState() {
        return state;
    }

    /**
     * Checks by state if data can be lost.
     *
     * @return
     */
    public boolean canLooseData() {
        return List.of(MeasurementState.STARTED, MeasurementState.WAITING, MeasurementState.FINISHED).contains(state);
    }
}
