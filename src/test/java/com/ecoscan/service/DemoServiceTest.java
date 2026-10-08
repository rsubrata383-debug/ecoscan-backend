package com.ecoscan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ecoscan.exception.ApiException;
import com.ecoscan.model.WasteResult;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class DemoServiceTest {

    private final DemoService demoService = new DemoService();

    @Test
    void hasNineItemsWithValidCategoriesAndBins() {
        assertEquals(9, demoService.getItems().size());
        Set<String> validCategories = Set.of("Plastic", "Organic", "E-Waste", "Paper", "Metal", "Glass", "Other");
        Set<String> validBins = Set.of("recyclable", "organic", "non-recyclable", "special");

        demoService.getItems().forEach(item -> {
            WasteResult result = demoService.getResult(item.id());
            assertTrue(validCategories.contains(result.category()));
            assertTrue(validBins.contains(result.bin()));
        });
    }

    @Test
    void unknownIdReturnsNotFound() {
        ApiException exception = assertThrows(ApiException.class, () -> demoService.getResult("unknown"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}
