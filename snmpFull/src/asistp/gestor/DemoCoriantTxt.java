
package asistp.gestor;

import asistp.bean.BeanAlarms;
import asistp.dao.CoriantDAO;
import asistp.service.CoriantService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Usuario2
 */
public class DemoCoriantTxt {

    public static void main(String... args) {

        DemoCoriantTxt d = new DemoCoriantTxt();

        List<BeanAlarms> data = d.readFile("D:/traps-coriant.txt");

        CoriantDAO dao = new CoriantDAO();
        //dao.insetAlarms(data, "12.43.43.2");

    }

    public List<BeanAlarms> readFile(String file) {

        List<BeanAlarms> matches = new ArrayList<>();
        try (FileReader f = new FileReader(file);
                BufferedReader b = new BufferedReader(f);) {
            
            b.lines().forEach(str -> {
                matches.add(proccesOids(str));
            });

        } catch (Exception e) {
            //e.printStackTrace();
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

        CoriantService service = new CoriantService();

        return service.match(fromSnmp);

    }

}
