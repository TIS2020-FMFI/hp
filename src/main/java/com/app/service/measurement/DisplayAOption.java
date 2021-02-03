package com.app.service.measurement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum DisplayAOption {
    L,
    C,
    Z,
    Y,
    r,
    rx,
    G,
    R;

    public static List<DisplayAOption> getAOptionsByB(DisplayBOption bOption) {
        switch (bOption) {
            case R:
            case G:
            case D:
            case Q:
                return new ArrayList<>(Arrays.asList(L, C));
            case deg:
            case rad:
                return new ArrayList<>(Arrays.asList(Z, Y, r));
            case ry:
                return new ArrayList<>(Collections.singletonList(rx));
            case B:
                return new ArrayList<>(Collections.singletonList(G));
            case Xs:
                return new ArrayList<>(Collections.singleton(R));
            default:
                return null;
        }
    }

    public static DisplayAOption getOptionFromString(String text) {
        if (text.contains("Γx")) {
            return rx;
        }
        if (text.contains("Γ")) {
            return r;
        }
        if (text.contains("Y")) {
            return Y;
        }
        if (text.contains("Z")) {
            return Z;
        }
        return DisplayAOption.valueOf(text);
    }

    public String toString()
    {
        if (this == rx) return "Γx";
        else if (this == r) return "Γ";
        else if (this == Y) return "|Y|";
        else if (this == Z) return "|Z|";
        else return super.toString();
    }

}
