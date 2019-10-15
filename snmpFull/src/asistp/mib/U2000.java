/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.mib;

import java.util.Map;
import java.util.HashMap;
//import asistp.util.ConstantAlarm;
import asistp.util.ReadProperties;

/**
 *
 * @author Usuario
 */
public class U2000 {

    private static U2000 _instance = null;
    private Map<String, String> mapOid;
    private Map<String, String> mapAlarm;

    public U2000() {
        inicialize();
    }

    public static U2000 Instance() {
        if (_instance == null) {
            _instance = new U2000();
        }
        return _instance;
    }

    private void inicialize() {
        initOid();
        initAlarm();
    }

    private void initOid() {
        mapOid = new HashMap();
        ReadProperties.readProperties("mibU2000", mapOid);
    }

    /*private void initOid() {
     mapOid = new HashMap();
     mapOid.put("1.3.6.1.2.1.1.3.0", "sysUpTime");
     mapOid.put("1.3.6.1.6.3.1.1.4.1.0", "snmpTrapOID");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.1.0", "hwNmNorthboundNEName");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.2.0", "hwNmNorthboundNEType");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.3.0", "hwNmNorthboundObjectInstance");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.4.0", "hwNmNorthboundEventType");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.5.0", "hwNmNorthboundEventTime");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.6.0", "hwNmNorthboundProbableCause");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.7.0", "hwNmNorthboundSeverity");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.8.0", "hwNmNorthboundEventDetail");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.9.0", "hwNmNorthboundAdditionalInfo");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.10.0", "hwNmNorthboundFaultFlag");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.11.0", "hwNmNorthboundFaultFunction");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.12.0", "hwNmNorthboundDeviceIP");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.13.0", "hwNmNorthboundSerialNo");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.14.0", "hwNmNorthboundProbableRepair");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.15.0", "hwNmNorthboundResourceIDs");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.24.0", "hwNmNorthboundEventName");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.25.0", "hwNmNorthboundReasonID");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.26.0", "hwNmNorthboundFaultID");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.27.0", "hwNmNorthboundDeviceType");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.28.0", "hwNmNorthboundTrailName");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.29.0", "hwNmNorthboundRootAlarm");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.30.0", "hwNmNorthboundGroupID");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.31.0", "hwNmNorthboundMaintainStatus");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1.7.1.32.0", "hwNmNorthboundRootAlarmSerialNo");
     mapOid.put("1.3.6.1.4.1.2011.2.15.1", "");
     }*/
    private void initAlarm() {
        mapAlarm = new HashMap();
        ReadProperties.readProperties("alarmU2000", mapAlarm);
    }
    /*private void initAlarm() {
     mapAlarm = new HashMap();
     mapAlarm.put(ConstantAlarm.ALARM_SEVERITY, "hwNmNorthboundSeverity");
     mapAlarm.put(ConstantAlarm.ALARM_INSTANCE, "hwNmNorthboundObjectInstance");
     mapAlarm.put(ConstantAlarm.ALARM_EVENT, "hwNmNorthboundEventName");
     mapAlarm.put(ConstantAlarm.ALARM_EVENT_DATE, "hwNmNorthboundEventTime");
     mapAlarm.put(ConstantAlarm.ALARM_EVENT_DETAIL, "hwNmNorthboundEventDetail");
     mapAlarm.put(ConstantAlarm.ALARM_PROBABLE_CAUSE, "hwNmNorthboundProbableCause");
     mapAlarm.put(ConstantAlarm.ALARM_ADDITIONAL_INFORMATION, "hwNmNorthboundAdditionalInfo");
     mapAlarm.put(ConstantAlarm.ALARM_FAULT_FUNCTION, "hwNmNorthboundFaultFunction");
     mapAlarm.put(ConstantAlarm.ALARM_FAULT_FLAG, "hwNmNorthboundFaultFlag");
     mapAlarm.put(ConstantAlarm.ALARM_DEVICE_IP, "hwNmNorthboundDeviceIP");
     mapAlarm.put(ConstantAlarm.ALARM_IDENTIFIER, "hwNmNorthboundSerialNo");
     }*/

    public Map<String, String> getMapOid() {
        return mapOid;
    }

    public Map<String, String> getMapAlarm() {
        return mapAlarm;
    }

}
