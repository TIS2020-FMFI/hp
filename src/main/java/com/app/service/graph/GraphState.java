package com.app.service.graph;

public enum GraphState {
    EMPTY,
    RUNNING,
    LOADED;

    /**
     *
     * @param state
     * @return
     */
    public static boolean isStatic(GraphState state) {
        return !state.equals(RUNNING);
    }
}
