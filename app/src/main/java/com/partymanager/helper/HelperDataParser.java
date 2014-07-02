package com.partymanager.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class HelperDataParser {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
    private static SimpleDateFormat dfgiornoN = new SimpleDateFormat("d", Locale.ITALIAN);
    private static SimpleDateFormat dfgiornoS = new SimpleDateFormat("E", Locale.ITALIAN);
    private static SimpleDateFormat dfMese = new SimpleDateFormat("MMM", Locale.ITALIAN);
    private static SimpleDateFormat dfMeseN = new SimpleDateFormat("MM", Locale.ITALIAN);
    private static SimpleDateFormat dfYear = new SimpleDateFormat("yyyy", Locale.ITALIAN);

    public static String getMese(GregorianCalendar cal) {
        return dfMese.format(cal.getTime());
    }

    public static String getGiornoLettere(GregorianCalendar cal) {
        return dfgiornoS.format(cal.getTime());
    }

    public static String getMeseN(GregorianCalendar cal) {
        return dfMeseN.format(cal.getTime());
    }

    public static String getGiornoNumerio(GregorianCalendar cal) {
        return dfgiornoN.format(cal.getTime());
    }

    public static String getYear(GregorianCalendar cal) {
        return dfYear.format(cal.getTime());
    }

    public static GregorianCalendar getCalFromString(String data) {
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
        try {
            Date dateTime = dateFormat.parse(data);
            GregorianCalendar gregCalendar = new GregorianCalendar();
            gregCalendar.setTime(dateTime);
            return gregCalendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getStringFromCal(GregorianCalendar cal) {
        return cal != null ? dateFormat.format(cal.getTime()) : null;
    }

    public static long getCountDownDays(int dayMonth, int month, int year) {

        Calendar thatDay = Calendar.getInstance();
        thatDay.set(Calendar.DAY_OF_MONTH, dayMonth);
        thatDay.set(Calendar.MONTH, month - 1); // 0-11 so 1 less
        thatDay.set(Calendar.YEAR, year);

        Calendar today = Calendar.getInstance();
        long diff = thatDay.getTimeInMillis() - today.getTimeInMillis();

        return diff / (24 * 60 * 60 * 1000);

    }
}
