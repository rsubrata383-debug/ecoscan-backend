package com.ecoscan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ecoscan.exception.ApiException;
import com.ecoscan.model.WasteResult;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class DemoServiceTest {

    private final DemoService demoService = new DemoService();
    private final Set<String> validCategories = Set.of("Plastic", "Organic", "E-Waste", "Paper", "Metal", "Glass",
            "Other");
    private final Set<String> validBins = Set.of("recyclable", "organic", "non-recyclable", "special");

    @Test
    void hasNineItemsAndEveryResultIsComplete() {
        assertEquals(9, demoService.getItems().size());
        Set<String> expectedIds = Set.of(
                "plastic-bottle", "aluminum-can", "banana-peel", "paper", "glass-bottle",
                "food-waste", "plastic-bag", "battery", "cardboard");
        assertEquals(expectedIds, demoService.getItems().stream()
                .map(item -> item.id())
                .collect(Collectors.toSet()));

        demoService.getItems().forEach(item -> {
            WasteResult result = demoService.getResult(item.id());
            assertTrue(validCategories.contains(result.category()));
            assertTrue(validBins.contains(result.bin()));
            assertFalse(result.itemName().isBlank());
            assertFalse(result.tip().isBlank());
            assertFalse(result.material().isBlank());
            assertTrue(Set.of("easy", "medium", "hard").contains(result.recyclability()));
            assertFalse(result.decompositionTime().isBlank());
            assertFalse(result.whyItMatters().isBlank());
            assertFalse(result.funFact().isBlank());
            assertFalse(result.commonMistake().isBlank());
            assertList(result.howToPrepare());
            assertList(result.prosOfRightDisposal());
            assertList(result.consOfWrongDisposal());
            assertList(result.afterRecyclingItBecomes());
            assertList(result.reuseIdeas());
        });
    }

    @Test
    void keepsSpecialAndNonRecyclableBins() {
        assertEquals("special", demoService.getResult("battery").bin());
        assertEquals("E-Waste", demoService.getResult("battery").category());
        assertEquals("non-recyclable", demoService.getResult("plastic-bag").bin());
    }

    @Test
    void unknownIdReturnsNotFound() {
        ApiException exception = assertThrows(ApiException.class, () -> demoService.getResult("unknown"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    private void assertList(List<String> values) {
        assertTrue(values.size() >= 2 && values.size() <= 3);
        values.forEach(value -> {
            assertFalse(value.isBlank());
            assertTrue(value.trim().split("\\s+").length <= 12);
        });
    }
}
