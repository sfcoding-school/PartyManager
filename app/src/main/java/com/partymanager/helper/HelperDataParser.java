package com.partymanager.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class HelperDataParser {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
    private static SimpleDateFormat dfgiornoN = new SimpleDateFormat("d", Locale.ITALIAN);
    private static SimpleDateFormat dfgiornoS = new SimpleDateFormat("E", Locale.ITALIAN);
    private static SimpleDateFormat dfMese = new SimpleDateFormat("MMM", Locale.ITALIAN);

    public static String getMese (GregorianCalendar cal){
        return dfMese.format(cal.getTime());
    }

    public static String getGiornoLettere (GregorianCalendar cal){
          return dfgiornoS.format(cal.getTime());
    }

    public static String getGiornoNumerio (GregorianCalendar cal){
        return dfgiornoN.format(cal.getTime());
    }

    public static GregorianCalendar getCalFromString (String data){
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
        try {
            Date dateTime = dateFormat.parse(data);
            GregorianCalendar gregCalendar = new GregorianCalendar();
            gregCalendar.setTime(dateTime);
            return  gregCalendar;
        }catch (ParseException e){
            e.printStackTrace();
            return null;
        }
    }
}
