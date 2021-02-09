package com.app.service.file.parameters;

public class Other {
    private static final double minElectricalLength = 0;
    private static final double maxElectricalLength = 99.99;
    private static final double minCapacitance = -1;
    private static final double maxCapacitance = 1;
    private double electricalLength;
    private double capacitance;
    private SweepType sweepType;
    private boolean highSpeed;
    private boolean autoSweep;

    /**
     *
     * @return
     */
    public double getElectricalLength() {
        return electricalLength;
    }

    /**
     *
     * @param electricalLength
     */
    public void setElectricalLength(Double electricalLength) {
        if(electricalLength < minElectricalLength) this.electricalLength = minElectricalLength;
        else if(electricalLength > maxElectricalLength) this.electricalLength = maxElectricalLength;
        else this.electricalLength = electricalLength;
    }

    /**
     *
     * @return
     */
    public double getCapacitance() {
        return capacitance;
    }

    /**
     *
     * @param capacitance
     */
    public void setCapacitance(Double capacitance) {
        if(capacitance < minCapacitance )this.capacitance = minCapacitance;
        else if(capacitance > maxCapacitance) this.capacitance = maxCapacitance;
        else { this.capacitance = capacitance; }
    }

    /**
     *
     * @return
     */
    public SweepType getSweepType() {
        return sweepType;
    }

    /**
     *
     * @param sweepType
     */
    public void setSweepType(SweepType sweepType) {
        this.sweepType = sweepType;
    }

    /**
     *
     * @return
     */
    public boolean isHighSpeed() {
        return highSpeed;
    }

    /**
     *
     * @param highSpeed
     */
    public void setHighSpeed(boolean highSpeed) {
        this.highSpeed = highSpeed;
    }

    /**
     *
     * @return
     */
    public boolean isAutoSweep() {
        return autoSweep;
    }

    /**
     *
     * @param autoSweep
     */
    public void setAutoSweep(boolean autoSweep) {
        this.autoSweep = autoSweep;
    }

    /**
     *
     * @return
     */
    public double getMinElectricalLength() {
        return minElectricalLength;
    }

    /**
     *
     * @return
     */
    public double getMinCapacitance() {
        return minCapacitance;
    }

    /**
     * Checks the validity of values.
     * In case of invalid value, sets default value.
     */
    public void check(){
        setElectricalLength(electricalLength);
        setCapacitance(capacitance);
        if(sweepType == null) setSweepType(SweepType.LOG);
    }
}