package com.app.service.measurement;

import com.app.service.AppMain;
import com.app.service.file.parameters.EnvironmentParameters;
import com.app.service.notification.NotificationType;

import java.util.LinkedList;

public class Measurement {
    LinkedList<SingleValue> data;
    EnvironmentParameters parameters;
    StringBuilder comment;
    int indexOfTheValueToSave = 0;
    MeasurementState state;

    public Measurement(EnvironmentParameters parameters) {
        this.data = new LinkedList<>();
        this.parameters = parameters;
        this.state = MeasurementState.WAITING;
        comment = new StringBuilder();
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

    public void updateComment(String newValue) {
        comment = new StringBuilder(newValue);
        AppMain.notificationService.createNotification("Comment saved", NotificationType.ANNOUNCEMENT);
    }
}
