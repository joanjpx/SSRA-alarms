package com.asistp.jms;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.asistp.model.Alarms;
import com.asistp.model.AlarmsDao;

public class Application {
	private static final Logger loggerExceptions = LogManager.getLogger("Exceptions");

	public void run(String response, Map<String, String[]> codes,String path,String pathFileConfg) {
		try {
			// System.out.println(response);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(response.getBytes("utf-8"))));
			doc.getDocumentElement().normalize();

			Alarms alarm = new Alarms();

			NodeList eventName = doc.getElementsByTagName("eventName");
			NodeList severityNode = doc.getElementsByTagName("severity");
			NodeList instanceNode = doc.getElementsByTagName("applicationDomain");
			NodeList eventNode = doc.getElementsByTagName("alarmName");
			NodeList type = doc.getElementsByTagName("type");
			NodeList specificProblem = doc.getElementsByTagName("specificProblem");
			NodeList affectedObjectFullName = doc.getElementsByTagName("affectedObjectFullName");
			NodeList dateStartNode = doc.getElementsByTagName("firstTimeDetected");
			NodeList dateEndNode = doc.getElementsByTagName("lastTimeCleared");
			NodeList nodeName = doc.getElementsByTagName("nodeName");
			NodeList elementIpTag = doc.getElementsByTagName("nodeId");
			NodeList affectedObejectDisplayName = doc.getElementsByTagName("affectedObjectDisplayedName");
			NodeList alarmClassTag = doc.getElementsByTagName("alarmClassTag");
			NodeList affectedObjectClassIndex = doc.getElementsByTagName("affectedObjectClassIndex");
			NodeList displayName = doc.getElementsByTagName("displayedName");
			NodeList ala_eventName = doc.getElementsByTagName("ALA_eventName");

			if (severityNode == null) {
				throw new IllegalArgumentException("Malformed");
			}
			String alarmCode = eventNode.item(0).getTextContent();
			String[] currentCode = codes.get(alarmCode);

			if (currentCode == null || currentCode.length < 3) {
				return;
			}
			if (severityNode.item(0).getTextContent().equalsIgnoreCase("cleared")
					&& eventName.item(0).getTextContent().equals("ObjectDeletion")
					&& alarmClassTag.item(0).getTextContent().equals("equipment.LinkDown")) {

				alarm.setFaultFlag("Cleared");
			} else if (severityNode.item(0).getTextContent().equalsIgnoreCase("major")
					&& eventName.item(0).getTextContent().equals("ObjectCreation")
					&& alarmClassTag.item(0).getTextContent().equals("equipment.LinkDown")) {
				alarm.setFaultFlag("Active");
			} else {
				alarm.setFaultFlag(null);
			}

			if (severityNode.item(0).getTextContent().equals("cleared")) {
				alarm.setSeverity("critical");
			} else if (severityNode.item(0).getTextContent().equals("info")) {
				alarm.setSeverity("warning");
			}else {
				alarm.setSeverity(severityNode.item(0).getTextContent());
			}
			alarm.setInstance(type.item(0).getTextContent());
			alarm.setEvent(currentCode[0]);

			if (ala_eventName.item(0).getTextContent().trim().equals("ObjectCreation")) {
				alarm.setDateStart(new Date(Long.parseLong(dateStartNode.item(0).getTextContent())));

			} else if (ala_eventName.item(0).getTextContent().trim().equals("ObjectDeletion")) {
				alarm.setDateStart(new Date(Long.parseLong(dateEndNode.item(0).getTextContent())));
			} else {
				alarm.setDateStart(new Date(Long.parseLong(dateStartNode.item(0).getTextContent())));
				alarm.setDateEnd(!dateEndNode.item(0).getTextContent().equals("0")
						? new Date(Long.parseLong(dateEndNode.item(0).getTextContent()))
						: null);
			}

			// alarm.setDateEnd(!dateEndNode.item(0).getTextContent().equals("0")?new
			// Date(Long.parseLong(dateEndNode.item(0).getTextContent())):null);
			alarm.setEventDetail(currentCode[2]);
			alarm.setElementIdentifier(nodeName.item(0).getTextContent());
			alarm.setProbableCause(currentCode[1]);
			alarm.setAdditionalInformation(response);
			alarm.setelEmentIp(elementIpTag.item(0).getTextContent());
			alarm.setEventDescription(currentCode[2]);

			alarm.setEventName(eventNode.item(0).getTextContent());
			alarm.setType(type.item(0).getTextContent());
			alarm.setSpecificProblem(specificProblem.item(0).getTextContent());
			alarm.setAffectedObjectFullName(affectedObjectFullName.item(0).getTextContent());
			alarm.setAffectedObejectDisplayName(affectedObejectDisplayName.item(0).getTextContent());
			alarm.setAlarmClassTag(alarmClassTag.item(0).getTextContent());
			alarm.setAffectedObjectClassIndex(affectedObjectClassIndex.item(0).getTextContent());
			alarm.setAlarmIdentifier(affectedObejectDisplayName.item(0).getTextContent());

			alarm.setMsg(response);
			alarm.setNbi_sys("JMS/NOKIA");
			// System.out.println(alarm.getEventName()+"----"+alarm.getElementIdentifier() +
			// "------" + alarm.getElementIp()+ "++++" +
			// alarm.getIpOrigin()+"-->"+alarm.getSeverity());
			System.out.println("Valor AffectObbjectDisplayName ->"+alarm.getAffectedObejectDisplayName());
			AlarmsDao.writeAlarmsJmsFileforDay(response);
			if(alarm.getAffectedObejectDisplayName().substring(0,4).equals("Port")) {
			alarm.persist(path,pathFileConfg);
			}
			// System.out.println(ala_eventName.item(0).getTextContent()+"---------"+eventNode.item(0).getTextContent()+"----"+severityNode.item(0).getTextContent());
			if (ala_eventName.item(0).getTextContent().trim().equals("ObjectCreation")
					|| ala_eventName.item(0).getTextContent().trim().equals("ObjectDeletion")) {
				if (eventNode.item(0).getTextContent().trim().equals("12")) {
					if (severityNode.item(0).getTextContent().trim().equals("cleared")) {
						// System.out.println("lastTimeCleared : "+
						// dateEndNode.item(0).getTextContent())
						alarm.setDateEnd(!dateEndNode.item(0).getTextContent().equals("0")
								? new Date(Long.parseLong(dateEndNode.item(0).getTextContent()))
								: null);
						alarm.persist2();

					} else if (severityNode.item(0).getTextContent().trim().equals("critical")) {
						alarm.setDateEnd(new Date(Long.parseLong(dateStartNode.item(0).getTextContent())));
						alarm.persist2();
					}
				}
			}
			// System.out.println(alarm);
		} catch (Exception e) {
			loggerExceptions.trace(e.getMessage());
		}
	}

}
