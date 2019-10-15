/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.snmp;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.snmp4j.smi.OID;

/**
 *
 * @author Usuario
 */
public class TestGet {

    private static final Logger log = Logger.getLogger("getsnmp");

    public static void main(String[] args) {
        try {
            String ip = "192.168.2.241";
            String port = "161";
            String comunity = "public";
            SNMPGet client = new SNMPGet(ip, port, comunity);
            client.start();
            List<String> list = new ArrayList();
            list.add("1.3.6.1.4.1.2162.4.4.1");
            list.add("1.3.6.1.4.1.2162.4.4.2");
            list.add("1.3.6.1.4.1.2162.4.4.3");
            list.add("1.3.6.1.4.1.2162.4.4.4.1.1");
            list.add("1.3.6.1.4.1.2162.4.4.4.1.2");
            list.add("1.3.6.1.4.1.2162.4.4.4.1.3");
            list.add("1.3.6.1.4.1.2162.4.4.4.1.4");
            list.add("1.3.6.1.4.1.2162.4.4.4.1.5");
            list.add("1.3.6.1.4.1.2162.4.4.4.1.6");
            list.add("1.3.6.1.4.1.2162.4.4.4.1.7");
            list.add("1.3.6.1.4.1.2162.4.4.4.1.8");
            list.add("1.3.6.1.4.1.2162.4.4.4");
            list.add("1.3.6.1.4.1.2162.4.4.4.1");
            list.add("1.3.6.1.4.1.2162.4.4.4.1.9");
            list.add("1.3.6.1.4.1.2162.4.4.5.1.1");
            list.add("1.3.6.1.4.1.2162.4.4.5.1.2");
            list.add("1.3.6.1.4.1.2162.4.4.5.1.3");
            list.add("1.3.6.1.4.1.2162.4.4.5.1.4");
            list.add("1.3.6.1.4.1.2162.4.4.5.1.5");
            list.add("1.3.6.1.4.1.2162.4.4.5.1.6");
            list.add("1.3.6.1.4.1.2162.4.4.5");
            list.add("1.3.6.1.4.1.2162.4.4.5.1");
            list.add("1.3.6.1.4.1.2162.4.4.6.2.1.1");
            list.add("1.3.6.1.4.1.2162.4.4.6.2.1.2");
            list.add("1.3.6.1.4.1.2162.4.4.6.2.1.3");
            list.add("1.3.6.1.4.1.2162.4.4.6.2.1.4");
            list.add("1.3.6.1.4.1.2162.4.4.6.2");
            list.add("1.3.6.1.4.1.2162.4.4.6.2.1");
            list.add("1.3.6.1.4.1.2162.4.10.3.1.1.1");
            list.add("1.3.6.1.4.1.2162.4.10.3.1.1.2");
            list.add("1.3.6.1.4.1.2162.4.10.3.1");
            list.add("1.3.6.1.4.1.2162.4.10.3.1.1");
            for (String str : list) {
                //String sysDescr = client.getAsString(new OID(".1.3.6.1.2.1.1.1.0"));
                String sysDescr = client.getAsString(new OID("."+str));
                //System.out.println(sysDescr);
                log.info("\n" + sysDescr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
