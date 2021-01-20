package com.app.service.measurement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum DisplayBOption {
    R,
    G,
    D,
    Q,
    rad,
    deg,
    ry,
    B,
    Xs;

    public static List<DisplayBOption> getBOptionsByA(DisplayAOption aOption) {
        switch (aOption) {
            case L:
            case C:
                return new ArrayList<>(Arrays.asList(R, G, D, Q));
            case Z:
            case Y:
            case r:
                return new ArrayList<>(Arrays.asList(rad, deg));
            case rx:
                return new ArrayList<>(Collections.singleton(ry));
            case G:
                return new ArrayList<>(Collections.singletonList(B));
            case R:
                return new ArrayList<>(Collections.singletonList(Xs));
            default:
                return null;
        }
    }

    public static DisplayBOption getOptionFromString(String text) {
        if (text.contains("deg")) {
            return deg;
        }
        if (text.contains("rad")) {
            return rad;
        }
        if (text.contains("Xs")) {
            return Xs;
        }
        return DisplayBOption.valueOf(text);
    }
}
