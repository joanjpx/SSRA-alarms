/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.service;

import asistp.bean.BeanAlarms;
import java.util.HashMap;

/**
 *
 * @author Usuario2
 */
public interface Service {

    public BeanAlarms match(HashMap<String, String> oids);

    //public BeanAlarms createBeanAlarms(HashMap<String, String> match);

    default BeanAlarms createBeanAlarms(HashMap<String, String> match) {
        return new BeanAlarms();
    }

}
