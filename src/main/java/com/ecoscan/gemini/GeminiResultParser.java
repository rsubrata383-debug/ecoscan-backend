package com.ecoscan.gemini;

import static com.ecoscan.constant.GeminiConstants.*;
import static com.ecoscan.constant.WasteConstants.*;

import com.ecoscan.constant.ApiMessages;
import com.ecoscan.exception.ApiException;
import com.ecoscan.model.WasteResult;
import com.ecoscan.util.ListUtils;
import com.ecoscan.util.TextUtils;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class GeminiResultParser {

    private final ObjectMapper objectMapper;

    public GeminiResultParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public WasteResult parseResponse(JsonNode response) {
        JsonNode textNode = response == null ? null
                : response.path(CANDIDATES).path(0).path(CONTENT).path(PARTS).path(0).path(TEXT);
        if (textNode == null || !textNode.isString() || textNode.asString().isBlank()) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, ApiMessages.NO_SCAN_RESULT);
        }
        return parse(textNode.asString());
    }

    WasteResult parse(String json) {
        if (json == null || json.isBlank()) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, ApiMessages.INVALID_SCAN_RESULT);
        }
        try {
            WasteResult result = objectMapper.readValue(json, WasteResult.class);
            validateRequired(result);

            String itemName = result.itemName().trim();
            String category = result.category();
            String bin = result.bin();
            validateAllowedValues(category, bin);
            if (itemName.equalsIgnoreCase(UNKNOWN_ITEM_NAME)) {
                bin = NON_RECYCLABLE_BIN;
            } else if (category.equals(E_WASTE_CATEGORY)) {
                bin = SPECIAL_BIN;
            }

            return normalize(result, itemName, category, bin);
        } catch (JacksonException exception) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, ApiMessages.INVALID_SCAN_RESULT);
        }
    }

    private void validateRequired(WasteResult result) {
        if (result == null || result.itemName() == null || result.itemName().isBlank()
                || result.tip() == null || result.tip().isBlank()
                || result.category() == null || result.bin() == null) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, ApiMessages.INVALID_SCAN_RESULT);
        }
    }

    private void validateAllowedValues(String category, String bin) {
        if (!CATEGORIES.contains(category) || !BINS.contains(bin)) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, ApiMessages.INVALID_SCAN_RESULT);
        }
    }

    private WasteResult normalize(WasteResult result, String itemName, String category, String bin) {
        return new WasteResult(
                itemName, category, bin, result.tip().trim(),
                TextUtils.cleanOptional(result.material()),
                cleanRecyclability(result.recyclability()),
                cleanList(result.howToPrepare()),
                TextUtils.cleanOptional(result.decompositionTime()),
                TextUtils.cleanOptional(result.whyItMatters()),
                cleanList(result.prosOfRightDisposal()),
                cleanList(result.consOfWrongDisposal()),
                cleanList(result.afterRecyclingItBecomes()),
                cleanList(result.reuseIdeas()),
                TextUtils.cleanOptional(result.funFact()),
                TextUtils.cleanOptional(result.commonMistake()));
    }

    private String cleanRecyclability(String value) {
        String cleaned = TextUtils.cleanOptional(value);
        return RECYCLABILITY.contains(cleaned) ? cleaned : "";
    }

    private List<String> cleanList(List<String> values) {
        return ListUtils.cleanStrings(values, MAX_LIST_ITEMS);
    }
}
