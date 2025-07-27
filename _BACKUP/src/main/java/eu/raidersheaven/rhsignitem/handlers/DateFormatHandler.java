package eu.raidersheaven.rhsignitem.handlers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatHandler {

    /**
     * Converts the date and time with their format (from config.yml)
     * @param pattern
     * @return
     */
    public static String getCurrentDate(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }
}
