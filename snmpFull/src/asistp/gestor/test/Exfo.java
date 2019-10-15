package asistp.gestor.test;

import asistp.bean.BeanAlarms;
import asistp.service.ExfoService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Usuario2
 */
public class Exfo {
    
    private final Logger trapsLog = Logger.getLogger("traps");

    public static void main(String... args) {

        (new Exfo()).runTest();

    }

    public List<BeanAlarms> readFile(String file) {

        List<BeanAlarms> matches = new ArrayList<>();
        List<StringBuilder> stringBuilder = new ArrayList<>();
        try (FileReader f = new FileReader(file);
                BufferedReader b = new BufferedReader(f);) {
            b.lines().forEach(str -> {
            stringBuilder.add(new StringBuilder(str));
            });
            
            matches = match(stringBuilder);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return matches;

    }

    public BeanAlarms proccesOids(String str) {

        StringBuilder strBuilder = new StringBuilder(str);
        int pos = strBuilder.indexOf("securityLevel");

        strBuilder.delete(0, pos + 1);
        int pos2 = strBuilder.indexOf("]");
        strBuilder.delete(0, pos2 + 1);

        String[] split = strBuilder.toString().split(";");
        HashMap<String, String> fromSnmp = new HashMap<>();

        for (String keyVal : split) {

            int pos3 = keyVal.indexOf("=");
            if (pos3 > -1) {
                fromSnmp.put(keyVal.substring(0, pos3).trim(), keyVal.substring(pos3 + 1, keyVal.length()).trim());
            }

        }
        //System.exit(0);
        ExfoService service = new ExfoService("e");
        return service.match(fromSnmp);

    }
    
    public void runTest () {
        
        Exfo d = new Exfo();

        List<BeanAlarms> data = d.readFile("D:/proy-gilat/traps/traps-exfo.txt");
        data.forEach(e -> {
            //System.out.println(e);
        });

//        CoriantDAO dao = new CoriantDAO();
//        dao.insetAlarms(data, "10.21.27.146");
    }
    
    public void run(List<StringBuilder> lines) {
        List<BeanAlarms> models = match(lines);
        
        models.forEach(b -> {
            //trapsLog.info(b);
            //System.out.println(b);
            
        });
        
    }
    
    public List<BeanAlarms> match(List<StringBuilder> lines) {
        List<BeanAlarms> matches = new ArrayList<>();
        lines.forEach(str -> {
            matches.add(proccesOids(str.toString()));
            });
        
        return matches;
    }

}
