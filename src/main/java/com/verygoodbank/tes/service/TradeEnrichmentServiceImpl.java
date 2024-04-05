package com.verygoodbank.tes.service;

import com.verygoodbank.tes.interfaces.TradeEnrichmentService;
import com.verygoodbank.tes.validation.CurrencyValidator;
import com.verygoodbank.tes.validation.DateValidator;
import com.verygoodbank.tes.validation.IdValidator;
import com.verygoodbank.tes.validation.PriceValidator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.verygoodbank.tes.constant.Constants.*;

@Service
public class TradeEnrichmentServiceImpl implements TradeEnrichmentService {

    private static final Logger logger = LoggerFactory.getLogger(TradeEnrichmentServiceImpl.class);
    private final Map<String, String> productMap = new HashMap<>();
    private final ResourceLoader resourceLoader;


    private String CSV_PATH = "classpath:product.csv";

    public TradeEnrichmentServiceImpl(ResourceLoader resourceLoader) {
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

    @Override
    public String enrichTradeData(MultipartFile tradeFile) {

        StringBuilder writer = new StringBuilder();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(tradeFile.getInputStream(), StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(fileReader)) {

            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(DATE_COLUMN, PRODUCT_NAME, CURRENCY_COLUMN, PRICE_COLUMN));

            parser.getRecords().stream()
                    .filter(record -> DateValidator.isValidDate(record, record.get(DATE_COLUMN)))
                    .filter(record -> CurrencyValidator.isValidCurrency(record, record.get(CURRENCY_COLUMN)))
                    .filter(record -> IdValidator.isInteger(record, record.get(PRODUCT_ID)))
                    .filter(record -> PriceValidator.isPriceANumber(record, record.get(PRICE_COLUMN)))
                    .forEach(record -> {
                        String date = record.get(DATE_COLUMN);
                        String productId = record.get(PRODUCT_ID);
                        String productName = productMap.getOrDefault(productId, MISSING_PRODUCT_NAME);
                        String currency = record.get(CURRENCY_COLUMN);
                        String price = record.get(PRICE_COLUMN);

                        if (MISSING_PRODUCT_NAME.equals(productName)) {
                            logger.warn("Missing product mapping for ID: {}", productId);
                        }

                        try {
                            csvPrinter.printRecord(date, productName, currency, price);
                        } catch (IOException e) {
                            logger.error("Error writing record: {}", e.getMessage(), e);
                        }
                    });

            csvPrinter.flush();
        } catch (Exception e) {
            logger.error("Error processing trade file: {}", e.getMessage(), e);
        }
        return writer.toString();
    }

}





