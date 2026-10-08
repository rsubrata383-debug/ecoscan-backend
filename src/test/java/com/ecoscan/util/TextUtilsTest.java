package com.ecoscan.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TextUtilsTest {

    @Test
    void cleansNullEmptyAndNormalText() {
        assertEquals("", TextUtils.cleanOptional(null));
        assertEquals("", TextUtils.cleanOptional("  "));
        assertEquals("Hello", TextUtils.cleanOptional("  Hello  "));
    }

    @Test
    void cutsLongText() {
        String value = "a".repeat(250);
        assertEquals(200, TextUtils.cleanOptional(value).length());
    }
}
