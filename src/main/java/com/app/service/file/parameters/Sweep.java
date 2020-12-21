package com.app.service.file.parameters;

public abstract class Sweep {

    protected double start;
    protected double stop;
    protected double step;
    protected double spot;



    public double getStart() {
        return start;
    }

    public double getStop() {
        return stop;
    }

    public double getStep() {
        return step;
    }

    public double getSpot() {
        return spot;
    }

}
