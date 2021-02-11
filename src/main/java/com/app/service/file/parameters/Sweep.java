package com.app.service.file.parameters;

public abstract class Sweep {

    protected double start;
    protected double stop;
    protected double step;
    protected double spot;


    /**
     *
     * @return
     */
    public double getStart() {
        return start;
    }

    /**
     *
     * @return
     */
    public double getStop() {
        return stop;
    }

    /**
     *
     * @return
     */
    public double getStep() {
        return step;
    }

    /**
     *
     * @return
     */
    public double getSpot() {
        return spot;
    }

    /**
     * Checks if values are in a valid range.
     * In case of invalid value, sets default value.
     */
    public abstract void check();
}
