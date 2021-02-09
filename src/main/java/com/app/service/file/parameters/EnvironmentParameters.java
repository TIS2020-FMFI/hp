package com.app.service.file.parameters;

import com.app.service.graph.GraphType;

public class EnvironmentParameters {
    private GraphType active;
    private Parameters upperGraphParameters;
    private Parameters lowerGraphParameters;

    /**
     * Determines which graph is active and returns its parameters.
     *
     * @return
     */
    public Parameters getActive() {
        return active.equals(GraphType.UPPER) ? upperGraphParameters:lowerGraphParameters;
    }

    /**
     * Returns graph parameters by type.
     *
     * @param type
     * @return
     */
    public Parameters getByType(GraphType type) {
        return type.equals(GraphType.UPPER) ? upperGraphParameters:lowerGraphParameters;
    }

    /**
     *
     * @return
     */
    public GraphType getActiveGraphType() {
        return active;
    }

    /**
     *
     * @param type
     */
    public void setActiveGraphType(GraphType type) { active = type; }

    /**
     *
     * @param lowerGraphParameters
     */
    public void setLowerGraphParameters(Parameters lowerGraphParameters) {
        this.lowerGraphParameters = lowerGraphParameters;
    }

    /**
     *
     * @param upperGraphParameters
     */
    public void setUpperGraphParameters(Parameters upperGraphParameters) {
        this.upperGraphParameters = upperGraphParameters;
    }

    /**
     *
     * @param active
     */
    public void setActive(GraphType active) {
        this.active = active;
    }
}
