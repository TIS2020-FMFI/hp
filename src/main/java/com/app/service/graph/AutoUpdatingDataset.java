package com.app.service.graph;

import com.app.service.measurement.Measurement;
import com.app.service.measurement.SingleValue;
import javafx.application.Platform;
import org.jfree.data.DomainOrder;
import org.jfree.data.xy.AbstractXYDataset;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class AutoUpdatingDataset extends AbstractXYDataset {

    private int poc = 0;
    private int sizeData = 0;
    private String name;
    private int whichSeries = -1;
    private int max;
    private long delay;
    private long visualDelay;
    private double[][] values;
    private int cursor = -1;
    private Timer timer;
    private long lastEvent;
    private Measurement measurement;
    private boolean stopMeasurement = false;

    AutoUpdatingDataset(Measurement measurement, String name, int max, int delay, int visualDelay, int whichSeries) {
        this.whichSeries = whichSeries;
        this.measurement = measurement;
        this.name = name;
        this.max = max;
        this.delay = delay;
        this.visualDelay = visualDelay;
        this.values = new double[max][2];
        timer = new Timer();
        lastEvent = System.currentTimeMillis();
    }

    public DomainOrder getDomainOrder() {
        return DomainOrder.ASCENDING;
    }

    public int getSeriesCount() {
        return 1;
    }

    public Comparable getSeriesKey(int series) {
        return name;
    }

    public int getItemCount(int series) {
        return cursor;
    }

    public void addValue(double x, double y) {
        cursor ++;
        values[cursor][0] = x;
        values[cursor][1] = y;
        fireDatasetChanged();
    }

    public double getYValue(int series, int item) {
        return values[item][1];
    }

    public double getXValue(int series, int item) {
        return values[item][0];
    }

    public Double getY(int series, int item) {
        return new Double(getYValue(series, item));
    }

    public Double getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

    public void setDelay(long delay, long visualDelay) {
        this.delay = delay;
        this.visualDelay = visualDelay;
    }

    public void abortMeasurement() {
        stopMeasurement = true;
    }

    public void start() {
        // check size() of Measurement.data everySecond

        new Timer().schedule(new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    if (stopMeasurement) {
                        cancel();
                    }
                    if (cursor >= max - 1) {
                        timer.cancel();
                        return;
                    }
                    // This is random data, should be commented if real data run, just for trial
//                    cursor++;
//                    values[cursor][0] = poc;
//                    values[cursor][1] = (Math.random() * 20 + 80);
//                    poc++;
//                    long now = System.currentTimeMillis();
//
//                    if (now - lastEvent > visualDelay) {
//                        lastEvent = now;
//                        fireDatasetChanged();
//                    }

//                    // Real data - Prepared for tommorow
                    int currentSizeData = measurement.getData().size();
                    if (currentSizeData != sizeData) {
                        SingleValue singleValue = measurement.getData().getLast();
                        double valueY = -1000000000;  // initialized at not possible to obtain value
                        if (whichSeries == 0) {
                            valueY = singleValue.getDisplayA();
                        }
                        if (whichSeries == 1) {
                            valueY = singleValue.getDisplayB();
                        }
                        if (valueY == -1000000000) {
                            cancel(); // cannot obtain value from singleValue;
                        }
                        cursor++;
                        values[cursor][0] = singleValue.getDisplayX();
                        values[cursor][whichSeries + 1] = valueY;
                        long now = System.currentTimeMillis();

                        if (now - lastEvent > visualDelay) {
                            lastEvent = now;
                            fireDatasetChanged();
                        }
                        sizeData = currentSizeData;
                    }
                });
            }
        }, delay, delay);
        // .cancel() to terminate both timerTask and timer
    }
}

