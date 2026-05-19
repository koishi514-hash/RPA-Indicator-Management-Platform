package com.rbac.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class IndicatorDateUtil {
    private static final DateTimeFormatter APP_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter INVOICE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDate parseAppDate(String appDate) {
        return LocalDate.parse(appDate, APP_DATE_FMT);
    }

    public static LocalDateTime parseInvoiceTime(String invoiceTime) {
        return LocalDateTime.parse(invoiceTime, INVOICE_TIME_FMT);
    }

    public static boolean isInvoiceIn1To12MonthsBeforeAppDateExcludingCurrentMonth(LocalDate appDate, LocalDateTime invoiceTime) {
        YearMonth appYm = YearMonth.from(appDate);
        YearMonth invYm = YearMonth.from(invoiceTime);

        int diff = (appYm.getYear() - invYm.getYear()) * 12 + (appYm.getMonthValue() - invYm.getMonthValue());
        return diff >= 1 && diff <= 12;
    }

    public static boolean tryParseInvoiceTime(String invoiceTime) {
        try {
            parseInvoiceTime(invoiceTime);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}

