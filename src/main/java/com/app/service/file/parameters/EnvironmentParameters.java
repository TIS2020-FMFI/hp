package com.app.service.file.parameters;

public class EnvironmentParameters {

    private DisplayYY displayYY;
    private FrequencySweep frequencySweep;
    private VoltageSweep voltageSweep;
    private Other other;
    private String comment;


    public FrequencySweep getFrequencySweep() {
        return frequencySweep;
    }

    public void setFrequencySweep(FrequencySweep frequencySweep) {
        this.frequencySweep = frequencySweep;
    }

    public VoltageSweep getVoltageSweep() {
        return voltageSweep;
    }

    public void setVoltageSweep(VoltageSweep voltageSweep) {
        this.voltageSweep = voltageSweep;
    }

    public Other getOther() {
        return other;
    }

    public void setOther(Other other) {
        this.other = other;
    }

    public DisplayYY getDisplayYY() {
        return displayYY;
    }

    public void setDisplayYY(DisplayYY displayYY) {
        this.displayYY = displayYY;
    }

    public void checkAll(){

        if(displayYY == null){
            displayYY = new DisplayYY();
        }
        if(displayYY.getA() == null) displayYY.setA("L");
        if(displayYY.getB() == null) displayYY.setB("R");
        if(displayYY.getX() == null) displayYY.setX(MeasuredQuantity.FREQUENCY);

        if (frequencySweep == null){
            frequencySweep = new FrequencySweep();
        }
        frequencySweep.check();

        if (voltageSweep == null){
            voltageSweep = new VoltageSweep();
        }
        voltageSweep.check();

        if(other == null){
            other = new Other();
        }
        other.check();

    }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }
}
