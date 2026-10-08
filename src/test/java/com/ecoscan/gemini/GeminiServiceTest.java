package com.ecoscan.gemini;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ecoscan.exception.ApiException;
import com.ecoscan.model.WasteResult;
import com.ecoscan.service.GeminiService;
import com.ecoscan.support.TestImages;
import com.ecoscan.validation.ImageValidator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

class GeminiServiceTest {

    private static final String REQUIRED_FIELDS = """
            {
              "itemName": "Plastic Bottle",
              "category": "Plastic",
              "bin": "recyclable",
              "tip": " Rinse it before recycling. "
            }
            """;
    private static final String FULL_RESULT = """
            {
              "itemName": "Plastic Bottle",
              "category": "Plastic",
              "bin": "recyclable",
              "tip": "Rinse it and recycle it.",
              "material": " PET 1 Plastic ",
              "recyclability": "easy",
              "howToPrepare": ["Empty it", "Crush it"],
              "decompositionTime": "Up to 450 years",
              "whyItMatters": "Plastic can enter waterways.",
              "prosOfRightDisposal": ["Saves material", "Reduces waste"],
              "consOfWrongDisposal": ["Can pollute land", "Can spoil paper"],
              "afterRecyclingItBecomes": ["New bottles", "Fabric"],
              "reuseIdeas": ["Planter", "Craft holder"],
              "funFact": "Plastic bottles can be recycled.",
              "commonMistake": "Leaving liquid inside can spoil paper."
            }
            """;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeminiService service = new GeminiService(
            new ImageValidator(),
            new GeminiClient(RestClient.create(), new GeminiRequestBuilder(), "", "gemini-3.5-flash"),
            new GeminiResultParser(objectMapper));
    private final GeminiResultParser parser = new GeminiResultParser(objectMapper);

    @Test
    void emptyKeyReturnsServiceUnavailable() {
        MockMultipartFile image = new MockMultipartFile("image", "photo.png", "image/png", TestImages.PNG);

        ApiException exception = assertThrows(ApiException.class, () -> service.scan(image));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
        assertEquals("AI mode is off. Use demo mode.", exception.getMessage());
    }

    @Test
    void parsesFullResult() {
        WasteResult result = parser.parse(FULL_RESULT);

        assertEquals("Plastic Bottle", result.itemName());
        assertEquals("PET 1 Plastic", result.material());
        assertEquals("easy", result.recyclability());
        assertEquals(List.of("Empty it", "Crush it"), result.howToPrepare());
        assertEquals("Rinse it and recycle it.", result.tip());
    }

    @Test
    void missingOptionalFieldsBecomeEmpty() {
        WasteResult result = parser.parse(REQUIRED_FIELDS);

        assertEquals("Rinse it before recycling.", result.tip());
        assertEquals("", result.material());
        assertEquals("", result.recyclability());
        assertTrue(result.howToPrepare().isEmpty());
        assertEquals("", result.decompositionTime());
        assertEquals("", result.whyItMatters());
        assertTrue(result.prosOfRightDisposal().isEmpty());
        assertTrue(result.consOfWrongDisposal().isEmpty());
        assertTrue(result.afterRecyclingItBecomes().isEmpty());
        assertTrue(result.reuseIdeas().isEmpty());
        assertEquals("", result.funFact());
        assertEquals("", result.commonMistake());
    }

    @Test
    void invalidBinReturnsBadGateway() {
        assertBadGateway(REQUIRED_FIELDS.replace("\"recyclable\"", "\"trash\""));
    }

    @Test
    void invalidCategoryReturnsBadGateway() {
        assertBadGateway(REQUIRED_FIELDS.replace("\"Plastic\"", "\"Textile\""));
    }

    @Test
    void emptyOrBrokenJsonReturnsBadGateway() {
        assertBadGateway("");
        assertBadGateway(null);
        assertBadGateway("{broken");
    }

    @Test
    void longTipIsAcceptedAndTrimmed() {
        String tip = "one two three four five six seven eight nine ten eleven twelve thirteen fourteen fifteen sixteen seventeen eighteen nineteen twenty one";
        String json = REQUIRED_FIELDS.replace(" Rinse it before recycling. ", " " + tip + " ");

        assertEquals(tip, parser.parse(json).tip());
    }

    @Test
    void listsAreLimitedToThreeItems() {
        String json = FULL_RESULT.replace(
                "[\"Empty it\", \"Crush it\"]",
                "[\"One\", \"Two\", \"Three\", \"Four\"]");

        assertEquals(List.of("One", "Two", "Three"), parser.parse(json).howToPrepare());
    }

    @Test
    void unknownItemUsesNonRecyclableBin() {
        WasteResult result = parser.parse(REQUIRED_FIELDS.replace("\"Plastic Bottle\"", "\"Unknown\""));

        assertEquals("non-recyclable", result.bin());
    }

    @Test
    void eWasteUsesSpecialBin() {
        String json = REQUIRED_FIELDS
                .replace("\"Plastic\"", "\"E-Waste\"")
                .replace("\"recyclable\"", "\"organic\"");

        assertEquals("special", parser.parse(json).bin());
    }

    private void assertBadGateway(String json) {
        ApiException exception = assertThrows(ApiException.class, () -> parser.parse(json));
        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatus());
    }
}
