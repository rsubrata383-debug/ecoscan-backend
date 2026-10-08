package com.ecoscan.controller;

import com.ecoscan.model.DemoItem;
import com.ecoscan.model.WasteResult;
import com.ecoscan.service.DemoService;
import com.ecoscan.service.GeminiService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ScanController {

    private final GeminiService geminiService;
    private final DemoService demoService;

    public ScanController(GeminiService geminiService, DemoService demoService) {
        this.geminiService = geminiService;
        this.demoService = demoService;
    }

    @PostMapping("/scan")
    public WasteResult scan(@RequestParam("image") MultipartFile image) {
        return geminiService.scan(image);
    }

    @GetMapping("/demo")
    public List<DemoItem> getDemoItems() {
        return demoService.getItems();
    }

    @GetMapping("/demo/{id}")
    public WasteResult getDemoResult(@PathVariable String id) {
        return demoService.getResult(id);
    }

    @GetMapping("/status")
    public Map<String, Boolean> getStatus() {
        return Map.of("aiEnabled", geminiService.isEnabled());
    }
}
