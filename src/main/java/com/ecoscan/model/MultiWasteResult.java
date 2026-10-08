package com.ecoscan.model;

import java.util.List;

public record MultiWasteResult(
        List<WasteResult> items,
        WasteResult primaryItem,
        int totalDetected) {

    public static MultiWasteResult of(List<WasteResult> items) {
        WasteResult primary = (items != null && !items.isEmpty()) ? items.get(0) : null;
        int count = (items != null) ? items.size() : 0;
        return new MultiWasteResult(items != null ? items : List.of(), primary, count);
    }
}
