/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.util;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Usuario
 */
public class ReadProperties {

    public static void readProperties(String nameProperty, Map<String, String> map) {
        ResourceBundle resources = ResourceBundle.getBundle("asistp.property." + nameProperty,
                Locale.getDefault());
        for (Enumeration e = resources.getKeys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            String value = (String) resources.getString(key);
            map.put(key, value);
        }
    }
}
