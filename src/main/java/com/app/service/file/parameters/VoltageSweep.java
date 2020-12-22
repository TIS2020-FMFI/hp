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
        if(start < minStart || start > maxStart){
            super.start = minStart;
        }else super.start = start;
    }


    public void setStop(Double stop) {
        if(stop < minStop || stop > maxStop){
            super.stop = minStop;
        }else super.stop = stop;
    }


    public void setStep(Double step) {
        if(step < minStep || step > maxStep){super.step = minStep;}
        else super.step = step;
    }


    public void setSpot(Double spot) {
        if(spot < minSpot || spot > maxSpot){super.spot = minSpot;}
        else super.spot = spot;
    }
}
