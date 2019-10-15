package asistp.service;

import asistp.bean.BeanAlarms;
import asistp.util.CalculoDuracion;
import asistp.util.ConstantAlarm;
import asistp.util.CoriantHelper;
import asistp.util.ReadProperties;
import asistp.util.Util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Usuario2
 */
public class CoriantService extends AbstractService {

    public CoriantService(String propertyName) {
        super(propertyName);
    }

    public CoriantService() {
        super("coriant");
    }
    
    public BeanAlarms createBeanAlarms(HashMap<String, String> match) {

        BeanAlarms bean = new BeanAlarms();

        Date date = CoriantService.toDate(match.get(ConstantAlarm.ALARM_EVENT_DATE));
        Date dateEnd = CoriantService.toDate(match.get(ConstantAlarm.ALARM_EVENT_DATE_END));

        bean.setFault_function(CoriantHelper.humanizeLikelyReason(match.get(ConstantAlarm.ALARM_FAULT_FUNCTION)));
        bean.setProbable_cause(CoriantHelper.humanizeLikelyReason(match.get(ConstantAlarm.ALARM_FAULT_FUNCTION)));

        bean.setInstance(match.get(ConstantAlarm.ALARM_INSTANCE));
        bean.setDevice_name(match.get(ConstantAlarm.ALARM_INSTANCE));
        bean.setAlarm_duration(CoriantService.dateDiff(date, dateEnd));
        bean.setSeverity(CoriantHelper.humanizeSeverity(match.get(ConstantAlarm.ALARM_SEVERITY)));
        bean.setEvent_date(date);
        bean.setEvent_date_end(dateEnd);
        bean.setFault_flag(CoriantHelper.humanizeFaultFlag(match.get(ConstantAlarm.ALARM_FAULT_FLAG)));
        bean.setEvent_detail(match.get(ConstantAlarm.ALARM_EVENT_DETAIL));

        return bean;

    }

    public static Date toDate(String input) {       

        try {
            return Util.parseDate(input, "MM/dd/yyyy HH:mm:ss");
        } catch (Exception e) {
        }
        return null;
    }

    public static String dateDiff(Date dateStart, Date dateEnd) {

        if (dateStart == null || dateEnd == null) {
            return null;
        }

        return CalculoDuracion.getDiferenciaDias(dateStart, dateEnd);
    }

}
