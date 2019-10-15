package com.asistp.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

import com.mysql.jdbc.Util;

public class Alarms {
	
	    private long id;
	    private String alaEventName;
	    private String severity;
	    private String instance;
	    private String event;
	    private Date dateStart;
	    private Date dateEnd;
	    private String eventDetail;
	    private String probableCause;
	    private String additionalInformation;
	    private String faultFunction;
	    private String faultFlag;
	    private String elementIp;
	    private String alarmIdentifier;
	    private String alarmDuration;
	    private String deviceName;
	    private String eventDescription;
	    private String elementIdentifier;
	    private String ipOrigin;
	    
	    
	    private String eventName;
	    private String type;
	    private String specificProblem;
	    private String affectedObjectFullName;
	    private String affectedObejectDisplayName;
	    private String alarmClassTag;
	    private String affectedObjectClassIndex;
	    private String Msg;
	    private String nbi_sys;
	    
	    
	    public String getMsg() {
			return Msg;
		}

		public void setMsg(String msg) {
			Msg = msg;
		}

		public String getIpOrigin() {
			return ipOrigin;
		}

		public void setIpOrigin(String ipOrigin) {
			this.ipOrigin = ipOrigin;
		}

		public long getId() {
			return id;
		}

	    public void setId(long id) {
			this.id = id;
		}

	    public String getSeverity() {
			return severity;
		}

		public void setSeverity(String severity) {
			this.severity = severity;
		}

		public String getInstance() {
			return instance;
		}

		public void setInstance(String instance) {
			this.instance = instance;
		}

		public String getEvent() {
			return event;
		}

		public void setEvent(String event) {
			this.event = event;
		}

		
		public Date getDateStart() {
			return dateStart;
		}

		public void setDateStart(Date dateStart) {
			this.dateStart = dateStart;
		}

		public Date getDateEnd() {
			return dateEnd;
		}

		public void setDateEnd(Date dateEnd) {
			this.dateEnd = dateEnd;
		}

		public String getEventDetail() {
			return eventDetail;
		}

		public void setEventDetail(String eventDetail) {
			this.eventDetail = eventDetail;
		}

		public String getProbableCause() {
			return probableCause;
		}

		public void setProbableCause(String probableCause) {
			this.probableCause = probableCause;
		}

		public String getAdditionalInformation() {
			return additionalInformation;
		}

		public void setAdditionalInformation(String additionalInformation) {
			this.additionalInformation = additionalInformation;
		}

		public String getFaultFunction() {
			return faultFunction;
		}

		public void setFaultFunction(String faultFunction) {
			this.faultFunction = faultFunction;
		}

		public String getFaultFlag() {
			return faultFlag;
		}

		public void setFaultFlag(String faultFlag) {
			this.faultFlag = faultFlag;
		}
		
		

		public String getNbi_sys() {
			return nbi_sys;
		}

		public void setNbi_sys(String nbi_sys) {
			this.nbi_sys = nbi_sys;
		}

		public String getElementIp() {
			return elementIp;
		}

		public void setelEmentIp(String elementIp) {
			this.elementIp = elementIp;
		}

		public String getAlarmIdentifier() {
			return alarmIdentifier;
		}

		public void setAlarmIdentifier(String alarmIdentifier) {
			this.alarmIdentifier = alarmIdentifier;
		}

		public String getAlarmDuration() {
			return alarmDuration;
		}

		public void setAlarmDuration(String alarmDuration) {
			this.alarmDuration = alarmDuration;
		}

		public String getDeviceName() {
			return deviceName;
		}

		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}

		public String getEventDescription() {
			return eventDescription;
		}

		public void setEventDescription(String eventDescription) {
			this.eventDescription = eventDescription;
		}

		public String getElementIdentifier() {
			return elementIdentifier;
		}

		public void setElementIdentifier(String elementIdentifier) {
			this.elementIdentifier = elementIdentifier;
		}
		
		public void persist(String path,String pathFileConfig) {
			 SimpleDateFormat sdf=new SimpleDateFormat("ddMMyyyy");
		        String filename=sdf.format(new Date());
			System.out.println("Carpeta para escribir-->" + path);
			AlarmsDao daoModel = new AlarmsDao();
			daoModel.pathFileConfig = pathFileConfig;
			daoModel.setModel_Alarms_Translator(this);
			daoModel.insetAlarms(this);
			daoModel.insetAlarmsOtherServer(this);
			try {
				String tablename=daoModel.createTableMensual();
				long id=daoModel.getId(this.additionalInformation,this.getNbi_sys() );
				daoModel.writeFile(path, tablename, "NOKIA_"+filename+".csv",id+this.additionalInformation );
				daoModel.insertTablaMensual(this, tablename);
				   if(this.getFaultFlag().equals("Active")) {
	                	daoModel.insetAlarmsOnlyCleared(this);
	                }else if(this.getFaultFlag().equals("Cleared")){
	                	int y=daoModel.updateActiveMensual(this, tablename);
	                	if(y==0) {
	                		System.out.println("Actualizacion Fallida para Active (Cleared Tabla Mensual) "+tablename);
	                	}else if (y==1) {
	                		System.out.println("Actualizacion Exitosa para Active (Cleared tabla Mensual) "+tablename);
	                	}
	                	
	                	int x=daoModel.updateActive(this);
	                	if(x==0) {
	                		System.out.println("Actualizacion Fallida para Active (Cleared Alarms2)");
	                	}else if (x==1) {
	                		System.out.println("Actualizacion Exitosa para Active (Cleared Alarms2)");
	                	}
	                	daoModel.updateAlarms(this);
	                	//prueba archivo
	                }
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			
		}
		public void persist2() {
			
			AlarmsDao daoModel = new AlarmsDao();
			//daoModel.setModel_Alarms_Translator(this);
			daoModel.insetJMSAlarms(this);
		}
		
		

		public String getEventName() {
			return eventName;
		}

		public void setEventName(String eventName) {
			this.eventName = eventName;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getSpecificProblem() {
			return specificProblem;
		}

		public void setSpecificProblem(String specificProblem) {
			this.specificProblem = specificProblem;
		}

		public String getAffectedObjectFullName() {
			return affectedObjectFullName;
		}

		public void setAffectedObjectFullName(String affectedObjectFullName) {
			this.affectedObjectFullName = affectedObjectFullName;
		}

		public String getAffectedObejectDisplayName() {
			return affectedObejectDisplayName;
		}

		public void setAffectedObejectDisplayName(String affectedObejectDisplayName) {
			this.affectedObejectDisplayName = affectedObejectDisplayName;
		}

		public String getAlarmClassTag() {
			return alarmClassTag;
		}

		public void setAlarmClassTag(String alarmClassTag) {
			this.alarmClassTag = alarmClassTag;
		}

		public String getAffectedObjectClassIndex() {
			return affectedObjectClassIndex;
		}

		public void setAffectedObjectClassIndex(String affectedObjectClassIndex) {
			this.affectedObjectClassIndex = affectedObjectClassIndex;
		}

		public void setElementIp(String elementIp) {
			this.elementIp = elementIp;
		}

		public String toString() {
	        StringBuilder strB = new StringBuilder();
	        
	      Stream.of( this.getClass().getDeclaredFields()).forEach( p -> {
	            try {
	                strB.append(p.getName());
	                strB.append("=");
	                strB.append(p.get(this));
	                strB.append("\n");
	            } catch (IllegalArgumentException ex) {
	                System.out.println(ex.getMessage());
	            } catch (IllegalAccessException ex) {
	                System.out.println(ex.getMessage());
	            }
	        });
	        
	        return strB.toString();
	        
	    }

		public String getAlaEventName() {
			return alaEventName;
		}

		public void setAlaEventName(String alaEventName) {
			this.alaEventName = alaEventName;
		}
		
		
		
		
}
