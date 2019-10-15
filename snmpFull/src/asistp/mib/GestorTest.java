/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.mib;

import java.util.Map;
import java.util.HashMap;
import asistp.util.ConstantAlarm;
import asistp.util.ReadProperties;

/**
 *
 * @author Usuario
 */
public class GestorTest extends MibGestor {

    private static GestorTest _instance = null;

    public GestorTest() {
        inicialize();
    }

    public static GestorTest Instance() {
        if (_instance == null) {
            _instance = new GestorTest();
        }
        return _instance;
    }

    private void inicialize() {
        initOid();
        initAlarm();
    }

    @Override
    public void initOid() {
        this.mapOid = new HashMap();
        ReadProperties.readProperties("mibU2000", mapOid);
    }

    @Override
    public void initAlarm() {
        this.mapAlarm = new HashMap();
        this.mapAlarm.put(ConstantAlarm.ALARM_SEVERITY, "hwNmNorthboundSeverity");
        this.mapAlarm.put(ConstantAlarm.ALARM_INSTANCE, "hwNmNorthboundObjectInstance");
        this.mapAlarm.put(ConstantAlarm.ALARM_EVENT, "hwNmNorthboundEventName");
        this.mapAlarm.put(ConstantAlarm.ALARM_EVENT_DATE, "hwNmNorthboundEventTime");
        this.mapAlarm.put(ConstantAlarm.ALARM_EVENT_DETAIL, "hwNmNorthboundEventDetail");
        this.mapAlarm.put(ConstantAlarm.ALARM_PROBABLE_CAUSE, "hwNmNorthboundProbableCause");
        this.mapAlarm.put(ConstantAlarm.ALARM_ADDITIONAL_INFORMATION, "hwNmNorthboundAdditionalInfo");
        this.mapAlarm.put(ConstantAlarm.ALARM_FAULT_FUNCTION, "hwNmNorthboundFaultFunction");
        this.mapAlarm.put(ConstantAlarm.ALARM_FAULT_FLAG, "hwNmNorthboundFaultFlag");
        this.mapAlarm.put(ConstantAlarm.ALARM_DEVICE_IP, "hwNmNorthboundDeviceIP");
        this.mapAlarm.put(ConstantAlarm.ALARM_IDENTIFIER, "hwNmNorthboundSerialNo");
    }

    @Override
    public Map<String, String> getMapOid() {
        return this.mapOid;
    }

    @Override
    public Map<String, String> getMapAlarm() {
        return this.mapAlarm;
    }

}
