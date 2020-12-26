package com.app.service.measurement;

public class SingleValue {
    double displayA;
    double displayB;
    double displayX;

    public SingleValue(double displayA, double displayB, double displayX) {
        this.displayA = displayA;
        this.displayB = displayB;
        this.displayX = displayX;
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
}
