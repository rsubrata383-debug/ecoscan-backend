package com.ecoscan.service;

import com.ecoscan.exception.ApiException;
import com.ecoscan.model.WasteResult;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GeminiService {

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent";
    private static final List<String> CATEGORIES = List.of("Plastic", "Organic", "E-Waste", "Paper", "Metal", "Glass",
            "Other");
    private static final List<String> BINS = List.of("recyclable", "organic", "non-recyclable", "special");
    private static final String PROMPT = """
            Identify the main waste item in the photo. Choose the bin by common household recycling rules.
            Put batteries, e-waste and bulbs in "special". Put thin plastic bags and food-soiled or mixed-material
            items in "non-recyclable". If there is no clear waste item, return itemName "Unknown", bin
            "non-recyclable", and a tip asking the user to try again with a clearer photo. Return only the JSON
            fields in the required schema. Keep the tip to 20 words or fewer in simple English.
            """;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GeminiService(
            RestClient restClient,
            ObjectMapper objectMapper,
            @Value("${gemini.api.key:}") String apiKey,
            @Value("${gemini.model:gemini-2.5-flash}") String model) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
    }

    public boolean isEnabled() {
        return !apiKey.isBlank();
    }

    public WasteResult scan(MultipartFile image) {
        if (image.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Please upload a non-empty image.");
        }

        String mimeType = image.getContentType();
        if (!List.of("image/jpeg", "image/png", "image/webp").contains(mimeType)
                || !hasMatchingSignature(image, mimeType)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Upload a valid JPEG, PNG, or WebP image.");
        }

        if (!isEnabled()) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "AI mode is off. Use demo mode.");
        }

        try {
            String encodedImage = Base64.getEncoder().encodeToString(image.getBytes());
            JsonNode response = restClient.post()
                    .uri(GEMINI_URL, model)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createRequest(encodedImage, mimeType))
                    .retrieve()
                    .body(JsonNode.class);
            return parseResult(response);
        } catch (RestClientException exception) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "The image scan failed. Please try again.");
        } catch (java.io.IOException exception) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "The image scan failed. Please try again.");
        }
    }

    private Map<String, Object> createRequest(String encodedImage, String mimeType) {
        Map<String, Object> schema = Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "itemName", Map.of("type", "STRING"),
                        "category", Map.of("type", "STRING", "enum", CATEGORIES),
                        "bin", Map.of("type", "STRING", "enum", BINS),
                        "tip", Map.of(
                                "type", "STRING",
                                "description", "A simple English tip with no more than 20 words.")),
                "required", List.of("itemName", "category", "bin", "tip"));
        Map<String, Object> generationConfig = Map.of(
                "responseMimeType", "application/json",
                "responseSchema", schema);
        return Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(
                                Map.of("text", PROMPT),
                                Map.of("inline_data", Map.of("mime_type", mimeType, "data", encodedImage))))),
                "generationConfig", generationConfig);
    }

    private WasteResult parseResult(JsonNode response) {
        JsonNode textNode = response == null ? null
                : response.path("candidates").path(0).path("content").path("parts").path(0).path("text");
        if (textNode == null || !textNode.isString() || textNode.asString().isBlank()) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "The image scan returned no result. Please try again.");
        }
        try {
            WasteResult result = objectMapper.readValue(textNode.asString(), WasteResult.class);
            if (result == null || result.itemName() == null || result.itemName().isBlank()
                    || result.tip() == null || result.tip().isBlank()
                    || result.tip().trim().split("\\s+").length > 20
                    || !CATEGORIES.contains(result.category()) || !BINS.contains(result.bin())) {
                throw new ApiException(HttpStatus.BAD_GATEWAY, "The image scan returned an invalid result.");
            }
            return result;
        } catch (JacksonException exception) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "The image scan returned an invalid result.");
        }
    }

    private boolean hasMatchingSignature(MultipartFile image, String mimeType) {
        try {
            byte[] bytes = image.getBytes();
            return switch (mimeType) {
                case "image/jpeg" -> bytes.length >= 3
                        && (bytes[0] & 0xff) == 0xff && (bytes[1] & 0xff) == 0xd8 && (bytes[2] & 0xff) == 0xff;
                case "image/png" -> bytes.length >= 8
                        && (bytes[0] & 0xff) == 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4e
                        && bytes[3] == 0x47 && bytes[4] == 0x0d && bytes[5] == 0x0a
                        && bytes[6] == 0x1a && bytes[7] == 0x0a;
                case "image/webp" -> bytes.length >= 12
                        && bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
                        && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P';
                default -> false;
            };
        } catch (java.io.IOException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "The image could not be read.");
        }
    }
}
