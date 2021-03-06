package com.app.service.graph.dataset;

import com.app.service.AppMain;
import com.app.service.graph.GraphState;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.MeasurementState;
import com.app.service.measurement.SingleValue;
import javafx.application.Platform;
import org.jfree.data.DomainOrder;
import org.jfree.data.xy.AbstractXYDataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AutoUpdatingDataset extends AbstractXYDataset {
    private int sizeData = 0;
    private String name;
    private DatasetType type;
    private final long delay = 20;
    private final long visualDelay = 100;
    private List<SingleValue> values;
    private long lastEvent;
    private Measurement measurement;
    private boolean stopMeasurement = false;

    /**
     * Constructs dataset configured for real time plotting. Has a timer, that can be started and then it watches over
     * measurement. If there are new data in measurement, it notifies listeners. (e.g. plot to repaint)
     *
     * @param measurement
     * @param type serves to clarify which y axis is used in this instantiation
     */
    public AutoUpdatingDataset(Measurement measurement, DatasetType type) {
        this.type = type;
        this.measurement = measurement;
        this.values = new ArrayList<>();
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

    public void abortMeasurement() {
        stopMeasurement = true;
    }

    public void start() {
        new Timer().schedule(new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    if (stopMeasurement) {
                        cancel();
                        return;
                    }
                    if (measurement.getData().size() > sizeData) {
                        SingleValue newValue = measurement.getData().get(sizeData);
//                        System.out.println("read " + type + ": " + newValue);
                        if (newValue == null) {
                            fireDatasetChanged();
                            cancel();
                            if (AppMain.graphService.getRunningGraph() != null) {
                                AppMain.graphService.getRunningGraph().setState(GraphState.LOADED);
                            }
                            measurement.setState(MeasurementState.FINISHED);
                            return;
                        }
                        values.add(newValue);
                        sizeData++;

//
//                        long now = System.currentTimeMillis();
//                        if (now - lastEvent > visualDelay) {
//                            lastEvent = now;
                          fireDatasetChanged();

//                        }
                    }
                });
            }
        }, delay, delay);
    }
}

