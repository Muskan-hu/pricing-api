package com.example.pricing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class PriceOffer {
    private String skuId;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer price;
}