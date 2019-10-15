package asistp.gestor.prod;

import asistp.bean.BeanAlarms;
import asistp.dao.CoriantDAO;
import asistp.service.ExfoService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Usuario2
 */
public class Exfo {

    private String from = "0.0.0.0";

    private final Logger trapsLog = Logger.getLogger("traps");

    public static void main(String... args) {

        (new Exfo()).runTest();

    }

    public Exfo() {
    }

    public Exfo(String ip) {
        from = ip;
    }

    /**
     * Test
     * @param file
     * @return 
     */
    public List<BeanAlarms> readFile(String file) {

        List<BeanAlarms> matches = new ArrayList<>();
        List<StringBuilder> stringBuilder = new ArrayList<>();
        try (FileReader f = new FileReader(file);
                BufferedReader b = new BufferedReader(f);) {
            b.lines().forEach(str -> {
                stringBuilder.add(new StringBuilder(str));
            });

            //matches = match(stringBuilder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return matches;

    }

    /**
     * Test
     */
    public void runTest() {

        Exfo d = new Exfo();

        List<BeanAlarms> data = d.readFile("D:/proy-gilat/traps/traps-exfo.txt");
        data.forEach(e -> {
            //System.out.println(e);
        });

//        CoriantDAO dao = new CoriantDAO();
//        dao.insetAlarms(data, "10.21.27.146");
    }

    /**
     * Prod
     * @param lines 
     */
    public void run(List<StringBuilder> lines,String path,String pathFileConfig) {
    	   SimpleDateFormat sdf=new SimpleDateFormat("ddMMyyyy");
           String filename=sdf.format(new Date());
    	try {
        BeanAlarms model = match(lines,"EXFO");
        model.setTypeAlarma("EXFO");
        model.setNbi_sys("SNMP/EXFO");
        /**List<BeanAlarms> matches = new ArrayList<>();
        matches.add(models);*/
        CoriantDAO dao=new CoriantDAO();
        dao.pathFileConfig = pathFileConfig;
        synchronized (dao) {
        	dao.setModel_Alarms_Translator(model);
        	dao.insetAlarms(model, from, lines);
        	dao.insetAlarmsOtherServer(model, from, lines);
        	
			String tablaname=dao.createTableMensual();
			long id=dao.getId(lines.toString(), "SNMP/EXFO");
			dao.insetAlarmsMensual(model, from, lines, tablaname);
			dao.writeFile(path, tablaname,"EXFO_"+filename , id+","+lines.toString());
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
        
    	} catch (Exception e) {
			// TODO: handle exception
		}
        //new CoriantDAO().insetAlarms(model, from,lines);
        // base de datos
        //models.forEach(b -> {
        //trapsLog.info(models);
        //System.out.println(b);

        //});
        
        
    }

    /**
     * usar el método estático de Abstract service en su lugar para otros rtu 
     * Solo poara exfo [workaround]
     * @param lines
     * @return 
     */
    public BeanAlarms match(List<StringBuilder> lines,String TypeAlarma) {
        //List<BeanAlarms> matches = new ArrayList<>();
        HashMap<String, String> fromSnmp = new HashMap<>();
           SimpleDateFormat sdf=new SimpleDateFormat("ddMMyyyy");
        String date=sdf.format(new Date());
        try {
            
        
         FileWriter fw=new FileWriter(date+"_"+TypeAlarma+".txt", true);
              PrintWriter pw=new PrintWriter(fw);
               pw.println("---------------------------------------");
        lines.forEach(str -> {
             //pw.println(str);
            String[] part = str.toString().split("=");
            
            if (part.length > 1) {
                //System.out.println("'" + part[0].trim() + "'=" + part[1].trim());
                fromSnmp.put(part[0].trim(), part[1].trim());
            }
            //matches.add(proccesOids(str.toString()));
        }) ;
        fw.close();    
        pw.close();
        } catch (Exception e) {
        }
        

        return (new ExfoService(from)).match(fromSnmp);
    }

}
