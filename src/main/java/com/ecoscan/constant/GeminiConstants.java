package com.ecoscan.constant;

/**
 * Constants used for interacting with the Google Gemini API and defining
 * waste classification schemas for the EcoScan service.
 */
public final class GeminiConstants {

        private GeminiConstants() {
                throw new UnsupportedOperationException("Utility/Constants class cannot be instantiated");
        }

        // =========================================================================
        // API Configuration & Endpoints
        // =========================================================================
        public static final String URL_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";
        public static final String API_KEY_HEADER = "x-goog-api-key";
        public static final String DEFAULT_MODEL = "gemini-1.5-flash";

        // =========================================================================
        // Gemini Request / Response Wire Protocol Keys
        // =========================================================================
        public static final String CONTENTS = "contents";
        public static final String CONTENT = "content";
        public static final String CANDIDATES = "candidates";
        public static final String PARTS = "parts";
        public static final String TEXT = "text";
        public static final String INLINE_DATA = "inline_data";
        public static final String MIME_TYPE = "mime_type";
        public static final String DATA = "data";
        public static final String GENERATION_CONFIG = "generationConfig";
        public static final String RESPONSE_MIME_TYPE = "responseMimeType";
        public static final String RESPONSE_SCHEMA = "responseSchema";
        public static final String APPLICATION_JSON = "application/json";

        // MIME Types
        public static final String MIME_IMAGE_JPEG = "image/jpeg";
        public static final String MIME_IMAGE_PNG = "image/png";
        public static final String MIME_IMAGE_WEBP = "image/webp";

        // =========================================================================
        // OpenAPI / JSON Schema Specification Keys & Types
        // =========================================================================
        public static final String TYPE = "type";
        public static final String PROPERTIES = "properties";
        public static final String ITEMS = "items";
        public static final String REQUIRED = "required";
        public static final String ENUM = "enum";
        public static final String DESCRIPTION = "description";

        public static final String OBJECT_TYPE = "OBJECT";
        public static final String STRING_TYPE = "STRING";
        public static final String ARRAY_TYPE = "ARRAY";
        public static final String INTEGER_TYPE = "INTEGER";
        public static final String BOOLEAN_TYPE = "BOOLEAN";

        // =========================================================================
        // Waste Classification Domain Fields (JSON Schema Property Names)
        // =========================================================================
        public static final String ITEM_NAME = "itemName";
        public static final String CATEGORY = "category";
        public static final String BIN = "bin";
        public static final String MATERIAL = "material";
        public static final String RECYCLABILITY_FIELD = "recyclability";
        public static final String TIP = "tip";
        public static final String HOW_TO_PREPARE = "howToPrepare";
        public static final String DECOMPOSITION_TIME = "decompositionTime";
        public static final String WHY_IT_MATTERS = "whyItMatters";
        public static final String PROS_OF_RIGHT_DISPOSAL = "prosOfRightDisposal";
        public static final String CONS_OF_WRONG_DISPOSAL = "consOfWrongDisposal";
        public static final String AFTER_RECYCLING_IT_BECOMES = "afterRecyclingItBecomes";
        public static final String REUSE_IDEAS = "reuseIdeas";
        public static final String FUN_FACT = "funFact";
        public static final String COMMON_MISTAKE = "commonMistake";

        // =========================================================================
        // Bin Classification Enums & Fallback Values
        // =========================================================================
        public static final String UNKNOWN_ITEM = "Unknown";
        public static final String FALLBACK_TIP = "Please try again with a closer, clearer photo of the waste item.";

        // =========================================================================
        // System Prompt
        // =========================================================================
        public static final String PROMPT = """
                        You are an expert waste classification engine. Analyze the provided image and identify up to 5 distinct waste items, ordered from most prominent to least prominent.

                        ### Bin Categories:
                        Assign each item exactly one of the following bins:
                        - "recyclable": Clean rigid plastics, metals, paper, cardboard, and clean glass.
                        - "non-recyclable": Thin plastic films/bags, food-soiled packaging, contaminated paper, and general refuse.
                        - "compost": Food scraps, organic waste, and yard clippings.
                        - "special": Batteries, electronics (e-waste), lightbulbs, and hazardous materials.

                        ### Field Constraints & Style:
                        - Language: Use simple, everyday English. Avoid complex technical jargon or academic words (such as "polymers", "anaerobic", "degradation", "leachate"). Write short, direct sentences that a student can easily understand.
                        - Lists: Any array fields (e.g., prosOfRightDisposal, consOfWrongDisposal, reuseIdeas) must contain 2 or 3 items.
                        - Conciseness: Every bullet item or tip string must be 12 words or fewer.
                        - Environmental metrics: Use qualified estimations only (e.g., "about", "up to"). Never provide exact carbon or CO2 numerical figures.

                        ### Ambiguity / Fallback:
                        If no recognizable waste item is present or the photo is illegible:
                        - Set "itemName" to "Unknown".
                        - Set "bin" to "non-recyclable".
                        - Set "tip" to "Please try again with a closer, clearer photo of the waste item."
                        - Return empty arrays or empty strings for all other descriptive fields.
                        """;
}