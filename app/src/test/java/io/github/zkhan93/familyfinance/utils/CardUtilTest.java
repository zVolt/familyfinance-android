package io.github.zkhan93.familyfinance.utils;

import org.junit.Test;

import io.github.zkhan93.familyfinance.util.CardUtil;

import static org.junit.Assert.*;
public class CardUtilTest {
    @Test
    public void formatOtherCardNumbers(){
        String number = "1234567891234567";
        String actual = CardUtil.formatCardNumber(number, ' ', false);
        assertEquals(actual, "1234 5678 9123 4567");

        actual = CardUtil.formatCardNumber(number, '-', false);
        assertEquals(actual, "1234-5678-9123-4567");

        actual = CardUtil.formatCardNumber(number, ' ', true);
        assertEquals(actual, "**** **** **** 4567");

        actual = CardUtil.formatCardNumber(number, '-', true);
        assertEquals(actual, "****-****-****-4567");
    }

    @Test
    public void formatAmexCardNumbers(){

        String number ="123456789123456";
        String actual = CardUtil.formatCardNumber(number, ' ', false);
        assertEquals(actual, "1234 567891 23456");

        actual = CardUtil.formatCardNumber(number, '-', false);
        assertEquals(actual, "1234-567891-23456");

        actual = CardUtil.formatCardNumber(number, ' ', true);
        assertEquals(actual, "**** ****** 23456");

        actual = CardUtil.formatCardNumber(number, '-', true);
        assertEquals(actual, "****-******-23456");
    }
}
