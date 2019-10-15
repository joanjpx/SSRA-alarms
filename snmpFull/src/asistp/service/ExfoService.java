/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.service;

import asistp.bean.BeanAlarms;
import asistp.util.ConstantAlarm;
import asistp.util.Util;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Usuario2
 */
public class ExfoService extends AbstractService {

    private static String from = null;

    public ExfoService(String ip) {
        super("exfo");
        from = ip;
    }

    @Override
    public BeanAlarms createBeanAlarms(HashMap<String, String> match) {
        BeanAlarms model = new BeanAlarms();
        String eventDescription = match.get(ConstantAlarm.EVENT_DESCRIPTION);
        model.setEvent_description(eventDescription);
        /**
         * Severity 16/03/2018
         */
        if (eventDescription.equals("Degradation")) {
            model.setSeverity("Warning");
        } else if (eventDescription.equals("Break") || eventDescription.equals("Cut")) {
            model.setSeverity("Critical");
        }

        model.setProbable_cause(eventDescription);
        model.setElement_identifier(match.get(ConstantAlarm.ELEMENT_IDENTIFIER));
        //model.setAdditional_information(match.getOrDefault(ConstantAlarm.ADDITIONAL_INFORMATION, "0") + " km");
        model.setAdditional_information(match.get("rtuFaultSiteName")+" - "+match.get("rtuOtauPort")+" - "+match.get("rtuRotauPort"));
        model.setInstance(match.get(ConstantAlarm.EVENT_DESCRIPTION));
        

        Date dateStart = null;
        Date dateEnd = null;
        try {
            dateStart = Util.parseDate(match.get(ConstantAlarm.EVENT_DATE_START), "M/d/yyyy h:mm:ss a");
            dateEnd = Util.parseDate(match.get(ConstantAlarm.ALARM_EVENT_DATE_END), "M/d/yyyy h:mm:ss a");
        } catch (Exception e) {
            try {
                dateStart = Util.parseDate(match.get(ConstantAlarm.EVENT_DATE_START), "M/d/yyyy H:m:s");
                dateEnd = Util.parseDate(match.get(ConstantAlarm.ALARM_EVENT_DATE_END), "M/d/yyyy H:m:s");
            } catch (Exception ea) {
                try {
                    //2018-02-23 14:19:48
                    dateStart = Util.parseDate(match.get(ConstantAlarm.EVENT_DATE_START), "yyyy-M-d h:mm:ss a");
                    dateEnd = Util.parseDate(match.get(ConstantAlarm.ALARM_EVENT_DATE_END), "yyyy-M-d h:mm:ss a");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    //System.out.println("Error(" + from + "): " + ex.getMessage());
                }
            }

        }
        
        String flag = match.get(ConstantAlarm.ALARM_FAULT_FLAG);
        /* 8/6/2018 */
        if (eventDescription.equals("Break") && flag.equals("New")) {
            model.setStatus("New");
            model.setFault_flag("Active");
            
        } else if (eventDescription.equals("Break") && flag.equals("Cleared") ) {
            model.setStatus("Cleared");
            model.setFault_flag("Cleared");
        }else{
            model.setFault_flag(null);
            }
        /*if (flag.equals("New")) {
            model.setStatus("New");
            model.setFault_flag("Active");
            
        } else if (flag.equals("Cleared") ) {
            model.setStatus("Cleared");
            model.setFault_flag("Cleared");
        }else{
        model.setFault_flag(null);
        }*/
        model.setEvent_date_start(dateEnd);
        
        //nuevo
        
        model.setEvent_detail(match.get("event"));
        return model;
    }

}
