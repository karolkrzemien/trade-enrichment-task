package com.verygoodbank.tes.web.controller;


import com.verygoodbank.tes.service.TradeEnrichmentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class TradeEnrichmentController {

    private final TradeEnrichmentServiceImpl tradeEnrichmentService;

    @PostMapping(value = "/enrich", produces = "text/csv")
    public ResponseEntity<String> uploadAndEnrichTradeData(@RequestParam("file") MultipartFile file) {
        String enrichedData = tradeEnrichmentService.enrichTradeData(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .body(enrichedData);
    }
}


