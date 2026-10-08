package com.ecoscan.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecoscan.support.TestImages;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "gemini.api.key=")
@AutoConfigureMockMvc
class ScanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void emptyImageReturnsBadRequest() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "empty.png", "image/png", new byte[0]);
        mockMvc.perform(multipart("/api/scan").file(image))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void textFileReturnsBadRequest() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "notes.txt", "text/plain", "not an image".getBytes());
        mockMvc.perform(multipart("/api/scan").file(image))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void missingContentTypeReturnsBadRequest() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "photo.png", null, TestImages.PNG);
        mockMvc.perform(multipart("/api/scan").file(image))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Upload a valid JPEG, PNG, or WebP image."));
    }

    @Test
    void wrongImageBytesReturnBadRequest() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "photo.png", "image/png", "not a PNG".getBytes());
        mockMvc.perform(multipart("/api/scan").file(image))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void demoListHasNineItems() throws Exception {
        mockMvc.perform(get("/api/demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(9));
    }

    @Test
    void unknownDemoItemReturnsMessage() throws Exception {
        mockMvc.perform(get("/api/demo/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Demo item was not found."));
    }

    @Test
    void unknownRouteReturnsJsonMessage() throws Exception {
        mockMvc.perform(get("/api/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.message").value("The request is not valid."));
    }

    @Test
    void statusShowsAiSetting() throws Exception {
        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aiEnabled").value(false));
    }
}
