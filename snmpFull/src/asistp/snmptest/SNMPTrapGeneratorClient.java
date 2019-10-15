/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.snmptest;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;

/**
 * @author mchopker
 *
 */
public class SNMPTrapGeneratorClient {

    private static final String community = "public";
    private static final String trapOid = ".1.3.6.1.2.1.1.6";
    //private static final String ipAddress = "10.90.4.3";
    private static final String ipAddress = "127.0.0.1";
    //private static final String ipAddress = "10.0.0.121";
    //private static final String ipAddress = "190.81.47.198";
    private static final int port = 162;

    public static void main(String args[]) {
        sendSnmpV1V2Trap(SnmpConstants.version1);
        sendSnmpV1V2Trap(SnmpConstants.version2c);
        sendSnmpV3Trap();
    }

    /**
     * This methods sends the V1/V2 trap
     *
     * @param version
     */
    private static void sendSnmpV1V2Trap(int version) {
        // send trap
        sendV1orV2Trap(version, community, ipAddress, port);
    }

    private static PDU createPdu(int snmpVersion) {
        PDU pdu = DefaultPDUFactory.createPDU(snmpVersion);
        if (snmpVersion == SnmpConstants.version1) {
            pdu.setType(PDU.V1TRAP);
        } else {
            pdu.setType(PDU.TRAP);
        }
        pdu.add(new VariableBinding(SnmpConstants.sysUpTime));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(trapOid)));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress,
                new IpAddress(ipAddress)));
        pdu.add(new VariableBinding(new OID(trapOid), new OctetString("Major")));
        return pdu;
    }

    private static void sendV1orV2Trap(int snmpVersion, String community,
            String ipAddress, int port) {
        try {
            // create v1/v2 PDU
            PDU snmpPDU = createPdu(snmpVersion);

            // Create Transport Mapping
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();

            // Create Target
            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(new OctetString(community));
            comtarget.setVersion(snmpVersion);
            comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
            comtarget.setRetries(2);
            comtarget.setTimeout(5000);

            // Send the PDU
            Snmp snmp = new Snmp(transport);
            snmp.send(snmpPDU, comtarget);
            System.out.println("Sent Trap to (IP:Port)=> " + ipAddress + ":"
                    + port);
            snmp.close();
        } catch (Exception e) {
            System.err.println("Error in Sending Trap to (IP:Port)=> "
                    + ipAddress + ":" + port);
            System.err.println("Exception Message = " + e.getMessage());
        }
    }

    /**
     * Sends the v3 trap
     */
    private static void sendSnmpV3Trap() {
        try {
            Address targetAddress = GenericAddress.parse("udp:" + ipAddress
                    + "/" + port);
            TransportMapping transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(
                    MPv3.createLocalEngineID()), 0);
            SecurityModels.getInstance().addSecurityModel(usm);
            transport.listen();

            snmp.getUSM().addUser(
                    new OctetString("MD5DES"),
                    new UsmUser(new OctetString("MD5DES"), null, null, null,
                            null));

            // Create Target
            UserTarget target = new UserTarget();
            target.setAddress(targetAddress);
            target.setRetries(1);
            target.setTimeout(11500);
            target.setVersion(SnmpConstants.version3);
            target.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
            target.setSecurityName(new OctetString("MD5DES"));

            // Create PDU for V3
            ScopedPDU pdu = new ScopedPDU();
            pdu.setType(ScopedPDU.NOTIFICATION);
            pdu.add(new VariableBinding(SnmpConstants.sysUpTime));
            pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID,
                    SnmpConstants.linkDown));
            pdu.add(new VariableBinding(new OID(trapOid), new OctetString(
                    "Major")));

            // Send the PDU
            snmp.send(pdu, target);
            System.out.println("Sending Trap to (IP:Port)=> " + ipAddress + ":"
                    + port);
            snmp.addCommandResponder(new CommandResponder() {
                @Override
                public void processPdu(CommandResponderEvent arg0) {
                    System.out.println(arg0);
                }
            });
            snmp.close();
        } catch (Exception e) {
            System.err.println("Error in Sending Trap to (IP:Port)=> "
                    + ipAddress + ":" + port);
            System.err.println("Exception Message = " + e.getMessage());
        }
    }
}
