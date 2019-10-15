/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.gestor;

import asistp.bean.BeanAlarms;
import asistp.bean.BeanOid;
import asistp.bean.BeanTrap;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import asistp.util.ConvertOid;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import asistp.logic.LogicAlarms;
import asistp.mib.U2000;
import asistp.util.CalculoDuracion;
import asistp.util.ConstantAlarm;

/**
 *
 * @author Usuario
 */
public class GestorU2000 {

    private final Logger trapsLog = Logger.getLogger("traps");
    private final Logger incidentLog = Logger.getLogger("incident");
    private BeanTrap beanTrap;
    private U2000 beanU2000;
    private boolean swError;
    private String strError;

    public GestorU2000() {

    }

    public void init(StringBuilder event, List<StringBuilder> list) {
        swError = false;
        strError = "el traps se registro correctamente";
        beanTrap = new BeanTrap();
        beanTrap.setEvent(event);
        List<BeanOid> listOid = new ArrayList();
        beanU2000 = U2000.Instance();
        Map<String, Integer> mapPosOid = new HashMap();
        for (StringBuilder str : list) {
            BeanOid beanOid = ConvertOid.getBeanOid(str);
            if (beanOid != null) {
                beanOid.setOidConvert(ConvertOid.getOidConvert(beanOid.getOid(), beanU2000.getMapOid()));
                if (beanOid.getOidConvert().length() > 0) {
                    mapPosOid.put(beanOid.getOidConvert().toString(), listOid.size());
                }
                listOid.add(beanOid);
            }
        }
        beanTrap.setListOid(listOid);
        beanTrap.setMapPosOid(mapPosOid);
        writeBeanAlarm();
        if (swError) {
            incidentLog.error(beanTrap);
        } else {
            trapsLog.info(beanTrap);
        }
    }

    private void writeBeanAlarm() {
        try {
            BeanAlarms beanAlarms = null;
            String fault_flag = getValueAttributeStr(ConstantAlarm.ALARM_FAULT_FLAG, 20);
            String alarm_identifier = getValueAttributeStr(ConstantAlarm.ALARM_IDENTIFIER, 30);
            if (fault_flag.equalsIgnoreCase("Recovery")) {
                LogicAlarms logic = new LogicAlarms();
                beanAlarms = logic.getBeanAlarms(alarm_identifier);
                if (beanAlarms != null) {
                    Date event_end = getValueAttributeDate(ConstantAlarm.ALARM_EVENT_DATE);
                    beanAlarms.setEvent_date_end(event_end);
                    beanAlarms.setAlarm_duration(CalculoDuracion.getDiferenciaDias(beanAlarms.getEvent_date(), event_end));
                    updateAlarmDb(beanAlarms);
                }
            }
            if (beanAlarms == null) {
                beanAlarms = new BeanAlarms();
                beanAlarms.setSeverity(getValueAttributeStr(ConstantAlarm.ALARM_SEVERITY, 100));
                beanAlarms.setInstance(getValueAttributeStr(ConstantAlarm.ALARM_INSTANCE, 80));
                beanAlarms.setEvent(getValueAttributeStr(ConstantAlarm.ALARM_EVENT, 255));
                beanAlarms.setEvent_detail(getValueAttributeStr(ConstantAlarm.ALARM_EVENT_DETAIL, 255));
                beanAlarms.setProbable_cause(getValueAttributeStr(ConstantAlarm.ALARM_PROBABLE_CAUSE, 255));
                beanAlarms.setAdditional_information(getValueAttributeStr(ConstantAlarm.ALARM_ADDITIONAL_INFORMATION, 255));
                beanAlarms.setFault_function(getValueAttributeStr(ConstantAlarm.ALARM_FAULT_FUNCTION, 100));
                beanAlarms.setFault_flag(fault_flag);
                beanAlarms.setDevice_ip(getValueAttributeStr(ConstantAlarm.ALARM_DEVICE_IP, 20));
                beanAlarms.setEvent_date(getValueAttributeDate(ConstantAlarm.ALARM_EVENT_DATE));
                beanAlarms.setAlarm_identifier(alarm_identifier);
                beanAlarms.setDevice_name(getValueAttributeStr(ConstantAlarm.DEVICE_NAME, 100));
                beanTrap.setStrError(strError);
                if (!swError) {
                    insertAlarmDb(beanAlarms);
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            //swError = true;
            //strError = "Error al leer el parametro: " + nameAttribute;
            //strError += "\nError: " + e.getMessage();
            //return "";
        }
    }

    private void insertAlarmDb(BeanAlarms beanAlarms) {
        LogicAlarms logic = new LogicAlarms();
        strError = logic.insertAlarm(beanAlarms);
        swError = strError.substring(0, 1).equals("*");
        beanTrap.setStrError(strError);
    }

    private void updateAlarmDb(BeanAlarms beanAlarms) {
        LogicAlarms logic = new LogicAlarms();
        strError = logic.updateAlarm(beanAlarms);
        swError = strError.substring(0, 1).equals("*");
        beanTrap.setStrError(strError);
    }

    private String getValueAttributeStr(String nameAttribute, int longitud)
            throws Exception {
        try {
            String val = "";
            String oid = beanU2000.getMapAlarm().get(nameAttribute);
            int pos = beanTrap.getMapPosOid().get(oid);
            if (pos > -1) {
                val = beanTrap.getListOid().get(pos).getValue().toString();
            }
            if (val.length() >= longitud) {
                val = val.substring(0, longitud);
            }
            return val;
        } catch (Exception e) {
            swError = true;
            strError = "Error al leer el parametro: " + nameAttribute;
            strError += "\nError: " + e.getMessage();
            throw e;
        }
    }

    private Date getValueAttributeDate(String nameAttribute)
            throws Exception {
        try {
            Date dateAttribute;
            String val = "";
            String oid = beanU2000.getMapAlarm().get(nameAttribute);
            int pos = beanTrap.getMapPosOid().get(oid);
            if (pos > -1) {
                val = beanTrap.getListOid().get(pos).getValue().toString();
            }
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy/MM/dd - HH:mm:ss'Z'", Locale.US);
            dateAttribute = format.parse(val);
            return dateAttribute;
        } catch (Exception e) {
            swError = true;
            strError = "Error al leer el parametro: " + nameAttribute;
            strError += "\nError: " + e.getMessage();
            throw e;
        }
    }

}
