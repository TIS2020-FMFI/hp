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
}
