package com.ecoscan.model;

import java.util.List;

public record WasteResult(
        String itemName,
        String category,
        String bin,
        String tip,
        String material,
        String recyclability,
        List<String> howToPrepare,
        String decompositionTime,
        String whyItMatters,
        List<String> prosOfRightDisposal,
        List<String> consOfWrongDisposal,
        List<String> afterRecyclingItBecomes,
        List<String> reuseIdeas,
        String funFact,
        String commonMistake) {
}
