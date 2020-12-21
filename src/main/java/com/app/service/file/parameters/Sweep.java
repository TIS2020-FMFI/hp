package com.app.service.file.parameters;

public abstract class Sweep {

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

    public void setStart(Double start) { this.start = start; }

    public void setStop(Double stop) {
        this.stop = stop;
    }

    public void setStep(Double step) {
        this.step = step;
    }

    public void setSpot(Double spot) {
        this.spot = spot;
    }
}
