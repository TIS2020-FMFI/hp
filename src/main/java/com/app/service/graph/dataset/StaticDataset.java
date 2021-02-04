package com.app.service.graph.dataset;

import com.app.service.measurement.Measurement;
import com.app.service.measurement.SingleValue;
import javafx.application.Platform;
import org.jfree.data.DomainOrder;
import org.jfree.data.xy.AbstractXYDataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StaticDataset extends AbstractXYDataset {
    private DatasetType type;
    private List<SingleValue> values;
    private int sizeData = 0;
    private Measurement measurement;

    /**
     * Constructs dataset configured for loaded measurement
     *
     * @param measurement
     * @param type
     */
    public StaticDataset(Measurement measurement, DatasetType type) {
        this.type = type;
        this.measurement = measurement;
        this.values = new ArrayList<>();
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
        return sizeData;
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

    public void start() {
        new Timer().schedule(new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    if (measurement.getData().size() == sizeData) {
                        fireDatasetChanged();
                        cancel();
                        return;
                    }
                    values.add(measurement.getData().get(sizeData));
                    sizeData++;
                    fireDatasetChanged();
                });
            }
        }, 2, 2);
    }

}

