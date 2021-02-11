package com.app.service.measurement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Values for display B.
 *
 */
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

    /**
     * Selects possible values for display B by display A value.
     *
     * @param aOption   Display A value.
     * @return
     */
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

    /**
     * Returns value by string.
     *
     * @param text
     * @return
     */
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
        if (text.contains("Γy")) {
            return ry;
        }
        return DisplayBOption.valueOf(text);
    }

    /**
     * Creates string by value.
     *
     * @return
     */
    public String toString()
    {
        if (this == deg) return "Θ(deg)";
        else if (this == rad) return "Θ(rad)";
        else if (this == Xs) return "Xs (Zplots)";
        else if (this == ry) return "Γy";
        else return super.toString();
    }
}
