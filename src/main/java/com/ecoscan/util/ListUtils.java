package com.ecoscan.util;

import java.util.ArrayList;
import java.util.List;

public final class ListUtils {

    private ListUtils() {
    }

    public static List<String> cleanStrings(List<String> values, int maxItems) {
        if (values == null || maxItems <= 0) {
            return List.of();
        }
        List<String> cleaned = new ArrayList<>();
        for (String value : values) {
            String item = TextUtils.cleanOptional(value);
            if (!item.isBlank()) {
                cleaned.add(item);
                if (cleaned.size() == maxItems) {
                    break;
                }
            }
        }
        return List.copyOf(cleaned);
    }
}
