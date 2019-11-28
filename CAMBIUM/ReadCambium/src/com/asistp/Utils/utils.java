package com.asistp.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class utils {
    public static String getFileName(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date today = new Date();
        String Stoday = sdf.format(today);
        return Stoday;
    }
}