package com.app.service.measurement;

import com.app.service.file.parameters.MeasuredQuantity;
import com.app.service.utils.Utils;


public class SingleValue {
    private double displayA;
    private double displayB;
    private double displayX;

    /**
     * Constructor sets values.
     *
     * @param displayA
     * @param displayB
     * @param displayX
     */
    public SingleValue(double displayA, double displayB, double displayX) {
        this.displayA = displayA;
        this.displayB = displayB;
        this.displayX = displayX;
    }

    /**
     * Constructor extracts three values from a string.
     * Creates exception if the number of values are less than three.
     *
     * @param input
     * @throws NumberFormatException
     */
    public SingleValue(String input) throws NumberFormatException {
        String[] values = Utils.lineSplitAndExtractNumbers(input, ",");
        if (values.length != 3) {
            throw new ArrayStoreException("Parsed measurement line does not hold 3 values -> length: " + values.length +"; " + (input == null));
        }
        this.displayX = Double.parseDouble(values[0]);
        this.displayA = Double.parseDouble(values[1]);
        this.displayB = Double.parseDouble(values[2]);
    }

    /**
     * Constructor extracts two values from a string, sets to displayA and displayB,
     * creates a third value from parameters and sets to displayX.
     * Creates exception if the number of values is not equal to two.
     *
     * @param input
     * @param measurement
     * @throws NumberFormatException
     */
    public SingleValue(String input, Measurement measurement) throws NumberFormatException {
        String[] values = Utils.lineSplitAndExtractNumbers(input, ",");
        if (values.length != 2) {
            throw new ArrayStoreException("Parsed measurement line does not hold 2 values -> length: " + values.length);
        }
        if (measurement.getParameters().getDisplayYY().getX() == MeasuredQuantity.FREQUENCY) {
            if  (measurement.getData().size() == 0) {
                this.displayX = measurement.getParameters().getFrequencySweep().getStart();
            } else {
                this.displayX = Math.min(measurement.getData().get(measurement.getData().size()-1).displayX + measurement.getParameters().getFrequencySweep().getStep(), measurement.getParameters().getFrequencySweep().getStop());
            }

        } else {
            if  (measurement.getData().size() == 0) {
                this.displayX = measurement.getParameters().getVoltageSweep().getStart();
            } else {
                this.displayX = Math.min(measurement.getData().get(measurement.getData().size()-1).displayX + measurement.getParameters().getVoltageSweep().getStep(), measurement.getParameters().getVoltageSweep().getStop());
            }
        }
        this.displayA = Double.parseDouble(values[0]);
        this.displayB = Double.parseDouble(values[1]);
    }

    /**
     *
     * @param displayA
     */
    public void setDisplayA(double displayA) {
        this.displayA = displayA;
    }

    /**
     *
     * @param displayB
     */
    public void setDisplayB(double displayB) {
        this.displayB = displayB;
    }

    /**
     *
     * @param displayX
     */
    public void setDisplayX(double displayX) {
        this.displayX = displayX;
    }

    /**
     *
     * @return
     */
    public double getDisplayA() {
        return displayA;
    }

    /**
     *
     * @return
     */
    public double getDisplayB() {
        return displayB;
    }

    /**
     *
     * @return
     */
    public double getDisplayX() {
        return displayX;
    }

    /**
     * Creates a string with values in variables.
     *
     * @return
     */
    @Override
    public String toString() {
        return "SingleValue {A=" + displayA + ", B=" + displayB + ", X=" + displayX + '}';
    }
}
