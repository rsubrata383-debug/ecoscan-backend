package com.ecoscan.gemini;

import static com.ecoscan.constant.GeminiConstants.API_KEY_HEADER;
import static com.ecoscan.constant.GeminiConstants.URL_TEMPLATE;

import com.ecoscan.constant.ApiMessages;
import com.ecoscan.exception.ApiException;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.JsonNode;

@Component
public class GeminiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);

    private final RestClient restClient;
    private final GeminiRequestBuilder requestBuilder;
    private final String apiKey;
    private final String model;

    public GeminiClient(
            RestClient restClient,
            GeminiRequestBuilder requestBuilder,
            @Value("${gemini.api.key:}") String apiKey,
            @Value("${gemini.model:gemini-flash-lite-latest}") String model) {
        this.restClient = restClient;
        this.requestBuilder = requestBuilder;
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.model = model != null ? model.trim() : "gemini-flash-lite-latest";
    }

    public boolean isEnabled() {
        return !apiKey.isBlank();
    }

    public JsonNode generate(byte[] imageBytes, String mimeType) {
        String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
        try {
            java.net.URI uri = java.net.URI.create(String.format(URL_TEMPLATE, model));
            return restClient.post()
                    .uri(uri)
                    .header(API_KEY_HEADER, apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBuilder.build(encodedImage, mimeType))
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException exception) {
            int status = exception.getStatusCode().value();
            log.warn("Gemini returned HTTP status {}: {}", status, exception.getResponseBodyAsString());
            if (status == HttpStatus.TOO_MANY_REQUESTS.value()) {
                throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, ApiMessages.TOO_MANY_SCANS);
            }
            throw new ApiException(HttpStatus.BAD_GATEWAY, ApiMessages.SCAN_FAILED);
        } catch (ResourceAccessException exception) {
            log.warn("Gemini connection or timeout problem: {}", exception.getClass().getSimpleName());
            throw new ApiException(HttpStatus.BAD_GATEWAY, ApiMessages.SCAN_FAILED);
        } catch (RestClientException exception) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, ApiMessages.SCAN_FAILED);
        }
    }
}
