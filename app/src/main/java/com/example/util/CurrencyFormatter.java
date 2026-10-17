package com.example.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

/**
 * Java utility for high-precision PKR currency formatting and reference token generation,
 * demonstrating seamless Java-Kotlin interoperability.
 */
public final class CurrencyFormatter {

    private static final DecimalFormat PKR_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        PKR_FORMAT = new DecimalFormat("#,##0.00", symbols);
    }

    private CurrencyFormatter() {
        // Utility class
    }

    /**
     * Formats an amount as Pakistani Rupee with standard symbol.
     * Example: 125450.5 -> "Rs. 125,450.50"
     */
    public static String formatPkr(double amount) {
        return "Rs. " + PKR_FORMAT.format(amount);
    }

    /**
     * Formats with explicit +/- sign for ledger items.
     * Example: 1500 (isCredit=true) -> "+Rs. 1,500.00"
     */
    public static String formatPkrSigned(double amount, boolean isCredit) {
        String formatted = PKR_FORMAT.format(Math.abs(amount));
        return (isCredit ? "+Rs. " : "-Rs. ") + formatted;
    }

    /**
     * Generates a unique compliant transaction reference ID for demo operations.
     * Example: "NP-98421054"
     */
    public static String generateReferenceId() {
        Random random = new Random();
        int part1 = 1000 + random.nextInt(9000);
        int part2 = 1000 + random.nextInt(9000);
        return "NP-" + part1 + part2;
    }
}
