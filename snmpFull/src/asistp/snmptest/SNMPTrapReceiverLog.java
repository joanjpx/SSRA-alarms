/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.snmptest;

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

import java.util.Random;

/**
 * @author mchopker
 *
 */
public class SNMPTrapReceiverLog implements CommandResponder {

    private MultiThreadedMessageDispatcher dispatcher;
    private Snmp snmp = null;
    private Address listenAddress;
    private ThreadPool threadPool;
    private int n = 0;
    private long start = -1;
    private final Logger trapsLog = Logger.getLogger("traps");
    private final Logger incidentLog = Logger.getLogger("incident");

    public SNMPTrapReceiverLog() {
    }

    public static void main(String[] args) {
        new SNMPTrapReceiverLog().run();
    }

    private void run() {
        try {
            init();
            snmp.addCommandResponder(this);
        } catch (Exception ex) {
            incidentLog.error("Error: ", ex);
            //ex.printStackTrace();
        }
    }

    private void init() throws UnknownHostException, IOException {
        threadPool = ThreadPool.create("Trap", 10);
        dispatcher = new MultiThreadedMessageDispatcher(threadPool,
                new MessageDispatcherImpl());
        /*listenAddress = GenericAddress.parse(System.getProperty(
         "snmp4j.listenAddress", "udp:10.0.0.121/6666"));*/
        listenAddress = GenericAddress.parse(System.getProperty(
                "snmp4j.listenAddress", "udp:0.0.0.0/162"));
        TransportMapping transport;
        if (listenAddress instanceof UdpAddress) {
            transport = new DefaultUdpTransportMapping(
                    (UdpAddress) listenAddress);
        } else {
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
    }

    @Override
    public void processPdu(CommandResponderEvent event) {
        if (start < 0) {
            start = System.currentTimeMillis() - 1;
        }
        n++;
        if ((n % 100 == 1)) {
            double val = (n / (double) (System.currentTimeMillis() - start)) * 1000;
            /*System.out.println("Processed "
             + (n / (double) (System.currentTimeMillis() - start)) * 1000
             + "/s, total=" + n);*/
            System.out.println("Processed "
                    + val + "/s, total=" + n);
            trapsLog.info("Processed " + val + "/s, total=" + n);
        }
        Random rnd = new Random();
        int num = rnd.nextInt();
        List<StringBuilder> listMsg = new ArrayList();
        StringBuilder msgEvent = new StringBuilder();
        msgEvent.append(event.toString());
        //System.out.println("event = " + event.toString());
        List<? extends VariableBinding> varBinds = event.getPDU()
                .getVariableBindings();
        if (varBinds != null && !varBinds.isEmpty()) {
            Iterator<? extends VariableBinding> varIter = varBinds.iterator();
            while (varIter.hasNext()) {
                VariableBinding var = varIter.next();
                listMsg.add(new StringBuilder(var.toString()));
                //msg.append(var.toString()).append(";");
            }
        }
        //System.out.println("Message Received: " + msg.toString());
        //trapsLog.info("Message Received: " + msg.toString());
        System.out.println("msgEvent (" + num + ") = " + msgEvent);
        for (StringBuilder msg : listMsg) {
            System.out.println("msg (" + num + ")= " + msg);
        }
    }
}
