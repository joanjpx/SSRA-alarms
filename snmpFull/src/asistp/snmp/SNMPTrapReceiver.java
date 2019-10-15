/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.snmp;

import asistp.gestor.prod.Eltek;
import asistp.gestor.prod.Exfo;
import asistp.gestor.prod.Siae;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;
//import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

//import java.util.Random;
/**
 * @author mchopker
 *
 */
public class SNMPTrapReceiver implements CommandResponder {

    private MultiThreadedMessageDispatcher dispatcher;
    private Snmp snmp = null;
    private Address listenAddress;
    private ThreadPool threadPool;
    private int n = 0;
    private long start = -1;
    private final Logger trapsLog = Logger.getLogger("traps");
    private final Logger incidentLog = Logger.getLogger("incident");
    private String path;
    private String pathFileConfig;

    public SNMPTrapReceiver() {

//        trapsLog.info("Iniciando");
    }

    public static void main(String[] args) {

        if (args.length < 3) {
            Logger.getLogger("traps").info("Ningún parámetro pasado");
            //System.err.println("Nothing to do");
            System.exit(0);
        }

        new SNMPTrapReceiver().run(args[0],args[1],args[2]);
    }

    private void run(String ip,String path,String pathFileConfig) {
        try {
            init(ip);
            this.path=path;
            this.pathFileConfig = pathFileConfig;
            snmp.addCommandResponder(this);
        } catch (Exception ex) {
            incidentLog.error("Error: ", ex);
            //ex.printStackTrace();
        }
    }

    private void init(String ip) throws UnknownHostException, IOException {
        threadPool = ThreadPool.create("Trap", 10);
        dispatcher = new MultiThreadedMessageDispatcher(threadPool,
                new MessageDispatcherImpl());
        /*listenAddress = GenericAddress.parse(System.getProperty(
         //"snmp4j.listenAddress", "udp:10.90.4.3/162"));
         "snmp4j.listenAddress", "udp:192.168.1.100/163"));*/
        System.out.println("Listen to: " + ip);
        //trapsLog.info("udp:" + ip + "/162");
        listenAddress = GenericAddress.parse("udp:" + ip + "/162");
        TransportMapping transport;
        if (listenAddress instanceof UdpAddress) {
            //trapsLog.info("UDP");
            transport = new DefaultUdpTransportMapping(
                    (UdpAddress) listenAddress);

        } else {
            //trapsLog.info("TCP");
            transport = new DefaultTcpTransportMapping(
                    (TcpAddress) listenAddress);
        }

        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(
                MPv3.createLocalEngineID()), 0);

        usm.setEngineDiscoveryEnabled(true);

        snmp = new Snmp(dispatcher, transport);
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3(usm));
        SecurityModels.getInstance().addSecurityModel(usm);

        snmp.getUSM().addUser(
                new OctetString("MD5DES"),
                new UsmUser(new OctetString("MD5DES"), AuthMD5.ID,
                        new OctetString("UserName"), PrivDES.ID,
                        new OctetString("PasswordUser")));

        snmp.getUSM().addUser(new OctetString("MD5DES"),
                new UsmUser(new OctetString("MD5DES"), null, null, null, null));

        snmp.listen();
        //trapsLog.info("Conectado y escuchando");
    }

    @Override
    public void processPdu(CommandResponderEvent event) {
        /*if (start < 0) {
         start = System.currentTimeMillis() - 1;
         }
         n++;
         if ((n % 100 == 1)) {
         double val = (n / (double) (System.currentTimeMillis() - start)) * 1000;
         System.out.println("Processed " + (n / (double) (System.currentTimeMillis() - start)) * 1000 + "/s, total=" + n);
         System.out.println("Processed " + val + "/s, total=" + n);
         trapsLog.info("Processed " + val + "/s, total=" + n);
         }*/
        //Random rnd = new Random();
        //int num = rnd.nextInt();
        List<StringBuilder> listMsg = new ArrayList();
        StringBuilder msgEvent = new StringBuilder();
        msgEvent.append(event.toString());
        StringBuilder msg = new StringBuilder();
        msg.append(event.toString());
        //trapsLog.info("event = " + event.toString());
        //trapsLog.info("\n");
        //trapsLog.info("from = " + event.getPeerAddress().toString());
        List<? extends VariableBinding> varBinds = event.getPDU()
                .getVariableBindings();
        if (varBinds != null && !varBinds.isEmpty()) {
            Iterator<? extends VariableBinding> varIter = varBinds.iterator();
            while (varIter.hasNext()) {
                VariableBinding var = varIter.next();
                listMsg.add(new StringBuilder(var.toString()));
                //System.out.println(var.toString());
                msg.append("\n").append(var.toString()).append(";");
            }
        }
        String[] parts = event.getPeerAddress().toString().split("/");
        String ip = parts[0];
 //System.out.println("--------->>>>>" + ip);
      //System.out.println(ip);
      
      
      // HUANCAVELICA
    
       
        /*if ("10.11.5.210".equals(ip)
                || "10.11.8.146".equals(ip)
                || "10.11.25.146".equals(ip)
                || "10.11.22.146".equals(ip)
                || "10.11.18.82".equals(ip)
                
                ) {
            (new Exfo(event.getPeerAddress().toString())).run(listMsg);

        } else if ("10.255.12.121".equals(ip)) {
         (new Eltek(event.getPeerAddress().toString())).run(listMsg);
         }*/
        
        
        //APURIMAC
       /*  if ("10.31.13.146".equals(ip)
                || "10.31.20.18".equals(ip)
                || "10.31.6.82".equals(ip)
                ) {
            (new Exfo(event.getPeerAddress().toString())).run(listMsg);

        } else if ("10.255.32.121".equals(ip)) {
         (new Eltek(event.getPeerAddress().toString())).run(listMsg);
         }*/
       
       //CUSCO
       
        if ("10.11.5.210".equals(ip)//huancavelica smrt
                || "10.11.8.146".equals(ip)//huancavelica smrt
                || "10.11.25.146".equals(ip)//huancavelica  smrt
                || "10.11.22.146".equals(ip)//huancavelica  smrt
                || "10.11.18.82".equals(ip)//huancavelica   smrt
                
                || "10.21.29.82".equals(ip) //ayacucho smrt
                || "10.21.16.82".equals(ip) //ayacucho smrt
                || "10.21.20.82".equals(ip) //ayacucho smrt
                || "10.21.17.82".equals(ip) //ayacucho smrt
                || "10.21.17.146".equals(ip)//ayaucho smrt
                || "10.21.7.210".equals(ip) //ayaucho smrt
                || "10.21.13.210".equals(ip)//ayacucho smrt
                
                || "10.31.13.146".equals(ip) //apurimac smrt
                || "10.31.20.18".equals(ip) //apurimac smrt
                || "10.31.6.82".equals(ip) //apurimac smrt
                
                || "10.41.19.146".equals(ip) //cusco smrt
                || "10.41.20.146".equals(ip) //cusco smrt
                || "10.41.15.146".equals(ip) //cusco smrt
                || "10.41.9.18".equals(ip)//cusco smrt
                || "10.41.25.210".equals(ip)//cusco smrt
                || "10.41.13.18".equals(ip)//cusco smrt
                || "10.41.9.82".equals(ip)//cusco smrt

                ) {
        	
            (new Exfo(event.getPeerAddress().toString())).run(listMsg,path,pathFileConfig);

        } else if ("10.254.12.141".equals(ip)  //"10.254.12.141" "10.254.22.141" "10.254.32.141" "10.254.42.141"  ssra
                ||"10.254.22.141".equals(ip) //10.255.12.121,10.255.22.121,10.255.32.121,10.255.42.121  smrt
                ||"10.254.32.141".equals(ip)
                ||"10.254.42.141".equals(ip)
                ||"10.255.12.121".equals(ip)
                ||"10.255.22.121".equals(ip)
                ||"10.255.32.121".equals(ip)
                ||"10.255.42.121".equals(ip)
                ) {
        	
         (new Eltek(event.getPeerAddress().toString())).run(listMsg,path,pathFileConfig);
         } /*else {
         //trapsLog.info("NO IP: " + ip);
         }*/
        
        else if ("10.254.12.119".equals(ip)|| "10.254.22.119".equals(ip) ||"10.254.32.119".equals(ip)||"10.254.42.119".equals(ip)
                ){
        	
        	
            
            (new Siae(event.getPeerAddress().toString())).run(listMsg,ip,path,pathFileConfig);
        }
        
        /*else {
         //trapsLog.info("NO IP: " + ip);
         }*/

    }
}
