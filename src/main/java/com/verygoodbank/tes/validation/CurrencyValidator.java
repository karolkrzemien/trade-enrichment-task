package com.verygoodbank.tes.validation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;

import static com.verygoodbank.tes.constant.Constants.CURRENCY_COLUMN;

@Slf4j
public class CurrencyValidator {

    public static boolean isValidCurrency(CSVRecord record, String currency) {
        if (currency.length() == 3){
            return true;
        }
        log.warn("Invalid currency for trade data on line {}: {}", record.getRecordNumber(), record.get(CURRENCY_COLUMN));
        return false;

    }
}

