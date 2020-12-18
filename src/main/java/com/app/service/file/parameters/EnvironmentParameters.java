package com.app.service.file.parameters;

public class EnvironmentParameters {

    private DisplayYY displayYY;
    private FrequencySweep frequencySweep;
    private VoltageSweep voltageSweep;
    private Other other;


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
}
