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

    private double start;
    private double stop;
    private double step;
    private double spot;



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

    public void setStart(Double start) {
        if(start < minStart || start > maxStart){
            this.start = minStart;
        }else this.start = start;
    }


    public void setStop(Double stop) {
        if(stop < minStop || stop > maxStop){
            this.stop = minStop;
        }else this.stop = stop;
    }


    public void setStep(Double step) {
        if(step < minStep || step > maxStep){this.step = minStep;}
        else this.step = step;
    }


    public void setSpot(Double spot) {
        if(spot < minSpot || spot > maxSpot){this.spot = minSpot;}
        else this.spot = spot;
    }
}
