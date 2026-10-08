package com.ecoscan.util;

import com.ecoscan.constant.WasteConstants;

public final class TextUtils {

    private TextUtils() {
    }

    public static String cleanOptional(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed.length() > WasteConstants.MAX_OPTIONAL_TEXT_LENGTH
                ? trimmed.substring(0, WasteConstants.MAX_OPTIONAL_TEXT_LENGTH)
                : trimmed;
    }
}
