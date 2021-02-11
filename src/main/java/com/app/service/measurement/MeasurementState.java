package com.app.service.measurement;

/**
 * Measurements states.
 */

public enum MeasurementState {
    /**
     * State of waiting for the introduction of parameters and start of measurement.
     */
    WAITING,

    /**
     * State of started measurement.
     */
    STARTED,

    /**
     * State of successfully completed measurement.
     */
    FINISHED,

    /**
     * State of aborted measurement.
     */
    ABORTED,

    /**
     * State of saved measurement.
     */
    SAVED,

    /**
     * State of loaded measurement.
     */
    LOADED
}
