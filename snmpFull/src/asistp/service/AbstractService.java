/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.service;

import asistp.bean.BeanAlarms;
import asistp.util.ReadProperties;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Usuario2
 */
public abstract class AbstractService implements Service {

    protected HashMap<String, String> fromProperties = new HashMap<>();

    public AbstractService(String propertyName) {

        ReadProperties.readProperties(propertyName, fromProperties);
    }

    @Override
    public BeanAlarms match(HashMap<String, String> oids) {

        HashMap<String, String> matches = new HashMap<>();

        oids.forEach((k, v) -> {

            String column = fromProperties.get(k);

            if (column != null) {
                //System.out.println(column + " -> " + v);
                matches.put(column, v);
            }

        });

        return createBeanAlarms(matches);
    }

    public HashMap<String, String> match2(HashMap<String, String> oids) {

        HashMap<String, String> matches = new HashMap<>();

        oids.forEach((k, v) -> {

            String column = fromProperties.get(k);

            if (column != null) {
                //System.out.println(column + " -> " + v);
                matches.put(column, v);
            }

        });
        return matches;
    }

    public HashMap<String, String> parse(List<StringBuilder> lines, String TypeAlarma) {
        HashMap<String, String> fromSnmp = new HashMap<>();
         SimpleDateFormat sdf=new SimpleDateFormat("ddMMyyyy");
         String date=sdf.format(new Date());
        try {

             FileWriter fw=new FileWriter(date+"_"+TypeAlarma+".txt", true);
                   PrintWriter pw=new PrintWriter(fw);
                 pw.println("---------------------------------------");
            lines.forEach(str -> {
                //System.out.println("*******"+str);

                   pw.println(str);
                String[] part = str.toString().split("=");
                if (part.length > 1) {
                    fromSnmp.put(part[0].trim(), part[1].trim());
                }
            });

            fw.close();    
            pw.close();
        } catch (Exception e) {
        }

        return fromSnmp;
    }

    public HashMap<String, String> match3(HashMap<String, String> oids) {
        HashMap<String, String> matches = new HashMap<>();

        oids.forEach((k, v) -> {

            String column = fromProperties.get(k);
            if (column != null) {
                //System.out.println("Column from properties " + column + " -> Valor a guardar" + v);
                matches.put(column, v);
            }

        });

        return matches;

    }

    public HashMap<String, String> parse2(List<StringBuilder> lines) {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        String date = sdf.format(new Date());
        HashMap<String, String> fromSnmp = new HashMap<>();
        try {

            FileWriter fw = new FileWriter(date + "_" + "SIAE" + ".txt", true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println("---------------------------------------");
            lines.forEach(str -> {
                //System.out.println("*******"+str);

                pw.println(str);

                String[] part = str.toString().split("=");
                

                if (part.length > 1) {
                    fromSnmp.put(part[0].trim(), part[1].trim());
                }
            });
            fw.close();
            pw.close();
            
           if(!fromSnmp.containsKey("1.3.6.1.4.1.3373.51.10.1.1.3")){
               return null;
           }

        } catch (Exception e) {
        }

        return fromSnmp;
    }

}
