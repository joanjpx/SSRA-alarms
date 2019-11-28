package com.asistp.main;

import java.sql.Timestamp;

public class Bean {
    private String severity;
    private String event_id;
    private Timestamp event_date_start;
    private Timestamp event_date_end;
    private String event_description;
    private String probable_cause;
    private String element_identifier;
    private String element_ip;
    private String alarm_duration;
    private String alarm_identifier;
    private String instance;
    private String event_detail;
    private String additional_information;
    private String fault_flag;
    private String fault_function;
    private String ip_origin;
    private String status;
    private String ciudad;
    private String sistema;
    private Timestamp stamp;
    private String nbi_sys;

    public String getSeverity() {
        return this.severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getEvent_id() {
        return this.event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public Timestamp getEvent_date_start() {
        return this.event_date_start;
    }

    public void setEvent_date_start(Timestamp event_date_start) {
        this.event_date_start = event_date_start;
    }

    public Timestamp getEvent_date_end() {
        return this.event_date_end;
    }

    public void setEvent_date_end(Timestamp event_date_end) {
        this.event_date_end = event_date_end;
    }

    public String getEvent_description() {
        return this.event_description;
    }

    public void setEvent_description(String event_description) {
        this.event_description = event_description;
    }

    public String getProbable_cause() {
        return this.probable_cause;
    }

    public void setProbable_cause(String probable_cause) {
        this.probable_cause = probable_cause;
    }

    public String getElement_identifier() {
        return this.element_identifier;
    }

    public void setElement_identifier(String element_identifier) {
        this.element_identifier = element_identifier;
    }

    public String getElement_ip() {
        return this.element_ip;
    }

    public void setElement_ip(String element_ip) {
        this.element_ip = element_ip;
    }

    public String getAlarm_duration() {
        return this.alarm_duration;
    }

    public void setAlarm_duration(String alarm_duration) {
        this.alarm_duration = alarm_duration;
    }

    public String getAlarm_identifier() {
        return this.alarm_identifier;
    }

    public void setAlarm_identifier(String alarm_identifier) {
        this.alarm_identifier = alarm_identifier;
    }

    public String getInstance() {
        return this.instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getEvent_detail() {
        return this.event_detail;
    }

    public void setEvent_detail(String event_detail) {
        this.event_detail = event_detail;
    }

    public String getAdditional_information() {
        return this.additional_information;
    }

    public void setAdditional_information(String additional_information) {
        this.additional_information = additional_information;
    }

    public String getFault_flag() {
        return this.fault_flag;
    }

    public void setFault_flag(String fault_flag) {
        this.fault_flag = fault_flag;
    }

    public String getFault_function() {
        return this.fault_function;
    }

    public void setFault_function(String fault_function) {
        this.fault_function = fault_function;
    }

    public String getIp_origin() {
        return this.ip_origin;
    }

    public void setIp_origin(String ip_origin) {
        this.ip_origin = ip_origin;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCiudad() {
        return this.ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getSistema() {
        return this.sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public Timestamp getStamp() {
        return this.stamp;
    }

    public void setStamp(Timestamp stamp) {
        this.stamp = stamp;
    }

    public String getNbi_sys() {
        return this.nbi_sys;
    }

    public void setNbi_sys(String nbi_sys) {
        this.nbi_sys = nbi_sys;
    }

    public String toString() {
        return "Bean [severity=" + this.severity + ", event_id=" + this.event_id + ", event_date_start=" + this.event_date_start + ", event_date_end=" + this.event_date_end + ", event_description=" + this.event_description + ", probable_cause=" + this.probable_cause + ", element_identifier=" + this.element_identifier + ", element_ip=" + this.element_ip + ", alarm_duration=" + this.alarm_duration + ", alarm_identifier=" + this.alarm_identifier + ", instance=" + this.instance + ", event_detail=" + this.event_detail + ", additional_information=" + this.additional_information + ", fault_flag=" + this.fault_flag + ", fault_function=" + this.fault_function + ", ip_origin=" + this.ip_origin + ", status=" + this.status + ", ciudad=" + this.ciudad + ", sistema=" + this.sistema + ", stamp=" + this.stamp + ", nbi_sys=" + this.nbi_sys + "]";
    }
}