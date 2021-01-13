package com.app.service.file.parameters;

public class VoltageSweep extends Sweep {
    static final double minStart = -40;
    static final double maxStart = 40;
    static final double minStop = -40;
    static final double maxStop = 40;
    static final double minStep = 0.01;
    static final double maxStep = 40;
    static final double minSpot = -40;
    static final double maxSpot = 40;


    public void setStart(Double start) {
        if(start < minStart) super.start = minStart;
        else if(start > maxStart) super.start = maxStart;
        else super.start = start;
    }


    public void setStop(Double stop) {
        if(stop < minStop) super.stop = minStop;
        else if(stop > maxStop) super.stop = maxStop;
        else super.stop = stop;
    }


    public void setStep(Double step) {
        if(step < minStep)super.step = minStep;
        else if(step > maxStep) super.step = maxStep;
        else super.step = step;
    }


    public void setSpot(Double spot) {
        if(spot < minSpot ) super.spot = minSpot;
        else if(spot > maxSpot) super.spot = maxSpot;
        else super.spot = spot;
    }


    public double getMinStart() {
        return minStart;
    }

    public double getMinStop() {
        return minStop;
    }

    public double getMinStep() {
        return minStep;
    }

    public double getMinSpot() {
        return minSpot;
    }

    public static double getMaxStart() {
        return maxStart;
    }

    public static double getMaxStop() {
        return maxStop;
    }

    public static double getMaxStep() {
        return maxStep;
    }

    public static double getMaxSpot() {
        return maxSpot;
    }

    @Override
    public void check() {
        setStart(super.start);
        setStop(super.stop);
        setSpot(super.spot);
        setStep(super.step);
    }
}
