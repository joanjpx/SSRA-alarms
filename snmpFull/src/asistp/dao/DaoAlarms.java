/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.dao;

import asistp.bean.BeanAlarms;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;

/**
 *
 * @author Usuario
 */
public class DaoAlarms extends Conexion {

    public DaoAlarms() {

    }

    public String insertAlarm(BeanAlarms beanAlarms) throws Exception {
        PreparedStatement ps = null;

        Connection cnx = null;
        try {
            cnx = getConexion(null);

            /* 8/6/2018 */
            ps = cnx.prepareStatement("SELECT id FROM alarms WHERE element_identifier=? and event_date_end is null order by id desc");
            ps.setString(1, beanAlarms.getElement_identifier());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Long id = rs.getLong(1);
                ps = cnx.prepareStatement("UPDATE alarms SET event_date_end=? WHERE id=?");
                ps.setLong(2, id);
                ps.setDate(1, java.sql.Date.valueOf(beanAlarms.getEvent_date_end().toInstant().atZone(ZoneId.of("America/Lima")).toLocalDate()));

            } else {
                String sqlinsert = " INSERT INTO alarms(";
                sqlinsert += " severity,instance,event,event_date,event_date_end,event_detail,";
                sqlinsert += " probable_cause,additional_information,fault_function,fault_flag,device_ip,";
                sqlinsert += " alarm_identifier, device_name)";
                sqlinsert += " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
                ps = cnx.prepareStatement(sqlinsert);
                ps.setString(1, beanAlarms.getSeverity());
                ps.setString(2, beanAlarms.getInstance());
                ps.setString(3, beanAlarms.getEvent());
                ps.setTimestamp(4, new Timestamp(beanAlarms.getEvent_date().getTime()));
                ps.setTimestamp(5, beanAlarms.getEvent_date_end() == null ? null
                        : new Timestamp(beanAlarms.getEvent_date_end().getTime()));
                ps.setString(6, beanAlarms.getEvent_detail());
                ps.setString(7, beanAlarms.getProbable_cause());
                ps.setString(8, beanAlarms.getAdditional_information());
                ps.setString(9, beanAlarms.getFault_function());
                ps.setString(10, beanAlarms.getFault_flag());
                ps.setString(11, beanAlarms.getDevice_ip());
                ps.setString(12, beanAlarms.getAlarm_identifier());
                ps.setString(13, beanAlarms.getDevice_name());
            }
            ps.executeUpdate();
            //System.out.println("Alarma Registrado Correctamente");
            return "Alarma Registrado Correctamente";

        } catch (SQLException e) {
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (cnx != null) {
                cnx.close();
            }
        }
    }

    public String updateAlarm(BeanAlarms beanAlarms) throws Exception {
        PreparedStatement ps = null;
        Connection cnx = null;
        try {
            cnx = getConexion(null);
            String sqlUpdate = " update alarms set";
            sqlUpdate += " event_date_end=?,";
            sqlUpdate += " alarm_duration=?";
            sqlUpdate += " where id=?";
            ps = cnx.prepareStatement(sqlUpdate);
            ps.setTimestamp(1, new Timestamp(beanAlarms.getEvent_date_end().getTime()));
            ps.setString(2, beanAlarms.getAlarm_duration());
            ps.setLong(3, beanAlarms.getId());
            ps.executeUpdate();
            return "Alarma Actualizada Correctamente";
        } catch (SQLException e) {
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (cnx != null) {
                cnx.close();
            }
        }
    }

    public BeanAlarms getBeanAlarms(String alarm_identifier)
            throws Exception {
        CallableStatement cs = null;
        Connection con = null;
        ResultSet rs = null;
        try {
            BeanAlarms alarm = null;
            con = getConexion(null);
            String procedure = " select id,severity,instance,event,event_date,event_detail,";
            procedure += " probable_cause,additional_information,fault_function,fault_flag,device_ip,";
            procedure += " alarm_identifier";
            procedure += " from alarms";
            procedure += " where alarm_identifier=?";
            procedure += " limit 1";
            cs = con.prepareCall(procedure);
            cs.setString(1, alarm_identifier);
            rs = cs.executeQuery();
            while (rs.next()) {
                alarm = new BeanAlarms();
                alarm.setId(rs.getLong("id"));
                alarm.setSeverity(rs.getString("severity"));
                alarm.setInstance(rs.getString("instance"));
                alarm.setEvent(rs.getString("event"));
                alarm.setEvent_date(rs.getTimestamp("event_date"));
                alarm.setEvent_detail(rs.getString("event_detail"));
                alarm.setProbable_cause(rs.getString("probable_cause"));
                alarm.setAdditional_information(rs.getString("additional_information"));
                alarm.setFault_function(rs.getString("fault_function"));
                alarm.setFault_flag(rs.getString("fault_flag"));
                alarm.setDevice_ip(rs.getString("device_ip"));
                alarm.setAlarm_identifier(rs.getString("alarm_identifier"));
            }
            return alarm;
        } catch (SQLException ex) {
            throw ex;
        } finally {
            cerrarConexion(con, rs, cs);
        }
    }

}
