/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.gestor.prod;

import asistp.bean.BeanAlarms;
import asistp.dao.CoriantDAO;
import asistp.service.AbstractService;
import asistp.util.ConstantAlarm;
import asistp.util.Util;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

import com.sun.javafx.collections.MappingChange.Map;

/**
 *
 * MultiSite
 *
 * @author Usuario2
 */
public class Eltek extends AbstractService {

    private String from = "0.0.0.0";

    private final Logger trapsLog = Logger.getLogger("traps");
    private static final Logger incidentLog = Logger.getLogger("incident");

    public Eltek(String ip) {
        super("eltek");
        from = ip;

    }

    public void run(List<StringBuilder> lines,String path,String pathFileConfig) {
        SimpleDateFormat sdf=new SimpleDateFormat("ddMMyyyy");
        String filename=sdf.format(new Date());
        try {
            HashMap<String, String> keyValOIDs = this.parse(lines,"ELTEK");
            HashMap<String, String> match = this.match2(keyValOIDs);
            BeanAlarms model = hydrate(match, keyValOIDs);
            model.setTypeAlarma("ELTEK");
            model.setNbi_sys("SNMP/ELTEK");
            if (model == null ) {
                throw new NullPointerException("Null model");
            }
            //(new CoriantDAO()).insetAlarms(model, from,lines)
            CoriantDAO dao=new CoriantDAO();
            dao.pathFileConfig = pathFileConfig;
            synchronized (dao) {
            	dao.setModel_Alarms_Translator(model);
            	dao.insetAlarms(model, from, lines);
            	dao.insetAlarmsOtherServer(model, from, lines);
                String tablaname=dao.createTableMensual();
                long id=dao.getId(lines.toString(), "SNMP/ELTEK");
                dao.writeFile(path, tablaname,"ELTEK_"+filename , id+","+lines.toString());
                dao.insetAlarmsMensual(model,tablaname, lines,tablaname);
                if(model.getFault_flag().equals("Active")) {
                	dao.insetAlarmsOnlyCleared(model, from, lines);
                }else if(model.getFault_flag().equals("Cleared")){
                	int y=dao.updateActiveMensual(model, tablaname);
                	if(y==0) {
                		System.out.println("Actualizacion Fallida para Active (Cleared Tabla Mensual) "+tablaname);
                	}else if (y>0) {
                		System.out.println("Actualizacion Exitosa para Active (Cleared tabla Mensual) "+tablaname);
                	}
                	
                	int x=dao.updateActive(model);
                	if(x==0) {
                		System.out.println("Actualizacion Fallida para Active (Cleared Alarms2)");
                	}else if (x==1) {
                		System.out.println("Actualizacion Exitosa para Active (Cleared Alarms2)");
                	}
                	
                	dao.updateAlarms(model);
                }
				
			}
            
            
            
            
        } catch (NullPointerException ne) {
            //ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //ArrayList<BeanAlarms> list = new ArrayList<>();
        //list.add(model);
        //(new CoriantDAO()).insetAlarms(list, from);
    }

    public BeanAlarms hydrate(HashMap<String, String> match, HashMap<String, String> keyValOIDs) {
        BeanAlarms model = new BeanAlarms();
        //System.out.println("===================================0");
        if(!keyValOIDs.containsKey("1.3.6.1.4.1.12148.14.1.1.3.0")){
            return null;
        }
        if(match.get("element_identifier").equals("MultisiteMonitor")) {
        	return null;
        }
        /*keyValOIDs.forEach((k, v) -> {
            
        });*/
        //System.out.println("--------------------------------------");
        match.forEach((k, v) -> {
            //System.out.println(k + "-" + v);
        });
        byte flujo, severity;
        
        try {
            flujo = Byte.parseByte(match.get(ConstantAlarm.ALARM_FAULT_FLAG));
            severity = Byte.valueOf(match.get(ConstantAlarm.ALARM_SEVERITY));
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            return null;
        }

        
        String strSeverity = "";
        
        if (severity == 2 || severity == 9 || severity == 11) {
            strSeverity = "minor";
        } else if (severity == 3 || severity == 7 || severity == 8 || severity == 10) {
            strSeverity = "major";
        } else if (severity == 18) {
            strSeverity = "critical";
        } else {
            return null;
        }

        model.setSeverity(strSeverity);
        String device_name=match.get(ConstantAlarm.DEVICE_NAME);
        model.setInstance(match.get(ConstantAlarm.DEVICE_NAME));
        //cambio
        model.setEvent(match.get(match.get(ConstantAlarm.DEVICE_NAME)));
        model.setProbable_cause(device_name);
        /**
         * 1: start 0: end
         */
        if(flujo==1){
            model.setFault_flag("Active");
        }else if (flujo==0){
            model.setFault_flag("Cleared");
        }else{
            model.setFault_flag(Byte.toString(flujo));
        }
        
        if (flujo == new Byte("1") && severity==18) {
            model.setStatus("New");
            //model.setNew(true);
            //model.setEvent_date_start(parseDate(match.get(ConstantAlarm.ALARM_EVENT_DATE_END)));
        } else if (flujo == new Byte("0") && severity == 18) {
            model.setStatus("Cleared");
            //model.setEvent_date_end(parseDate(match.get(ConstantAlarm.ALARM_EVENT_DATE_END)));
        }
        
        //model.setStatus(String.valueOf(flujo));
        
        model.setEvent_date_start(parseDate(match.get(ConstantAlarm.ALARM_EVENT_DATE_END)));
        
        
        /* 8/6/2016 */
        
        
        

        model.setEvent_detail(keyValOIDs.get(match.get(ConstantAlarm.ALARM_EVENT_DETAIL)));
        model.setAdditional_information(match.get(ConstantAlarm.ADDITIONAL_INFORMATION));
        
        model.setElement_identifier(match.get(ConstantAlarm.ELEMENT_IDENTIFIER));
        model.setDevice_name(match.get(ConstantAlarm.DEVICE_NAME));
//        System.out.println("--------------------------------------");
       // System.out.println(model);
        return model;
    }

    private Date parseDate(String dateStr) {
        
        String[] arr = dateStr.split(":");
//        System.out.println("Año:" + Integer.parseInt("" + arr[0] + arr[1], 16));
//        System.out.println("Mes:" + Integer.parseInt("" + arr[2], 16));
//        System.out.println("Día:" + Integer.parseInt("" + arr[3], 16));
//        System.out.println("Hora:" + Integer.parseInt("" + arr[4], 16));
//        System.out.println("Min:" + Integer.parseInt("" + arr[5], 16));
//        System.out.println("Seg:" + Integer.parseInt("" + arr[6], 16));
        StringBuilder strB = new StringBuilder();
        strB.append(Integer.parseInt("" + arr[2], 16));
        strB.append("/");
        strB.append(Integer.parseInt("" + arr[3], 16));
        strB.append("/");
        strB.append(Integer.parseInt("" + arr[0] + arr[1], 16));
        strB.append(" ");
        strB.append(Integer.parseInt("" + arr[4], 16));
        strB.append(":");
        strB.append(Integer.parseInt("" + arr[5], 16));
        strB.append(":");
        strB.append(Integer.parseInt("" + arr[6], 16));

        try {
            //return Util.parseDate(strB.toString(), "MM/dd/yyyy HH:mm:ss");
            return Util.parseDate(strB.toString(), "M/d/uuuu H:m:s");
        } catch (Exception e) {
            StringWriter error = new StringWriter();
            e.printStackTrace(new PrintWriter(error));
            incidentLog.warn(error.toString());
        }
        return null;
    }

}
