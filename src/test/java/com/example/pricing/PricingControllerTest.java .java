package com.example.pricing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PricingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPriceFlow() throws Exception {
        // 1. Prepare and Upload TSV
        // Using the exact data from your assignment
        String tsvContent = "SkuID|StartTime|EndTime|Price\n" +
                            "u00006541|10:00|10:15|101\n" +
                            "u00006541|10:05|10:10|99";
        
        MockMultipartFile file = new MockMultipartFile(
                "file", "offers.tsv", "text/plain", tsvContent.getBytes());

        mockMvc.perform(multipart("/api/pricing/upload").file(file))
                .andExpect(status().isOk());

        // 2. TDD Case: Time 09:55 (Expect NOT SET - Before window)
        mockMvc.perform(get("/api/pricing/price")
                .param("skuid", "u00006541")
                .param("time", "09:55"))
                .andExpect(status().isOk())
                .andExpect(content().string("NOT SET"));

        // 3. TDD Case: Time 10:03 (Expect 101 - Inside first window)
        mockMvc.perform(get("/api/pricing/price")
                .param("skuid", "u00006541")
                .param("time", "10:03"))
                .andExpect(status().isOk())
                .andExpect(content().string("101"));

        // 4. TDD Case: Time 10:05 (Expect 99 - Overlapping/Specific window)
        mockMvc.perform(get("/api/pricing/price")
                .param("skuid", "u00006541")
                .param("time", "10:05"))
                .andExpect(status().isOk())
                .andExpect(content().string("99"));

        // 5. Requirement: Time is non-mandatory (Should not crash, returns NOT SET or Price based on current time)
        mockMvc.perform(get("/api/pricing/price")
                .param("skuid", "u00006541"))
                .andExpect(status().isOk());

        // 6. Requirement: Missing Mandatory Skuid (Expect 400 Bad Request)
        mockMvc.perform(get("/api/pricing/price"))
                .andExpect(status().isBadRequest());
    }
}