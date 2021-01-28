package com.app.service.measurement;

import com.app.service.file.parameters.MeasuredQuantity;
import com.app.service.utils.Utils;


public class SingleValue {
    private double displayA;
    private double displayB;
    private double displayX;

    public SingleValue(double displayA, double displayB, double displayX) {
        this.displayA = displayA;
        this.displayB = displayB;
        this.displayX = displayX;
    }

    public SingleValue(String input) throws NumberFormatException {
        String[] values = Utils.lineSplitAndExtractNumbers(input, ",");
        if (values.length != 3) {
            throw new ArrayStoreException("Parsed measurement line does not hold 3 values -> length: " + values.length);
        }
        this.displayX = Double.parseDouble(values[0]);
        this.displayA = Double.parseDouble(values[1]);
        this.displayB = Double.parseDouble(values[2]);
    }

    public SingleValue(String input, Measurement measurement) throws NumberFormatException {
        String[] values = Utils.lineSplitAndExtractNumbers(input, ",");
        if (values.length != 2) {
            throw new ArrayStoreException("Parsed measurement line does not hold 2 values -> length: " + values.length);
        }
        if (measurement.getParameters().getDisplayYY().getX() == MeasuredQuantity.FREQUENCY) {
            if  (measurement.getData().size() == 0) {
                this.displayX = measurement.getParameters().getFrequencySweep().getStart();
            } else {
                this.displayX = measurement.getData().get(measurement.getData().size()-1).displayX + measurement.getParameters().getFrequencySweep().getStep();
            }

        } else {
            if  (measurement.getData().size() == 0) {
                this.displayX = measurement.getParameters().getVoltageSweep().getStart();
            } else {
                this.displayX = measurement.getData().get(measurement.getData().size()-1).displayX + measurement.getParameters().getVoltageSweep().getStep();
            }
        }
        this.displayA = Double.parseDouble(values[0]);
        this.displayB = Double.parseDouble(values[1]);
    }




    public void setDisplayA(double displayA) {
        this.displayA = displayA;
    }

    public void setDisplayB(double displayB) {
        this.displayB = displayB;
    }

    public void setDisplayX(double displayX) {
        this.displayX = displayX;
    }

    public double getDisplayA() {
        return displayA;
    }

    public double getDisplayB() {
        return displayB;
    }

    public double getDisplayX() {
        return displayX;
    }

    @Override
    public String toString() {
        return "SingleValue {A=" + displayA + ", B=" + displayB + ", X=" + displayX + '}';
    }
}
