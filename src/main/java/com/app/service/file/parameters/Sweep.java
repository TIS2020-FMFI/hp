package com.app.service.file.parameters;

public abstract class Sweep {

    private double start;
    private double stop;
    private double step;
    private double spot;

    double minStart, maxStart;
    double minStop, maxStop;
    double minStep, maxStep;
    double minSpot, maxSpot;

    public double getStart() {
        return start;
    }

    public void setStart(Double start) {
        this.start = start != null ? start:this.minStart;
    }

    public double getStop() {
        return stop;
    }

    public void setStop(Double stop) {
        this.stop = stop != null ? stop:this.maxStop;
    }

    public double getStep() {
        return step;
    }

    public void setStep(Double step) {
        this.step = step != null ? step:this.minStep;
    }

    public double getSpot() {
        return spot;
    }

    public void setSpot(Double spot) {
        this.spot = spot != null ? spot:this.minSpot;
    }
}
