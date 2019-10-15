package asistp.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Usuario2
 */
public class Util {
    private static final Logger incidentLog = Logger.getLogger("incident");
    public static Date parseDate(String date, String format) throws Exception {
        
        try {
            LocalDateTime ldt = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(format));
        ZonedDateTime utcTime = ldt.atZone(ZoneOffset.UTC);
        
        ZonedDateTime peruTime = utcTime.withZoneSameInstant(ZoneId.of("America/Lima"));
        return Date.from(peruTime.toInstant());
        } catch (Exception e) {
            StringWriter error = new StringWriter();
            e.printStackTrace(new PrintWriter(error));
            incidentLog.warn(error.toString());
        }
        return null;
        
//        Date dateAttribute = null;
//        try {
//            SimpleDateFormat simpleDate = new SimpleDateFormat(format);
//            dateAttribute = simpleDate.parse(date);
//            return dateAttribute;
//        } catch (NullPointerException e) {
//        }
//        return dateAttribute;
    }

}
