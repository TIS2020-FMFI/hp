package com.app.service.file.parameters;

public class Other {
    private double electricalLength;
    private double capacitance;
    private static final double minElectricalLength = 0;
    private static final double maxElectricalLength = 99.99;
    private static final double minCapacitance = -1;
    private static final double maxCapacitance = 1;
    private SweepType sweepType;
    private boolean highSpeed;
    private boolean autoSweep;

    public double getElectricalLength() {
        return electricalLength;
    }

    public void setElectricalLength(Double electricalLength) {
        if(electricalLength < minElectricalLength) this.electricalLength = minElectricalLength;
        else if(electricalLength > maxElectricalLength) this.electricalLength = maxElectricalLength;
        else this.electricalLength = electricalLength;
    }

    public double getCapacitance() {
        return capacitance;
    }

    public void setCapacitance(Double capacitance) {
        if(capacitance < minCapacitance )this.capacitance = minCapacitance;
        else if(capacitance > maxCapacitance) this.capacitance = maxCapacitance;
        else { this.capacitance = capacitance; }
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

    public double getMinElectricalLength() {
        return minElectricalLength;
    }

    public double getMinCapacitance() {
        return minCapacitance;
    }
}