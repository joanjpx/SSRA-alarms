/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.logic;

import asistp.bean.BeanAlarms;
import asistp.dao.DaoAlarms;
import org.apache.log4j.Logger;

/**
 *
 * @author Usuario
 */
public class LogicAlarms {

    private static Logger log = Logger.getLogger(LogicAlarms.class);

    public LogicAlarms() {

    }

    public String insertAlarm(BeanAlarms beanAlarms) {
        String rpta;
        try {
            DaoAlarms dao = new DaoAlarms();
            rpta = dao.insertAlarm(beanAlarms);
        } catch (Exception e) {
            e.printStackTrace();
            rpta = "*Error Db: " + e.getMessage();
        }
        return rpta;
    }

    public String updateAlarm(BeanAlarms beanAlarms) {
        String rpta;
        try {
            DaoAlarms dao = new DaoAlarms();
            rpta = dao.updateAlarm(beanAlarms);
        } catch (Exception e) {
            e.printStackTrace();
            rpta = "*Error Db: " + e.getMessage();
        }
        return rpta;
    }

    public BeanAlarms getBeanAlarms(String alarm_identifier) {
        try {
            DaoAlarms dao = new DaoAlarms();
            return dao.getBeanAlarms(alarm_identifier);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }
}
