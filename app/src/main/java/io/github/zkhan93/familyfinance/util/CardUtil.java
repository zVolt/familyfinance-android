package io.github.zkhan93.familyfinance.util;

public class CardUtil {
    public static String formatCardNumber(String number, char delimiter, boolean hideDigits){
        String replace4 = String.format("$0%c", delimiter);
        String replace6 = String.format("$0%c", delimiter);
        if (hideDigits){
            replace4 = String.format("****%c", delimiter);
            replace6 = String.format("******%c", delimiter);
        }
        if (number.length() == 15){
            return number.replaceFirst("\\d{4}", replace4).replaceFirst("\\d{6}", replace6);
        }
        return number.replaceAll("\\d{4}(?!$)", replace4);
    }
}
