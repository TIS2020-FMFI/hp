package com.app.service.graph.dataset;

import com.app.service.AppMain;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.SingleValue;
import javafx.application.Platform;
import org.jfree.data.DomainOrder;
import org.jfree.data.xy.AbstractXYDataset;

import java.util.*;


public class AutoUpdatingDataset extends AbstractXYDataset {

    private int sizeData = 0;
    private String name;
    private DatasetType type;
    private final long delay = 100;
    private final long visualDelay = 100;
    private List<SingleValue> values;
    private int cursor = -1;
    private long lastEvent;
    private Measurement measurement;
    private boolean stopMeasurement = false;

    public AutoUpdatingDataset(Measurement measurement, DatasetType type) {
        this.type = type;
        this.measurement = measurement;
        this.values = measurement.getData();
        lastEvent = System.currentTimeMillis();
    }

    public DomainOrder getDomainOrder() {
        return DomainOrder.ASCENDING;
    }

    public int getSeriesCount() {
        return 1;
    }

    public Comparable getSeriesKey(int series) {
        return measurement.getParameters().getDisplayYY().getX();
    }

    public int getItemCount(int series) {
        return cursor;
    }

    public void addValue(double x, double y) {
        cursor ++;
        fireDatasetChanged();
    }

    public double getYValue(int series, int item) {
        return type.equals(DatasetType.LEFT) ? values.get(item).getDisplayA():values.get(item).getDisplayB();
    }
    public double getXValue(int series, int item) {
        return values.get(item).getDisplayX();
    }

    public Double getY(int series, int item) {
        return getYValue(series, item);
    }
    public Double getX(int series, int item) {
        return getXValue(series, item);
    }

    public void abortMeasurement() {
        stopMeasurement = true;
    }

    public void start() {
        new Timer().schedule(new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    if (stopMeasurement) {
                        cancel();
                    }
                    if (AppMain.debugMode) {
                        getRandomTestData();
                    } else {
                        int currentSizeData = measurement.getData().size();
                        if (currentSizeData != sizeData) {
                            // TODO: when do we call cancel() ?
                            values.add(measurement.getData().getLast());

                            long now = System.currentTimeMillis();
                            if (now - lastEvent > visualDelay) {
                                lastEvent = now;
                                fireDatasetChanged();
                            }
                            sizeData = currentSizeData;
                        }
                    }
                });
            }
        }, delay, delay);
    }

    private void getRandomTestData() {
        values.add(new SingleValue(Math.random() * 20 + 80, Math.random() * 20 + 80, ++cursor));
        long now = System.currentTimeMillis();
        if (now - lastEvent > visualDelay) {
            lastEvent = now;
            fireDatasetChanged();
        }
    }
}

