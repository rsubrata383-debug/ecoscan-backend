package com.ecoscan.validation;

import com.ecoscan.constant.ApiMessages;
import com.ecoscan.constant.WasteConstants;
import com.ecoscan.exception.ApiException;
import com.ecoscan.util.ImageSignature;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageValidator {

    public byte[] validate(MultipartFile image) {
        if (image.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ApiMessages.EMPTY_IMAGE);
        }

        byte[] bytes;
        try {
            bytes = image.getBytes();
        } catch (IOException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ApiMessages.IMAGE_READ_FAILED);
        }

        if (bytes.length > WasteConstants.MAX_IMAGE_SIZE_BYTES) {
            throw new ApiException(HttpStatus.CONTENT_TOO_LARGE, ApiMessages.IMAGE_TOO_LARGE);
        }

        String mimeType = image.getContentType();
        if (mimeType == null || !WasteConstants.IMAGE_MIME_TYPES.contains(mimeType)
                || !ImageSignature.matches(bytes, mimeType)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ApiMessages.INVALID_IMAGE_TYPE);
        }
        return bytes;
    }
}
