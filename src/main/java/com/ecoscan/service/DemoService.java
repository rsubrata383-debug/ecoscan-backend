package com.ecoscan.service;

import com.ecoscan.exception.ApiException;
import com.ecoscan.model.DemoItem;
import com.ecoscan.model.WasteResult;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

    private final List<DemoItem> items = List.of(
            new DemoItem("plastic-bottle", "Plastic Bottle", "🧴"),
            new DemoItem("aluminum-can", "Aluminum Can", "🥫"),
            new DemoItem("banana-peel", "Banana Peel", "🍌"),
            new DemoItem("paper", "Paper", "📄"),
            new DemoItem("glass-bottle", "Glass Bottle", "🍾"),
            new DemoItem("food-waste", "Food Waste", "🍎"),
            new DemoItem("plastic-bag", "Plastic Bag", "🛍️"),
            new DemoItem("battery", "Battery", "🔋"),
            new DemoItem("cardboard", "Cardboard", "📦"));

    private final Map<String, WasteResult> results = Map.of(
            "plastic-bottle",
            new WasteResult("Plastic Bottle", "Plastic", "recyclable", "Rinse it and put it in the recycling bin."),
            "aluminum-can", new WasteResult("Aluminum Can", "Metal", "recyclable", "Rinse it before recycling."),
            "banana-peel", new WasteResult("Banana Peel", "Organic", "organic", "Add it to your compost bin."),
            "paper", new WasteResult("Paper", "Paper", "recyclable", "Keep it clean and dry for recycling."),
            "glass-bottle",
            new WasteResult("Glass Bottle", "Glass", "recyclable", "Rinse it and recycle it carefully."),
            "food-waste", new WasteResult("Food Waste", "Organic", "organic", "Put food scraps in your compost bin."),
            "plastic-bag",
            new WasteResult("Plastic Bag", "Plastic", "non-recyclable", "Reuse it or put it in general waste."),
            "battery", new WasteResult("Battery", "E-Waste", "special", "Take it to a battery collection point."),
            "cardboard", new WasteResult("Cardboard", "Paper", "recyclable", "Flatten it and keep it dry."));

    public List<DemoItem> getItems() {
        return items;
    }

    public WasteResult getResult(String id) {
        WasteResult result = results.get(id);
        if (result == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Demo item was not found.");
        }
        return result;
    }
}
