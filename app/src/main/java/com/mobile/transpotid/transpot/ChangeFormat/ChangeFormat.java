package com.mobile.transpotid.transpot.ChangeFormat;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by andro on 21/03/2016.
 */
public class ChangeFormat {
    public static String formatNumber(Double price){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        String pattern = "Rp #,##0.###";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);

        String formatted = decimalFormat.format(price);

        return formatted;
    }

    public static String formatDate(String tanggal){
        String strCurrentDate = tanggal;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd",Locale.ENGLISH);
        Date newDate = null;
        try {
            newDate = format.parse(strCurrentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("dd/mm/yyyy",Locale.ENGLISH);
        String date = format.format(newDate);

        return date;
    }
}
