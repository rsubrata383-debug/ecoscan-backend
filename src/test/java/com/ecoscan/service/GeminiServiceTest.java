package com.ecoscan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ecoscan.exception.ApiException;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestClient;

class GeminiServiceTest {

    @Test
    void emptyKeyReturnsServiceUnavailable() {
        GeminiService service = new GeminiService(RestClient.create(), new ObjectMapper(), "", "gemini-2.5-flash");
        byte[] png = {(byte) 0x89, 'P', 'N', 'G', 0x0d, 0x0a, 0x1a, 0x0a};
        MockMultipartFile image = new MockMultipartFile("image", "photo.png", "image/png", png);

        ApiException exception = assertThrows(ApiException.class, () -> service.scan(image));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
        assertEquals("AI mode is off. Use demo mode.", exception.getMessage());
    }
}
