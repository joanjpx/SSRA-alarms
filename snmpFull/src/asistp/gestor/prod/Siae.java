/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.gestor.prod;

import asistp.bean.BeanAlarms;
import asistp.dao.CoriantDAO;
import asistp.service.AbstractService;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Martin
 */
public class Siae extends AbstractService {

    private String from = "0.0.0.0";

    public Siae(String propertyName) {
        super("siae");

    }

    public void run(List<StringBuilder> lines, String ip,String path,String pathFileConfig) {
    	SimpleDateFormat sdf=new SimpleDateFormat("ddMMyyyy");
        String filename=sdf.format(new Date());
    	try {
			
		
        HashMap mapOids = this.parse2(lines);
        if(mapOids!=null){
        HashMap<String, String> match = this.match3(mapOids);
        
        BeanAlarms model = this.buildBeanAlarma(match, ip, lines);
        model.setTypeAlarma("SIAE");
        model.setNbi_sys("SNMP/SIAE");
        //new CoriantDAO().insetAlarms2(bean, from);
        CoriantDAO dao=new CoriantDAO();
        dao.pathFileConfig = pathFileConfig;
        synchronized (dao) {
        	dao.setModel_Alarms_Translator(model);
			dao.insetAlarms2(model, ip);
			dao.insetAlarmsOtherServer(model, from, lines);
			  String tablaname=dao.createTableMensual();
              long id=dao.getId(lines.toString(), "SNMP/SIAE");
              dao.writeFile(path, tablaname,"SIAE_"+filename , id+","+lines.toString());
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
        
        }
        
    	} catch (Exception e) {
			// TODO: handle exception
		}

    }

    public BeanAlarms buildBeanAlarma(HashMap<String, String> match, String ip, List<StringBuilder> listOIDS) {
        BeanAlarms bean = new BeanAlarms();
        bean.setSeverity("critical");
        bean.setInstance(null);
        bean.setEvent(null);
        bean.setEvent_date_start(new Timestamp(System.currentTimeMillis()));
        bean.setEvent_date_end(new Timestamp(System.currentTimeMillis()));
        bean.setEvent_description("disconnected");
        bean.setProbable_cause("disconnected");
        bean.setElement_identifier(match.get("device_name"));
        bean.setDevice_ip(match.get("device_ip"));
        bean.setAlarm_duration(null);
        bean.setAlarm_identifier(null);
        bean.setInstance(match.get("device_name"));
        bean.setEvent_detail("disconected");
        bean.setAdditional_information(ip + ", " + listOIDS);
        bean.setFault_function(null);
        bean.setFault_flag(null);
        String fault_flag=match.get("fault_flag");
        if(fault_flag.equals("3")){
            bean.setFault_flag("Active");
        }else if(fault_flag.equals("2")){
            bean.setFault_flag("Cleared");
        }
        
        bean.setSistema(null);
        bean.setStamp(new Timestamp(System.currentTimeMillis()));

        /*  String event_dateValue=match.get("event_date");
            if(event_dateValue.equals("3")){
                    bean.setEvent_date(new Date());
                
            }else{
                bean.setEvent_date(null);
            }
            //bean.setEvent_detail("disconected");
            bean.setProbable_cause(null);
            bean.setAdditional_information(null);
            bean.setFault_function("fault_function");
            String fault_flag=match.get("fault_flag");
            if(fault_flag.equals("3")){
                bean.setFault_flag("Active");
            }else if (fault_flag.equals("2")){
                bean.setFault_flag("Cleared");
            }else{
                bean.setFault_flag(null);
            }
            bean.setDevice_ip(null);
            bean.setAlarm_identifier(null);
            bean.setDevice_name(match.get("device_name"));*/
        return bean;

    }

}
