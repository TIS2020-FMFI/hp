package com.app.service.file.parameters;

public class Other {
    private String electricalLength;
    private String capacitance;
    private SweepType sweepType;
    private boolean highSpeed;
    private boolean autoSweep;

    public String getElectricalLength() {
        return electricalLength;
    }

    public void setElectricalLength(String electricalLength) {
        this.electricalLength = electricalLength;
    }

    public String getCapacitance() {
        return capacitance;
    }

    public void setCapacitance(String capacitance) {
        this.capacitance = capacitance;
    }

    public SweepType getSweepType() {
        return sweepType;
    }

    public void setSweepType(SweepType sweepType) {
        this.sweepType = sweepType;
    }

    public boolean isHighSpeed() {
        return highSpeed;
    }

    public void setHighSpeed(boolean highSpeed) {
        this.highSpeed = highSpeed;
    }

    public boolean isAutoSweep() {
        return autoSweep;
    }

    public void setAutoSweep(boolean autoSweep) {
        this.autoSweep = autoSweep;
    }
}
