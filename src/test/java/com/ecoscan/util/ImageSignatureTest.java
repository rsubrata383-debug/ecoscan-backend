package com.ecoscan.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ecoscan.support.TestImages;
import org.junit.jupiter.api.Test;

class ImageSignatureTest {

    @Test
    void matchesSupportedSignatures() {
        assertTrue(ImageSignature.matches(TestImages.PNG, "image/png"));
        assertTrue(ImageSignature.matches(TestImages.JPEG, "image/jpeg"));
        assertTrue(ImageSignature.matches(TestImages.WEBP, "image/webp"));
    }

    @Test
    void rejectsNullEmptyAndMismatchedInput() {
        assertFalse(ImageSignature.matches(null, "image/png"));
        assertFalse(ImageSignature.matches(new byte[0], "image/png"));
        assertFalse(ImageSignature.matches(TestImages.PNG, "image/jpeg"));
        assertFalse(ImageSignature.matches(TestImages.PNG, null));
    }

    @Test
    void acceptsSignatureWithAdditionalImageBytes() {
        byte[] image = new byte[TestImages.PNG.length + 2];
        System.arraycopy(TestImages.PNG, 0, image, 0, TestImages.PNG.length);
        image[TestImages.PNG.length] = 1;
        image[TestImages.PNG.length + 1] = 2;

        assertTrue(ImageSignature.matches(image, "image/png"));
    }
}
