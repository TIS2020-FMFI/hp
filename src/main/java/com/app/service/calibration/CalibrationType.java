package com.app.service.calibration;

import javax.naming.directory.NoSuchAttributeException;

public enum CalibrationType {
    LOAD,
    OPEN,
    SHORT;


    public static CalibrationType getTypeFromString(String type) throws NoSuchAttributeException {
        switch (type) {
            case "short":
            case "SHORT":
                return CalibrationType.SHORT;
            case "load":
            case "LOAD":
                return CalibrationType.LOAD;
            case "open":
            case "OPEN":
                return CalibrationType.OPEN;
            default:
                throw new NoSuchAttributeException("Calibration type not recognized!");
        }
    }
}
