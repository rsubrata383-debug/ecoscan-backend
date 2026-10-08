package com.ecoscan.gemini;

import static com.ecoscan.constant.GeminiConstants.*;
import static com.ecoscan.constant.WasteConstants.*;

import com.ecoscan.constant.ApiMessages;
import com.ecoscan.exception.ApiException;
import com.ecoscan.model.MultiWasteResult;
import com.ecoscan.model.WasteResult;
import com.ecoscan.util.ListUtils;
import com.ecoscan.util.TextUtils;
import java.util.ArrayList;
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

    public MultiWasteResult parseResponse(JsonNode response) {
        JsonNode textNode = response == null ? null
                : response.path(CANDIDATES).path(0).path(CONTENT).path(PARTS).path(0).path(TEXT);
        if (textNode == null || !textNode.isString() || textNode.asString().isBlank()) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, ApiMessages.NO_SCAN_RESULT);
        }
        return parse(textNode.asString());
    }

    MultiWasteResult parse(String json) {
        if (json == null || json.isBlank()) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, ApiMessages.INVALID_SCAN_RESULT);
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            List<WasteResult> items = new ArrayList<>();

            if (root.has(ITEMS) && root.get(ITEMS).isArray()) {
                for (JsonNode itemNode : root.get(ITEMS)) {
                    WasteResult item = objectMapper.treeToValue(itemNode, WasteResult.class);
                    items.add(processSingleItem(item));
                }
            } else if (root.has(ITEM_NAME)) {
                WasteResult item = objectMapper.treeToValue(root, WasteResult.class);
                items.add(processSingleItem(item));
            }

            if (items.isEmpty()) {
                throw new ApiException(HttpStatus.BAD_GATEWAY, ApiMessages.INVALID_SCAN_RESULT);
            }

            return MultiWasteResult.of(items);
        } catch (JacksonException exception) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, ApiMessages.INVALID_SCAN_RESULT);
        }
    }

    private WasteResult processSingleItem(WasteResult result) {
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
