package com.verygoodbank.tes.validation;

import com.verygoodbank.tes.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;

@Slf4j
public class PriceValidator {

    public static boolean isPriceANumber(CSVRecord record, String price) {

        String pattern = "^(0|[1-9]\\d*)(\\.\\d+)?$";

        if (price.matches(pattern)) {
            return true;
        }
        log.warn("Invalid price for line number: {}, {}", record.getRecordNumber(), record.get(Constants.PRICE_COLUMN));
        return false;
    }
}
