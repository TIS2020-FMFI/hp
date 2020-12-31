package com.app.service.graph;

import org.jfree.data.DomainOrder;
import org.jfree.data.xy.AbstractXYDataset;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;


public class AutoUpdatingDataset extends AbstractXYDataset implements ActionListener {

    long first;
    int poc = 0;
    private String name;
    private int max;
    private long delay;
    private long visualDelay;
    private double[][] values;
    private int cursor = -1;
    private Timer timer;
    private Random random = new Random();
    private long lastEvent;
    private long period;
    private double yDataMax = Math.PI * 10;

    AutoUpdatingDataset(String name, int max, int delay, int visualDelay) {
        this.name = name;
        this.max = max;
        this.delay = delay;
        this.visualDelay = visualDelay;
        this.values = new double[max][2];
        timer = new Timer(delay, this);
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
        values[cursor][0] = x;
        values[cursor][1] = y;
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

    public void actionPerformed(ActionEvent ae) {
        if (cursor >= max - 1) {
            timer.stop();
            return;
        }
        cursor++;
        values[cursor][0] = poc;
        values[cursor][1] = (Math.random() * 20 + 80);
        poc++;
        long now = System.currentTimeMillis();

        if (now - lastEvent > visualDelay) {
            lastEvent = now;
            fireDatasetChanged();
        }
    }

    public void start() {
        timer.start();
    }
}
