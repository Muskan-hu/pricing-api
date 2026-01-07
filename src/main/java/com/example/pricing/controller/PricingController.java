package com.example.pricing.controller;

import com.example.pricing.service.PricingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {
    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @PostMapping("/upload")
    public String uploadTsv(@RequestParam("file") MultipartFile file) throws IOException {
        pricingService.uploadTsv(new String(file.getBytes()));
        return "File uploaded successfully";
    }

    @GetMapping("/price")
    public String getPrice(@RequestParam String skuid, @RequestParam(required = false) String time) {
        return pricingService.getPrice(skuid, time);
    }
}