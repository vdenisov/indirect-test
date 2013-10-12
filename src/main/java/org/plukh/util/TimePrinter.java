package org.plukh.util;

import java.util.Calendar;
import java.util.TimeZone;

public class TimePrinter {
    public static String printTime(long time) {
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTimeInMillis(time);
        return String.format("%tH:%<tM:%<tS.%<tL", cal);
    }

    public static void main(String[] args) {
        System.out.println(printTime(5 * 1000 * 60 + 15 * 1000 + 200 ));
        System.out.println(printTime(2 * 1000 * 3600 + 3 * 1000 * 60 + 2 * 1000 + 1));
        System.out.println(printTime(0));
    }
}
