package com.verygoodbank.tes;

import com.verygoodbank.tes.validation.PriceValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TradeEnrichmentServiceApplication {

    public static void main(String[] args) {
       SpringApplication.run(TradeEnrichmentServiceApplication.class, args);
    }
}