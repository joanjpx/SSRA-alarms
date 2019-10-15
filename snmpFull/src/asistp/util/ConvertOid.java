/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.util;

import asistp.bean.BeanOid;
import java.util.Map;

/**
 *
 * @author Usuario
 */
public class ConvertOid {

    public static BeanOid getBeanOid(StringBuilder str) {
        int posEqual = str.indexOf("=");
        if (posEqual > -1) {
            BeanOid beanOid = new BeanOid();
            beanOid.setOid(new StringBuilder(str.substring(0, posEqual).trim()));
            beanOid.setValue(new StringBuilder(str.substring(posEqual + 1, str.length()).trim()));
            return beanOid;
        }
        return null;
    }

    public static StringBuilder getOidConvert(StringBuilder strOid, Map<String, String> mapOid) {
        StringBuilder valConvert = new StringBuilder();
        String value = mapOid.get(strOid.toString());
        if (value != null) {
            valConvert.append(value);
        }
        return valConvert;
    }
}
