package edu.phystech.stethoscope.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class Utils {

    private static final String DATE_PATTERN = "dd:MM:yyyy";

    public static String dateToString(DateTime dateTime) {
        return dateTime.toString(DATE_PATTERN);
    }

    public static DateTime stringToDate(String string) {
        return DateTimeFormat.forPattern(DATE_PATTERN).parseDateTime(string);
    }

}
