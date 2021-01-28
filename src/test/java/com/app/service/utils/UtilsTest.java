package com.app.service.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class UtilsTest {

    Utils tested = new Utils();

    @Test
    void lineSplitAndExtractNumbers() {
        String[] temp = Utils.lineSplitAndExtractNumbers(" V 000039.00,NLN-000097E-12,NRN 049.66E+00", ",");
        Double a = Double.parseDouble(temp[0]);
        System.out.println(a);
    }
}