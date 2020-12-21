package com.app.service.file.parameters;

public class VoltageSweep extends Sweep {
    double minStart = -40;
    double maxStart = 40;
    double minStop = -40;
    double maxStop = 40;
    double minStep = 0.01;
    double maxStep = 40;
    double minSpot = -40;
    double maxSpot = 40;

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
