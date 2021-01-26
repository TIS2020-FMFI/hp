package com.app.service.graph;

public enum GraphState {
    EMPTY,
    RUNNING,
    LOADED,
    DONE,
    SAVED;

    /**
     *
     * @param state
     * @return
     */
    public static boolean isStatic(GraphState state) {
        return !state.equals(RUNNING);
    }

    /**
     *
     * @param state
     * @return
     */
    public static boolean canBeLoaded(GraphState state) {
        return !state.equals(DONE);
    }
}
