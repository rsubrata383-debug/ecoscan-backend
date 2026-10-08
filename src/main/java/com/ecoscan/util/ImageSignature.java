package com.ecoscan.util;

import static com.ecoscan.constant.WasteConstants.MIME_JPEG;
import static com.ecoscan.constant.WasteConstants.MIME_PNG;
import static com.ecoscan.constant.WasteConstants.MIME_WEBP;

public final class ImageSignature {

    private ImageSignature() {
    }

    public static boolean matches(byte[] bytes, String mimeType) {
        if (bytes == null || mimeType == null) {
            return false;
        }
        return switch (mimeType) {
            case MIME_JPEG -> bytes.length >= 3
                    && (bytes[0] & 0xff) == 0xff && (bytes[1] & 0xff) == 0xd8 && (bytes[2] & 0xff) == 0xff;
            case MIME_PNG -> bytes.length >= 8
                    && (bytes[0] & 0xff) == 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4e
                    && bytes[3] == 0x47 && bytes[4] == 0x0d && bytes[5] == 0x0a
                    && bytes[6] == 0x1a && bytes[7] == 0x0a;
            case MIME_WEBP -> bytes.length >= 12
                    && bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
                    && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P';
            default -> false;
        };
    }
}
