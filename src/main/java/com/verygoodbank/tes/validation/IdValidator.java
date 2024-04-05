package com.verygoodbank.tes.validation;

import com.verygoodbank.tes.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;

@Slf4j
public class IdValidator {

    public static boolean isInteger(CSVRecord record, String number ){
        String pattern = "^[1-9]\\d*(\\.\\d+)?|0\\.\\d+$";

        if (number.matches(pattern)){
            return true;
        }
        log.warn("Invalid ID for line {}: {}", record.getRecordNumber(), record.get(Constants.PRODUCT_ID) );
        return false;
    }
}
