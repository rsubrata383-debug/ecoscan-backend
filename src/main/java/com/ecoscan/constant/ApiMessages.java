package com.ecoscan.constant;

public final class ApiMessages {

    public static final String PLEASE_UPLOAD_IMAGE = "Please upload an image.";
    public static final String EMPTY_IMAGE = "Please upload a non-empty image.";
    public static final String IMAGE_READ_FAILED = "The image could not be read.";
    public static final String IMAGE_TOO_LARGE = "Image must be 5 MB or smaller.";
    public static final String INVALID_IMAGE_TYPE = "Upload a valid JPEG, PNG, or WebP image.";
    public static final String AI_DISABLED = "AI mode is off. Use demo mode.";
    public static final String TOO_MANY_SCANS = "Too many scans right now. Wait a moment and try again.";
    public static final String SCAN_FAILED = "The image scan failed. Please try again.";
    public static final String NO_SCAN_RESULT = "The image scan returned no result. Please try again.";
    public static final String INVALID_SCAN_RESULT = "The image scan returned an invalid result. Please try again.";
    public static final String DEMO_ITEM_NOT_FOUND = "Demo item was not found.";
    public static final String INVALID_REQUEST = "The request is not valid.";
    public static final String UNEXPECTED_ERROR = "Something went wrong. Please try again.";

    private ApiMessages() {
    }
}
