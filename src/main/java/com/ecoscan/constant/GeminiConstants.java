package com.ecoscan.constant;

public final class GeminiConstants {

    public static final String URL =
            "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent";
    public static final String API_KEY_HEADER = "x-goog-api-key";
    public static final String ITEM_NAME = "itemName";
    public static final String CATEGORY = "category";
    public static final String BIN = "bin";
    public static final String TIP = "tip";
    public static final String MATERIAL = "material";
    public static final String RECYCLABILITY_FIELD = "recyclability";
    public static final String HOW_TO_PREPARE = "howToPrepare";
    public static final String DECOMPOSITION_TIME = "decompositionTime";
    public static final String WHY_IT_MATTERS = "whyItMatters";
    public static final String PROS_OF_RIGHT_DISPOSAL = "prosOfRightDisposal";
    public static final String CONS_OF_WRONG_DISPOSAL = "consOfWrongDisposal";
    public static final String AFTER_RECYCLING_IT_BECOMES = "afterRecyclingItBecomes";
    public static final String REUSE_IDEAS = "reuseIdeas";
    public static final String FUN_FACT = "funFact";
    public static final String COMMON_MISTAKE = "commonMistake";
    public static final String TYPE = "type";
    public static final String OBJECT_TYPE = "OBJECT";
    public static final String STRING_TYPE = "STRING";
    public static final String ARRAY_TYPE = "ARRAY";
    public static final String ENUM = "enum";
    public static final String PROPERTIES = "properties";
    public static final String ITEMS = "items";
    public static final String REQUIRED = "required";
    public static final String CONTENTS = "contents";
    public static final String PARTS = "parts";
    public static final String TEXT = "text";
    public static final String INLINE_DATA = "inline_data";
    public static final String MIME_TYPE = "mime_type";
    public static final String DATA = "data";
    public static final String GENERATION_CONFIG = "generationConfig";
    public static final String RESPONSE_MIME_TYPE = "responseMimeType";
    public static final String APPLICATION_JSON = "application/json";
    public static final String RESPONSE_SCHEMA = "responseSchema";
    public static final String CANDIDATES = "candidates";
    public static final String CONTENT = "content";
    public static final String PROMPT = """
            Identify the main waste item in the photo. Choose the bin by common household recycling rules.
            The bin from your answer is final. Put batteries, e-waste and bulbs in "special". Put thin plastic bags
            and food-soiled or mixed-material items in "non-recyclable".
            Use very simple English. Keep the tip short. Lists must have 2 or 3 items, with every line 12 words
            or fewer. Use general facts only and words like "up to" or "about". Do not give exact carbon or CO2
            numbers. prosOfRightDisposal are good things when sorted into the right bin. consOfWrongDisposal are
            bad things when sorted into the wrong bin.
            If there is no clear waste item, return itemName "Unknown", bin "non-recyclable", a tip asking the
            user to try again with a clearer photo, and empty strings or empty lists for all other fields.
            Return only JSON fields in the required schema.
            """;

    private GeminiConstants() {
    }
}
