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
