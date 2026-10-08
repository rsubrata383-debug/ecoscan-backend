package com.ecoscan.gemini;

import static com.ecoscan.constant.GeminiConstants.*;
import static com.ecoscan.constant.WasteConstants.BINS;
import static com.ecoscan.constant.WasteConstants.CATEGORIES;
import static com.ecoscan.constant.WasteConstants.RECYCLABILITY;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class GeminiRequestBuilder {

    public Map<String, Object> build(String encodedImage, String mimeType) {
        Map<String, Object> itemProperties = Map.ofEntries(
                Map.entry(ITEM_NAME, stringSchema()),
                Map.entry(CATEGORY, enumSchema(CATEGORIES)),
                Map.entry(BIN, enumSchema(BINS)),
                Map.entry(TIP, stringSchema()),
                Map.entry(MATERIAL, stringSchema()),
                Map.entry(RECYCLABILITY_FIELD, enumSchema(RECYCLABILITY)),
                Map.entry(HOW_TO_PREPARE, stringArraySchema()),
                Map.entry(DECOMPOSITION_TIME, stringSchema()),
                Map.entry(WHY_IT_MATTERS, stringSchema()),
                Map.entry(PROS_OF_RIGHT_DISPOSAL, stringArraySchema()),
                Map.entry(CONS_OF_WRONG_DISPOSAL, stringArraySchema()),
                Map.entry(AFTER_RECYCLING_IT_BECOMES, stringArraySchema()),
                Map.entry(REUSE_IDEAS, stringArraySchema()),
                Map.entry(FUN_FACT, stringSchema()),
                Map.entry(COMMON_MISTAKE, stringSchema()));

        Map<String, Object> itemSchema = Map.of(
                TYPE, OBJECT_TYPE,
                PROPERTIES, itemProperties,
                REQUIRED, List.of(
                        ITEM_NAME, CATEGORY, BIN, TIP, MATERIAL, RECYCLABILITY_FIELD, HOW_TO_PREPARE,
                        DECOMPOSITION_TIME, WHY_IT_MATTERS, PROS_OF_RIGHT_DISPOSAL, CONS_OF_WRONG_DISPOSAL,
                        AFTER_RECYCLING_IT_BECOMES, REUSE_IDEAS, FUN_FACT, COMMON_MISTAKE));

        Map<String, Object> rootSchema = Map.of(
                TYPE, OBJECT_TYPE,
                PROPERTIES, Map.of(
                        ITEMS, Map.of(
                                TYPE, ARRAY_TYPE,
                                ITEMS, itemSchema)),
                REQUIRED, List.of(ITEMS));

        Map<String, Object> generationConfig = Map.of(
                RESPONSE_MIME_TYPE, APPLICATION_JSON,
                RESPONSE_SCHEMA, rootSchema);

        return Map.of(
                CONTENTS, List.of(Map.of(
                        PARTS, List.of(
                                Map.of(TEXT, PROMPT),
                                Map.of(INLINE_DATA, Map.of(MIME_TYPE, mimeType, DATA, encodedImage))))),
                GENERATION_CONFIG, generationConfig);
    }

    private Map<String, Object> stringSchema() {
        return Map.of(TYPE, STRING_TYPE);
    }

    private Map<String, Object> enumSchema(List<String> values) {
        return Map.of(TYPE, STRING_TYPE, ENUM, values);
    }

    private Map<String, Object> stringArraySchema() {
        return Map.of(TYPE, ARRAY_TYPE, ITEMS, stringSchema());
    }
}
