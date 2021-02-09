package com.app.service.file.parameters;


public class FrequencySweep extends Sweep {

    static final double minStart = 1;
    static final double maxStart = 1000;
    static final double minStop = 1;
    static final double maxStop = 1000;
    static final double minStep = 0.1;
    static final double maxStep = 999;
    static final double minSpot = 1;
    static final double maxSpot = 1000;

    /**
     *
     * @param start
     */
    public void setStart(Double start) {
        if(start < minStart) super.start = minStart;
        else if(start > maxStart) super.start = maxStart;
        else super.start = start;
    }

    /**
     *
     * @param stop
     */
    public void setStop(Double stop) {
        if(stop < minStop) super.stop = minStop;
        else if(stop > maxStop) super.stop = maxStop;
        else super.stop = stop;
    }

    /**
     *
     * @param step
     */
    public void setStep(Double step) {
        if(step < minStep)super.step = minStep;
        else if(step > maxStep) super.step = maxStep;
        else super.step = step;
    }


    /**
     *
     * @param spot
     */
    public void setSpot(Double spot) {
        if(spot < minSpot ) super.spot = minSpot;
        else if(spot > maxSpot) super.spot = maxSpot;
        else super.spot = spot;
    }

    /**
     *
     * @return
     */
    public double getMinStart() {
        return minStart;
    }

    /**
     *
     * @return
     */
    public double getMinStop() {
        return minStop;
    }

    /**
     *
     * @return
     */
    public double getMinStep() {
        return minStep;
    }

    /**
     *
     * @return
     */
    public double getMinSpot() {
        return minSpot;
    }

    /**
     *
     * @return
     */
    public double getMaxStart() {
        return maxStart;
    }

    /**
     *
     * @return
     */
    public double getMaxStop() {
        return maxStop;
    }

    /**
     *
     * @return
     */
    public double getMaxStep() {
        return maxStep;
    }

    /**
     *
     * @return
     */
    public double getMaxSpot() {
        return maxSpot;
    }

    /**
     * Checks if values are in a valid range.
     * In case of invalid value, sets default value.
     */
    @Override
    public void check() {
        setStart(super.start);
        setStop(super.stop);
        setSpot(super.spot);
        setStep(super.step);
    }
}
