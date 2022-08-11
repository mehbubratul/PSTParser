package com.mehbub.lib;

import org.apache.commons.validator.GenericValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;

public class DateUtil {

    static final List<String> datePatterns = Arrays.asList(
            "yyyy-MM-dd", "yyyy/MM/dd", "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy",
            "MM-dd-yyyy", "MM/dd/yyyy", "yyyyMMdd");


    static final String targetDatePattern = "yyyy-MM-dd";

    public static AbstractMap.SimpleEntry<Boolean, String> DateValidation(String value) {

        for (String datePattern : datePatterns) {
            boolean isValid = GenericValidator.isDate(value, datePattern, true);
            if (isValid) {
                String result = conversionFromOneDateFormatToAnother(value, datePattern);
                return new AbstractMap.SimpleEntry<>(true, result);
            }
        }

        return new AbstractMap.SimpleEntry<>(false, "");
    }

    private static String conversionFromOneDateFormatToAnother(String startDateString, String currentPattern) {
        return LocalDate.parse(startDateString, DateTimeFormatter.ofPattern(currentPattern)).format(DateTimeFormatter.ofPattern(targetDatePattern));
    }
}
