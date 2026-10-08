package com.ecoscan.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class ListUtilsTest {

    @Test
    void handlesNullAndEmptyLists() {
        assertEquals(List.of(), ListUtils.cleanStrings(null, 3));
        assertEquals(List.of(), ListUtils.cleanStrings(List.of(), 3));
    }

    @Test
    void trimsRemovesBlankAndLimitsItems() {
        assertEquals(
                List.of("One", "Two", "Three"),
                ListUtils.cleanStrings(List.of(" One ", "", "Two", "Three", "Four"), 3));
    }

    @Test
    void cutsLongListItems() {
        assertEquals(200, ListUtils.cleanStrings(List.of("x".repeat(220)), 3).getFirst().length());
    }
}
