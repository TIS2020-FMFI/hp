package com.app.service.graph;

public enum GraphState {
    EMPTY,
    RUNNING,
    LOADED,
    DONE,
    SAVED;

    public static boolean isStatic(GraphState state) {
        return !state.equals(RUNNING);
    }

    public static boolean canBeLoaded(GraphState state) {
        return !state.equals(DONE);
    }
}
