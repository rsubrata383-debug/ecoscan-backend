package com.ecoscan.service;

import com.ecoscan.constant.ApiMessages;
import com.ecoscan.demo.DemoData;
import com.ecoscan.exception.ApiException;
import com.ecoscan.model.DemoItem;
import com.ecoscan.model.WasteResult;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

    public List<DemoItem> getItems() {
        return DemoData.ITEMS;
    }

    public WasteResult getResult(String id) {
        WasteResult result = DemoData.RESULTS.get(id);
        if (result == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, ApiMessages.DEMO_ITEM_NOT_FOUND);
        }
        return result;
    }

    public com.ecoscan.model.MultiWasteResult getMultiDemoResult() {
        return com.ecoscan.model.MultiWasteResult.of(List.of(
                DemoData.RESULTS.get("plastic-bottle"),
                DemoData.RESULTS.get("aluminum-can"),
                DemoData.RESULTS.get("banana-peel")
        ));
    }
}
