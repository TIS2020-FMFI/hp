package com.app.service.measurement;

import com.app.service.file.parameters.EnvironmentParameters;
import com.app.service.file.parameters.Parameters;

import java.util.LinkedList;

public class Measurement {
    LinkedList<SingleValue> data;
    Parameters parameters;
    StringBuilder comment;
    int indexOfTheValueToSave = 0;
    MeasurementState state;

    public Measurement(Parameters parameters) {
        this.data = new LinkedList<>();
        this.parameters = parameters;
        this.state = MeasurementState.WAITING;
        comment = new StringBuilder();
    }

    public void addSingleValue(SingleValue singleValue){
        data.add(singleValue);
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

    public LinkedList<SingleValue> getData() {
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

    public void updateComment(String newValue) {
        comment = new StringBuilder(newValue);
//na testoch pada        AppMain.notificationService.createNotification("Comment saved", NotificationType.ANNOUNCEMENT);
    }

    public StringBuilder getComment() {
        return comment;
    }
}
