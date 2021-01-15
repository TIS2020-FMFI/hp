package com.app.service.graph.dataset;

import com.app.service.measurement.Measurement;
import com.app.service.measurement.SingleValue;
import org.jfree.data.DomainOrder;
import org.jfree.data.xy.AbstractXYDataset;

import java.util.List;
import java.util.Timer;


public class StaticDataset extends AbstractXYDataset {
    private DatasetType type;
    private List<SingleValue> values;
    private int cursor = -1;
    private Timer timer;
    private long lastEvent;
    private Measurement measurement;
    private boolean stopMeasurement = false;

    public StaticDataset(Measurement measurement, DatasetType type) {
        this.type = type;
        this.measurement = measurement;
        values = measurement.getData();
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
        return measurement.getData().get(0).getDisplayX();
    }

    public int getItemCount(int series) {
        return cursor;
    }

    public void addValue(double x, double y) {
        cursor ++;
//        values[cursor][0] = x;
//        values[cursor][1] = y;
        fireDatasetChanged();
    }

    public double getYValue(int series, int item) {
        return type.equals(DatasetType.LEFT) ? values.get(item).getDisplayA():values.get(item).getDisplayB();
    }
    public double getXValue(int series, int item) {
        return values.get(item).getDisplayX();
    }

    public Double getY(int series, int item) {
        return new Double(getYValue(series, item));
    }

    public Double getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

}

