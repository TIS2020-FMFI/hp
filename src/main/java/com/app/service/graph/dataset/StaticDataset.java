package com.app.service.graph.dataset;

import com.app.service.measurement.Measurement;
import com.app.service.measurement.SingleValue;
import org.jfree.data.DomainOrder;
import org.jfree.data.xy.AbstractXYDataset;

import java.util.List;


public class StaticDataset extends AbstractXYDataset {
    private DatasetType type;
    private List<SingleValue> values;
    private int cursor = -1;
    private Measurement measurement;

    public StaticDataset(Measurement measurement, DatasetType type) {
        this.type = type;
        this.measurement = measurement;
        this.values = measurement.getData();
        this.cursor += measurement.getData().size()+1;
        fireDatasetChanged();
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

    public double getYValue(int series, int item) {
        return type.equals(DatasetType.LEFT) ? values.get(item).getDisplayA() : values.get(item).getDisplayB();
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

}

