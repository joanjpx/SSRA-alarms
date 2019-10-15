/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.bean;

import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Usuario
 */
public class BeanAlarms {
    String typeAlarma;
    private long id;
    private String severity;
    private String instance;
    private String event;
    private Date event_date;
    /**
     * Reemplaza a event_date 07/02/2018
     */
    private Date event_date_start;
    private Date event_date_end;
    private String event_detail;
    private String probable_cause;
    private String additional_information;
    private String fault_function;
    private String fault_flag;
    private String device_ip;
    private String alarm_identifier;
    private String alarm_duration;
    private String device_name;
    private String event_description;
    private String element_identifier;
    private String sistema;
    private Timestamp stamp;
    private String nbi_sys;
    
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
    
    private boolean New = false;

    public boolean isNew() {
        return New;
    }

    public void setNew(boolean New) {
        this.New = New;
    }

    

    public String getElement_identifier() {
        return element_identifier;
    }

    public void setElement_identifier(String element_identifier) {
        this.element_identifier = element_identifier;
    }
    
    public String getEvent_description() {
        return event_description;
    }

    public void setEvent_description(String event_description) {
        this.event_description = event_description;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSeverity() {
        return severity;
    }

    public Date getEvent_date_start() {
        return event_date_start;
    }

    public void setEvent_date_start(Date event_date_start) {
        this.event_date_start = event_date_start;
    }   

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Date getEvent_date() {
        return event_date;
    }

    public void setEvent_date(Date event_date) {
        this.event_date = event_date;
    }

    public Date getEvent_date_end() {
        return event_date_end;
    }

    public void setEvent_date_end(Date event_date_end) {
        this.event_date_end = event_date_end;
    }

    public String getEvent_detail() {
        return event_detail;
    }

    public void setEvent_detail(String event_detail) {
        this.event_detail = event_detail;
    }

    public String getProbable_cause() {
        return probable_cause;
    }

    public void setProbable_cause(String probable_cause) {
        this.probable_cause = probable_cause;
    }

    public String getAdditional_information() {
        return additional_information;
    }

    public void setAdditional_information(String additional_information) {
        this.additional_information = additional_information;
    }

    public String getFault_function() {
        return fault_function;
    }

    public void setFault_function(String fault_function) {
        this.fault_function = fault_function;
    }

    public String getFault_flag() {
        return fault_flag;
    }

    public void setFault_flag(String fault_flag) {
        this.fault_flag = fault_flag;
    }

    public String getDevice_ip() {
        return device_ip;
    }

    public void setDevice_ip(String device_ip) {
        this.device_ip = device_ip;
    }

    public String getAlarm_identifier() {
        return alarm_identifier;
    }

    public void setAlarm_identifier(String alarm_identifier) {
        this.alarm_identifier = alarm_identifier;
    }

    public String getAlarm_duration() {
        return alarm_duration;
    }

    public void setAlarm_duration(String alarm_duration) {
        this.alarm_duration = alarm_duration;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }
    
    public String toString2() {
        StringBuilder strB = new StringBuilder();
        
        Stream.of(this.getClass().getDeclaredFields()).forEach(p -> {
            try {
                strB.append(p.getName());
                strB.append("=");
                strB.append(p.get(this));
                strB.append("\n");
            } catch (IllegalArgumentException ex) {
                //System.out.println(ex.getMessage());
            } catch (IllegalAccessException ex) {
                //System.out.println(ex.getMessage());
            }
        });
        
        return strB.toString();
        
    }

    public String getTypeAlarma() {
        return typeAlarma;
    }

    public void setTypeAlarma(String typeAlarma) {
        this.typeAlarma = typeAlarma;
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public Timestamp getStamp() {
        return stamp;
    }

    public void setStamp(Timestamp stamp) {
        this.stamp = stamp;
    }

    public String getNbi_sys() {
        return nbi_sys;
    }

    public void setNbi_sys(String nbi_sys) {
        this.nbi_sys = nbi_sys;
    }
    
    
    
    

    @Override
    public String toString() {
        return "BeanAlarms{" + "typeAlarma=" + typeAlarma + ", id=" + id + ", severity=" + severity + ", instance=" + instance + ", event=" + event + ", event_date=" + event_date + ", event_date_start=" + event_date_start + ", event_date_end=" + event_date_end + ", event_detail=" + event_detail + ", probable_cause=" + probable_cause + ", additional_information=" + additional_information + ", fault_function=" + fault_function + ", fault_flag=" + fault_flag + ", device_ip=" + device_ip + ", alarm_identifier=" + alarm_identifier + ", alarm_duration=" + alarm_duration + ", device_name=" + device_name + ", event_description=" + event_description + ", element_identifier=" + element_identifier + ", status=" + status + ", New=" + New + '}';
    }
    
    
    
}
