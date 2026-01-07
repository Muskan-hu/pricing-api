package com.example.pricing.service;

import com.example.pricing.model.PriceOffer;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PricingService {
    // Fast retrieval storage
    private final Map<String, List<PriceOffer>> inventory = new ConcurrentHashMap<>();

    public void uploadTsv(String content) {
        String[] lines = content.split("\n");
        // Skip header
        for (int i = 1; i < lines.length; i++) {
            String[] parts = lines[i].split("\\t|\\|"); // Handles tabs or pipes
            if (parts.length < 4) continue;

            PriceOffer offer = new PriceOffer(
                parts[0].trim(),
                LocalTime.parse(formatTime(parts[1].trim())),
                LocalTime.parse(formatTime(parts[2].trim())),
                Integer.parseInt(parts[3].trim())
            );
            inventory.computeIfAbsent(offer.getSkuId(), k -> new ArrayList<>()).add(offer);
        }
    }

    public String getPrice(String skuId, String timeStr) {
        if (!inventory.containsKey(skuId)) return "SKU NOT FOUND";
        
        LocalTime queryTime = (timeStr == null) ? LocalTime.now() : LocalTime.parse(formatTime(timeStr));
        
        return inventory.get(skuId).stream()
                .filter(offer -> !queryTime.isBefore(offer.getStartTime()) && !queryTime.isAfter(offer.getEndTime()))
                // Get the most specific (shortest) or last window if multiple overlap
                .reduce((first, second) -> second) 
                .map(offer -> String.valueOf(offer.getPrice()))
                .orElse("NOT SET");
    }

    private String formatTime(String time) {
        return time.length() == 4 ? "0" + time : time; // Converts 7:00 to 07:00
    }
}