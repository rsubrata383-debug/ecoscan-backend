package com.ecoscan.service;

import com.ecoscan.constant.ApiMessages;
import com.ecoscan.exception.ApiException;
import com.ecoscan.gemini.GeminiClient;
import com.ecoscan.gemini.GeminiResultParser;
import com.ecoscan.model.MultiWasteResult;
import com.ecoscan.validation.ImageValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GeminiService {

    private final ImageValidator imageValidator;
    private final GeminiClient geminiClient;
    private final GeminiResultParser resultParser;

    public GeminiService(
            ImageValidator imageValidator,
            GeminiClient geminiClient,
            GeminiResultParser resultParser) {
        this.imageValidator = imageValidator;
        this.geminiClient = geminiClient;
        this.resultParser = resultParser;
    }

    public boolean isEnabled() {
        return geminiClient.isEnabled();
    }

    public MultiWasteResult scan(MultipartFile image) {
        byte[] bytes = imageValidator.validate(image);
        if (!isEnabled()) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, ApiMessages.AI_DISABLED);
        }
        return resultParser.parseResponse(geminiClient.generate(bytes, image.getContentType()));
    }
}
