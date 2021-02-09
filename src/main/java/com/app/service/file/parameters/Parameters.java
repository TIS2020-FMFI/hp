package com.app.service.file.parameters;

public class Parameters {
    private DisplayYY displayYY;
    private FrequencySweep frequencySweep;
    private VoltageSweep voltageSweep;
    private Other other;
    private String comment;

    /**
     *
     * @return
     */
    public FrequencySweep getFrequencySweep() {
        return frequencySweep;
    }

    /**
     *
     * @param frequencySweep
     */
    public void setFrequencySweep(FrequencySweep frequencySweep) {
        this.frequencySweep = frequencySweep;
    }

    /**
     *
     * @return
     */
    public VoltageSweep getVoltageSweep() {
        return voltageSweep;
    }

    /**
     *
     * @param voltageSweep
     */
    public void setVoltageSweep(VoltageSweep voltageSweep) {
        this.voltageSweep = voltageSweep;
    }

    /**
     *
     * @return
     */
    public Other getOther() {
        return other;
    }

    /**
     *
     * @param other
     */
    public void setOther(Other other) {
        this.other = other;
    }

    /**
     *
     * @return
     */
    public DisplayYY getDisplayYY() {
        return displayYY;
    }

    /**
     *
     * @param displayYY
     */
    public void setDisplayYY(DisplayYY displayYY) {
        this.displayYY = displayYY;
    }

    /**
     * Checks the validity of values.
     * In case of invalid value, sets default value.
     */
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

    /**
     *
     * @return
     */
    public String getComment() { return comment; }

    /**
     *
     * @param comment
     */
    public void setComment(String comment) { this.comment = comment; }
}
