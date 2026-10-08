package com.ecoscan.constant;

import java.util.List;

public final class WasteConstants {

    public static final String CATEGORY_PLASTIC = "Plastic";
    public static final String CATEGORY_ORGANIC = "Organic";
    public static final String CATEGORY_E_WASTE = "E-Waste";
    public static final String CATEGORY_PAPER = "Paper";
    public static final String CATEGORY_METAL = "Metal";
    public static final String CATEGORY_GLASS = "Glass";
    public static final String CATEGORY_OTHER = "Other";
    public static final String BIN_RECYCLABLE = "recyclable";
    public static final String BIN_ORGANIC = "organic";
    public static final String BIN_NON_RECYCLABLE = "non-recyclable";
    public static final String BIN_SPECIAL = "special";
    public static final String RECYCLABILITY_EASY = "easy";
    public static final String RECYCLABILITY_MEDIUM = "medium";
    public static final String RECYCLABILITY_HARD = "hard";
    public static final String MIME_JPEG = "image/jpeg";
    public static final String MIME_PNG = "image/png";
    public static final String MIME_WEBP = "image/webp";
    public static final List<String> CATEGORIES = List.of(
            CATEGORY_PLASTIC, CATEGORY_ORGANIC, CATEGORY_E_WASTE, CATEGORY_PAPER,
            CATEGORY_METAL, CATEGORY_GLASS, CATEGORY_OTHER);
    public static final List<String> BINS = List.of(BIN_RECYCLABLE, BIN_ORGANIC, BIN_NON_RECYCLABLE, BIN_SPECIAL);
    public static final List<String> RECYCLABILITY = List.of(
            RECYCLABILITY_EASY, RECYCLABILITY_MEDIUM, RECYCLABILITY_HARD);
    public static final List<String> IMAGE_MIME_TYPES = List.of(MIME_JPEG, MIME_PNG, MIME_WEBP);
    public static final String UNKNOWN_ITEM_NAME = "Unknown";
    public static final String SPECIAL_BIN = BIN_SPECIAL;
    public static final String NON_RECYCLABLE_BIN = BIN_NON_RECYCLABLE;
    public static final String E_WASTE_CATEGORY = CATEGORY_E_WASTE;
    public static final int MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;
    public static final int MAX_OPTIONAL_TEXT_LENGTH = 200;
    public static final int MAX_LIST_ITEMS = 3;

    private WasteConstants() {
    }
}
