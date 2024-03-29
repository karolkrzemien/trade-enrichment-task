package com.verygoodbank.tes.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Service
public class TradeEnrichmentService {

    private static final Logger logger = LoggerFactory.getLogger(TradeEnrichmentService.class);
    private final Map<String, String> productMap = new HashMap<>();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final ResourceLoader resourceLoader;


    private String CSV_PATH = "classpath:product.csv";

    public TradeEnrichmentService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        loadProductData();
    }

    private void loadProductData() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceLoader.getResource(CSV_PATH).getInputStream(), StandardCharsets.UTF_8));
            reader
                    .lines()
                    .map(line -> line.split(","))
                    .filter(parts -> parts.length >= 2)
                    .forEach(parts -> productMap.put(parts[0], parts[1]));
        } catch (Exception e) {
            logger.error("Error loading product data: {}", e.getMessage(), e);
        }
    }

    public String enrichTradeData(MultipartFile tradeFile) {

        StringBuilder writer = new StringBuilder();

        try {
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                    .setHeader("date", "product_name", "currency", "price")
                    .build());

            try (CSVParser parser = new CSVParser(new InputStreamReader(tradeFile.getInputStream(), StandardCharsets.UTF_8), CSVFormat.DEFAULT)) {
                for (CSVRecord record : parser) {
                    String date = record.get(0);
                    String productId = record.get(1);
                    try {
                        LocalDate.parse(date, DATE_FORMATTER); // Validate date format
                        String productName = productMap.getOrDefault(productId, "Missing Product Name");
                        if ("Missing Product Name".equals(productName)) {
                            logger.warn("Missing product mapping for ID: {}", productId);
                        }
                        writer.append(String.join(",", date, productName, record.get(2), record.get(3))).append("\n");
                    } catch (DateTimeParseException e) {
                        logger.error("Invalid date format for trade data on line {}: {}", record.getRecordNumber(), date);
                    }
                }
            }
            csvPrinter.flush();
            csvPrinter.close();
        } catch (Exception e) {
            logger.error("Error processing trade file: {}", e.getMessage(), e);
        }
        return writer.toString();
    }

}





