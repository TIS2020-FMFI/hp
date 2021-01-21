package com.app.service.file.parameters;

import com.app.service.graph.Graph;
import com.app.service.graph.GraphType;

public class EnvironmentParameters {
    private GraphType active;
    private Parameters upperGraphParameters;
    private Parameters lowerGraphParameters;


    public Parameters getActive() {
        return active.equals(GraphType.UPPER) ? upperGraphParameters:lowerGraphParameters;
    }

    public Parameters getByType(GraphType type) {
        return type.equals(GraphType.UPPER) ? upperGraphParameters:lowerGraphParameters;
    }

    public GraphType getActiveGraphType() {
        return active;
    }
    public void setActiveGraphType(GraphType type) { active = type; }

    public void setLowerGraphParameters(Parameters lowerGraphParameters) {
        this.lowerGraphParameters = lowerGraphParameters;
    }

    public void setUpperGraphParameters(Parameters upperGraphParameters) {
        this.upperGraphParameters = upperGraphParameters;
    }

    public void setActive(GraphType active) {
        this.active = active;
    }
}
