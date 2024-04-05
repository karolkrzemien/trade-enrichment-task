package com.verygoodbank.tes.validation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.verygoodbank.tes.constant.Constants.DATE_COLUMN;


@Slf4j
public class DateValidator {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static boolean isValidDate(CSVRecord record, String dateStr) {
        try {
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            log.warn("Invalid date format for trade data on line {}: {}", record.getRecordNumber(), record.get(DATE_COLUMN));
            return false;
        }
    }
}
