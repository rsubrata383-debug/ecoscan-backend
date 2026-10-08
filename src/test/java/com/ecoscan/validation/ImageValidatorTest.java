package com.ecoscan.validation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ecoscan.exception.ApiException;
import com.ecoscan.support.TestImages;
import com.ecoscan.constant.WasteConstants;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

class ImageValidatorTest {

    private final ImageValidator validator = new ImageValidator();

    @Test
    void rejectsEmptyFile() {
        MockMultipartFile image = new MockMultipartFile("image", "empty.png", "image/png", new byte[0]);
        assertStatus(HttpStatus.BAD_REQUEST, image);
    }

    @Test
    void rejectsNullMimeTypeAndWrongBytes() {
        assertStatus(HttpStatus.BAD_REQUEST,
                new MockMultipartFile("image", "image.png", null, TestImages.PNG));
        assertStatus(HttpStatus.BAD_REQUEST,
                new MockMultipartFile("image", "image.png", "image/png", "bad".getBytes()));
    }

    @Test
    void rejectsTextMimeType() {
        assertStatus(HttpStatus.BAD_REQUEST,
                new MockMultipartFile("image", "notes.txt", "text/plain", "text".getBytes()));
    }

    @Test
    void rejectsFilesLargerThanFiveMegabytes() {
        byte[] bytes = new byte[WasteConstants.MAX_IMAGE_SIZE_BYTES + 1];
        assertStatus(HttpStatus.CONTENT_TOO_LARGE,
                new MockMultipartFile("image", "large.png", "image/png", bytes));
    }

    @Test
    void acceptsPngJpegAndWebpAndReturnsBytes() {
        assertArrayEquals(TestImages.PNG, validator.validate(
                new MockMultipartFile("image", "image.png", "image/png", TestImages.PNG)));
        assertArrayEquals(TestImages.JPEG, validator.validate(
                new MockMultipartFile("image", "image.jpg", "image/jpeg", TestImages.JPEG)));
        assertArrayEquals(TestImages.WEBP, validator.validate(
                new MockMultipartFile("image", "image.webp", "image/webp", TestImages.WEBP)));
    }

    private void assertStatus(HttpStatus status, MockMultipartFile image) {
        ApiException exception = assertThrows(ApiException.class, () -> validator.validate(image));
        assertEquals(status, exception.getStatus());
    }
}
