package com.asistp.model;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.fabric.xmlrpc.base.Data;

public class AlarmsDao extends MYSQLConnect {

	private static Logger logger = LogManager.getLogger(AlarmsDao.class);

	public void insetAlarms(Alarms alarm) {
		System.out.println("Insertando Alarma Nokia  " + new Date());
		String insertTableSQL = "INSERT INTO alarms "
				+ "(instance, severity, event_date_start, event_date_end, fault_flag, "
				+ "event_detail, probable_cause, fault_function, alarm_duration, ip_origin, additional_information,"
				+ " event_description, element_identifier, element_ip, alarm_identifier, stamp,sistema,ciudad,nbi_sys)"
				+ " VALUES " + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try (java.sql.Connection cn = getConexion(null); PreparedStatement ps = cn.prepareStatement(insertTableSQL);) {

			Timestamp dEnd = (alarm.getDateEnd() != null) ? new Timestamp(alarm.getDateEnd().getTime()) : null;
			Timestamp d = (alarm.getDateStart() != null) ? new Timestamp(alarm.getDateStart().getTime()) : null;

			ps.setString(1, alarm.getInstance());
			ps.setString(2, alarm.getSeverity());
			ps.setTimestamp(3, d);
			ps.setTimestamp(4, dEnd);
			ps.setString(5, alarm.getFaultFlag());
			ps.setString(6, alarm.getEventDetail());
			ps.setString(7, alarm.getProbableCause());
			ps.setString(8, alarm.getFaultFunction());
			ps.setString(9, alarm.getAlarmDuration());
			ps.setString(10, alarm.getIpOrigin());
			ps.setString(11, alarm.getAdditionalInformation());
			ps.setString(12, alarm.getEventDescription());
			ps.setString(13, alarm.getElementIdentifier());
			ps.setString(14, alarm.getElementIp());
			ps.setString(15, alarm.getAlarmIdentifier());
			///////////// Elements/////////
			int platforms = 0;
			int locationId = 0;
			String descriptionSolutions = null;
			String descriptionPlatforms = null;
			String ubigeoLocations = null;
			String nameLocalities = null;

			try {
				Object[] elementsValues = getValue(new String[] { "platforms", "location_id" }, "elements",
						"identifier='" + alarm.getElementIdentifier() + "'");
				platforms = (int) elementsValues[0];
				locationId = (int) elementsValues[1];
				//System.out.println("-----Elements---->" + platforms + "-----" + locationId);
				//////// Platform/////////////

				int solutionIdPlatforms = 0;
				Object[] platformsValues = getValue(new String[] { "description", "solution_id" }, "platforms",
						"id=" + platforms);
				descriptionPlatforms = (String) platformsValues[0];
				solutionIdPlatforms = (int) platformsValues[1];
				// //System.out.println("----Platforms----->"+descriptionPlatforms
				// +"-----"+solutionIdPlatforms);
				///////// Solutions//////////////

				Object[] solutionsValues = getValue(new String[] { "description" }, "solutions",
						"id=" + solutionIdPlatforms);
				descriptionSolutions = (String) solutionsValues[0];
				// //System.out.println("----Solutions----->"+descriptionSolutions);
				/////// locations//////////
				int locatiesidLocations = 0;

				Object[] locationsValues = getValue(new String[] { "localitie_id", "ubigeo" }, "locations",
						"id=" + locationId);
				locatiesidLocations = (int) locationsValues[0];
				ubigeoLocations = (String) locationsValues[1];

				//System.out.println("----locations----->" + locatiesidLocations + "-----" + ubigeoLocations);
				////////////////////////// Localities

				Object[] localitiesValues = getValue(new String[] { "name" }, "localities",
						"id=" + locatiesidLocations);
				nameLocalities = (String) localitiesValues[0];

				//System.out.println("----localities----->" + nameLocalities);

			} catch (Exception e) {
				//System.out.println("Registro no encontrado  y guarndalo como null");
			}

			ps.setTimestamp(16, new Timestamp(System.currentTimeMillis()));
			ps.setString(17, "[" + descriptionPlatforms + "] - [" + descriptionSolutions + "]");
			ps.setString(18, "[" + ubigeoLocations + "] - [" + nameLocalities + "]");
			ps.setString(19, alarm.getNbi_sys());
			ps.addBatch();

			ps.execute();
			ps.close();
			cn.close();

		} catch (Exception e) {
			
			e.printStackTrace();
			logger.warn("Error SQL: " + e.getMessage());
		}

	}
	public String createTableMensual() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		String SdateTitle = sdf.format(new Date());
		ResultSet rs = null;
		String sqlCreateTable = "CREATE TABLE zAlarms" + SdateTitle + " (" 
				+ "id int primary key AUTO_INCREMENT, "
				+ "severity varchar(100), " 
				+ "event_date_start timestamp, " 
				+ "event_date_end timestamp, "
				+ "event_description varchar(255), "
				+ "element_identifier varchar(150), "
				+ "fault_flag varchar(20), "
				+ "stamp timestamp," + "nbi_sys varchar(15), " 
				+ "solution varchar(100), "
				+ "locality varchar(100)"
				+ ")";
		try (Connection cn = getConexion(null);) {
			java.sql.DatabaseMetaData dbm = cn.getMetaData();
			rs = dbm.getTables(null, null, "zAlarms" + SdateTitle, null);
			if (rs.next()) {
				System.out.println("la Tabla zAlarms" + SdateTitle + " <---Existe");

			} else {
				System.out.println("la Tabla zAlarms" + SdateTitle + " <---No Existe");
				System.out.println("Creando Tabla zAlarms" + SdateTitle);

				java.sql.Statement st = cn.createStatement();
				st.execute(sqlCreateTable);

			}

			rs.close();

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}

		}

		return "zAlarms"+SdateTitle;

	}


	public void insetJMSAlarms(Alarms alarm) {

		String insertTableSQL = "INSERT INTO JMSAlarms "
				+ " (eventName, severity,type,specificProblem,affectedObjectFullName,firsTimeDetected,lastTimeDetected,nodeId,nodeName,"
				+ " affectedObjectDisplayedName,alarmClassTag,affectedObjectClassIndex,fullMsg)" + " VALUES "
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try (java.sql.Connection cn = getConexion(null); PreparedStatement ps = cn.prepareStatement(insertTableSQL);) {

			Timestamp dEnd = (alarm.getDateEnd() != null) ? new Timestamp(alarm.getDateEnd().getTime()) : null;
			Timestamp d = (alarm.getDateStart() != null) ? new Timestamp(alarm.getDateStart().getTime()) : null;

			ps.setString(1, alarm.getEventName());
			ps.setString(2, alarm.getSeverity());
			ps.setString(3, alarm.getType());
			ps.setString(4, alarm.getSpecificProblem());
			ps.setString(5, alarm.getAffectedObjectFullName());
			ps.setTimestamp(6, d);
			ps.setTimestamp(7, dEnd);
			ps.setString(8, alarm.getElementIp());
			ps.setString(9, alarm.getElementIdentifier());
			ps.setString(10, alarm.getAffectedObejectDisplayName());
			ps.setString(11, alarm.getAlarmClassTag());
			ps.setString(12, alarm.getAffectedObjectClassIndex());
			ps.setString(13, alarm.getMsg());

			ps.addBatch();

			ps.execute();
			ps.close();
			cn.close();

		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("Error SQL: " + e.getMessage());
		}

	}

	public Object[] getValue(String[] columns, String table, String where) {
		String col = "";
		for (int x = 0; x < columns.length; x++) {
			col += columns[x];
			if (x < columns.length - 1) {
				col += ",";
			}
		}
		String sql = "select " + col + " from " + table + " where " + where;
		//System.out.println(sql);
		Object r1 = null;
		Object r2 = null;
		try (java.sql.Connection cn = getConexion(null); ResultSet rs = cn.createStatement().executeQuery(sql);) {
			while (rs.next()) {
				if (columns.length == 1) {
					r1 = rs.getObject(1);
				} else {
					r1 = rs.getObject(1);
					r2 = rs.getObject(2);

				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return new Object[] { r1, r2 };
	}
	
	public void insertTablaMensual(Alarms alarm,String tablename) {
		String insertTableSQL = "INSERT INTO "+tablename
				+ " (severity, event_date_start, event_date_end, event_description, element_identifier, "
				+ " fault_flag, stamp, nbi_sys, solution, locality)"
				+ " VALUES " + "(?,?,?,?,?,?,?,?,?,?)";

		try (java.sql.Connection cn = getConexion(null); PreparedStatement ps = cn.prepareStatement(insertTableSQL);) {

			Timestamp dEnd = (alarm.getDateEnd() != null) ? new Timestamp(alarm.getDateEnd().getTime()) : null;
			Timestamp d = (alarm.getDateStart() != null) ? new Timestamp(alarm.getDateStart().getTime()) : null;

			//ps.setString(1, alarm.getInstance());
		
			//ps.setString(5, alarm.getFaultFlag());
			//ps.setString(6, alarm.getEventDetail());
			//ps.setString(7, alarm.getProbableCause());
			//ps.setString(8, alarm.getFaultFunction());
			//ps.setString(9, alarm.getAlarmDuration());
			//ps.setString(10, alarm.getIpOrigin());
			//ps.setString(11, alarm.getAdditionalInformation());
			//ps.setString(12, alarm.getEventDescription());
			//ps.setString(13, alarm.getElementIdentifier());
			//ps.setString(14, alarm.getElementIp());
			//ps.setString(15, alarm.getAlarmIdentifier());
			///////////// Elements/////////
			int platforms = 0;
			int locationId = 0;
			String descriptionSolutions = null;
			String descriptionPlatforms = null;
			String ubigeoLocations = null;
			String nameLocalities = null;

			try {
				Object[] elementsValues = getValue(new String[] { "platforms", "location_id" }, "elements",
						"identifier='" + alarm.getElementIdentifier() + "'");
				platforms = (int) elementsValues[0];
				locationId = (int) elementsValues[1];
				//System.out.println("-----Elements---->" + platforms + "-----" + locationId);
				//////// Platform/////////////

				int solutionIdPlatforms = 0;
				Object[] platformsValues = getValue(new String[] { "description", "solution_id" }, "platforms",
						"id=" + platforms);
				descriptionPlatforms = (String) platformsValues[0];
				solutionIdPlatforms = (int) platformsValues[1];
				// //System.out.println("----Platforms----->"+descriptionPlatforms
				// +"-----"+solutionIdPlatforms);
				///////// Solutions//////////////

				Object[] solutionsValues = getValue(new String[] { "description" }, "solutions",
						"id=" + solutionIdPlatforms);
				descriptionSolutions = (String) solutionsValues[0];
				// //System.out.println("----Solutions----->"+descriptionSolutions);
				/////// locations//////////
				int locatiesidLocations = 0;

				Object[] locationsValues = getValue(new String[] { "localitie_id", "ubigeo" }, "locations",
						"id=" + locationId);
				locatiesidLocations = (int) locationsValues[0];
				ubigeoLocations = (String) locationsValues[1];

				//System.out.println("----locations----->" + locatiesidLocations + "-----" + ubigeoLocations);
				////////////////////////// Localities

				Object[] localitiesValues = getValue(new String[] { "name" }, "localities",
						"id=" + locatiesidLocations);
				nameLocalities = (String) localitiesValues[0];

				//System.out.println("----localities----->" + nameLocalities);

			} catch (Exception e) {
				//System.out.println("Registro no encontrado  y guarndalo como null");
			}

			
			//ps.setString(17, "[" + descriptionPlatforms + "] - [" + descriptionSolutions + "]");
			//ps.setString(18, "[" + ubigeoLocations + "] - [" + nameLocalities + "]");
			ps.setString(1, alarm.getSeverity());
			ps.setTimestamp(2, d);
			ps.setTimestamp(3, dEnd);
			ps.setString(4, alarm.getEventDescription()+"("+alarm.getProbableCause()+")");
			ps.setString(5, alarm.getElementIdentifier());
			ps.setString(6, alarm.getFaultFlag());
			ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
			ps.setString(8, alarm.getNbi_sys());
			ps.setString(9, "[" + descriptionPlatforms + "] - [" + descriptionSolutions + "]");
			ps.setString(10, "[" + ubigeoLocations + "] - [" + nameLocalities + "]");
			ps.addBatch();

			ps.execute();
			ps.close();
			cn.close();

		} catch (Exception e) {
			
			e.printStackTrace();
			logger.warn("Error SQL: " + e.getMessage());
		}

	}
	
	public void writeFile(String path,String subCarpeta,String filename,String value) {
		 String sep=System.getProperty("file.separator");
		 File f=new File(path);
		 if(!f.exists()) {
			 f.mkdir();
			 System.out.println("Carpeta : "+path+ "  Creado ");
		 }
		 File f2=new File(path+sep+subCarpeta);
		 if(!f2.exists()) {
			 f2.mkdir();
			 System.out.println("SubCarpeta Creado :  "+subCarpeta);
		 }
				 
		 FileWriter fw=null;
		 try {
			 fw=new FileWriter(path+sep+subCarpeta+sep+filename,true);
			 fw.write(value+"--->"+new Date()+"   "+System.getProperty("line.separator"));
			 fw.close();
			
		} catch (Exception e) {
			
		}
		 
	 }
	
	 public long getId(String line,String nbi_sys)throws Exception{
		 String query="Select id from alarms where additional_information = ? and nbi_sys = ? order by id desc limit 1 ";
		 long id=0;
		 try (Connection cn = getConexion(null); PreparedStatement ps = cn.prepareStatement(query);) {
			 ps.setString(1, line);
			 ps.setString(2,nbi_sys);
			 ResultSet rs=ps.executeQuery();
			 while (rs.next()) {
				id=rs.getLong(1);
				
			}
			
		} catch (Exception e) {
			throw new Exception("Error Recuperando ID para la asignacion al archivo CSV");
		}
		 return id;
	 }
	 
	 public void insetAlarmsOnlyCleared(Alarms alarm) {
			System.out.println("Insertando Alarma Nokia  " + new Date());
			String insertTableSQL = "INSERT INTO alarms2 "
					+ "(instance, severity, event_date_start, event_date_end, fault_flag, "
					+ "event_detail, probable_cause, fault_function, alarm_duration, ip_origin, additional_information,"
					+ " event_description, element_identifier, element_ip, alarm_identifier, stamp,sistema,ciudad,nbi_sys)"
					+ " VALUES " + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			try (java.sql.Connection cn = getConexion(null); PreparedStatement ps = cn.prepareStatement(insertTableSQL);) {

				Timestamp dEnd = (alarm.getDateEnd() != null) ? new Timestamp(alarm.getDateEnd().getTime()) : null;
				Timestamp d = (alarm.getDateStart() != null) ? new Timestamp(alarm.getDateStart().getTime()) : null;

				ps.setString(1, alarm.getInstance());
				ps.setString(2, alarm.getSeverity());
				ps.setTimestamp(3, d);
				ps.setTimestamp(4, dEnd);
				ps.setString(5, alarm.getFaultFlag());
				ps.setString(6, alarm.getEventDetail());
				ps.setString(7, alarm.getProbableCause());
				ps.setString(8, alarm.getFaultFunction());
				ps.setString(9, alarm.getAlarmDuration());
				ps.setString(10, alarm.getIpOrigin());
				ps.setString(11, alarm.getAdditionalInformation());
				ps.setString(12, alarm.getEventDescription());
				ps.setString(13, alarm.getElementIdentifier());
				ps.setString(14, alarm.getElementIp());
				ps.setString(15, alarm.getAlarmIdentifier());
				///////////// Elements/////////
				int platforms = 0;
				int locationId = 0;
				String descriptionSolutions = null;
				String descriptionPlatforms = null;
				String ubigeoLocations = null;
				String nameLocalities = null;

				try {
					Object[] elementsValues = getValue(new String[] { "platforms", "location_id" }, "elements",
							"identifier='" + alarm.getElementIdentifier() + "'");
					platforms = (int) elementsValues[0];
					locationId = (int) elementsValues[1];
					//System.out.println("-----Elements---->" + platforms + "-----" + locationId);
					//////// Platform/////////////

					int solutionIdPlatforms = 0;
					Object[] platformsValues = getValue(new String[] { "description", "solution_id" }, "platforms",
							"id=" + platforms);
					descriptionPlatforms = (String) platformsValues[0];
					solutionIdPlatforms = (int) platformsValues[1];
					// //System.out.println("----Platforms----->"+descriptionPlatforms
					// +"-----"+solutionIdPlatforms);
					///////// Solutions//////////////

					Object[] solutionsValues = getValue(new String[] { "description" }, "solutions",
							"id=" + solutionIdPlatforms);
					descriptionSolutions = (String) solutionsValues[0];
					// //System.out.println("----Solutions----->"+descriptionSolutions);
					/////// locations//////////
					int locatiesidLocations = 0;

					Object[] locationsValues = getValue(new String[] { "localitie_id", "ubigeo" }, "locations",
							"id=" + locationId);
					locatiesidLocations = (int) locationsValues[0];
					ubigeoLocations = (String) locationsValues[1];

					//System.out.println("----locations----->" + locatiesidLocations + "-----" + ubigeoLocations);
					////////////////////////// Localities

					Object[] localitiesValues = getValue(new String[] { "name" }, "localities",
							"id=" + locatiesidLocations);
					nameLocalities = (String) localitiesValues[0];

					//System.out.println("----localities----->" + nameLocalities);

				} catch (Exception e) {
					//System.out.println("Registro no encontrado  y guarndalo como null");
				}

				ps.setTimestamp(16, new Timestamp(System.currentTimeMillis()));
				ps.setString(17, "[" + descriptionPlatforms + "] - [" + descriptionSolutions + "]");
				ps.setString(18, "[" + ubigeoLocations + "] - [" + nameLocalities + "]");
				ps.setString(19, alarm.getNbi_sys());
				ps.addBatch();

				ps.execute();
				ps.close();
				cn.close();

			} catch (Exception e) {
				
				e.printStackTrace();
				logger.warn("Error SQL: " + e.getMessage());
			}

		}
	 
	 public int updateActiveMensual(Alarms model,String tablename) {
		 int validateupdate=0;
		 String sqlupdate="update "+tablename+" set event_date_end=? where event_date_end is null and nbi_sys=? and element_identifier=?";
		 try(Connection cn = getConexion(null); PreparedStatement ps = cn.prepareStatement(sqlupdate);) {
			 ps.setTimestamp(1,new Timestamp(model.getDateStart().getTime()));
			 ps.setString(2, model.getNbi_sys());
			 ps.setString(3,model.getElementIdentifier());
			 validateupdate= ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			validateupdate=0;
		}
		 
		 return validateupdate;
	 }
	 
	 public int updateActive(Alarms model) {
		 int validateupdate=0;
		 String sqlupdate="update alarms2 set event_date_end=?  where event_date_end is null and nbi_sys=? order by id desc limit 1 ";
		 try(Connection cn = getConexion(null); PreparedStatement ps = cn.prepareStatement(sqlupdate);) {
			 ps.setTimestamp(1,new Timestamp(model.getDateStart().getTime()));
			 //ps.setString(2, model.getAdditional_information());
			 ps.setString(2,model.getNbi_sys());
			 validateupdate=ps.executeUpdate();
			 
			
		} catch (Exception e) {
			e.printStackTrace();
			return validateupdate;
			
		}
		 return validateupdate;
	 }
	 
	 
	 public void setModel_Alarms_Translator(Alarms model) {
		 System.out.print("Modelo severity y description "+ model.getSeverity() + " -- " +model.getEventDescription() );
		 ResultSet rs=null;
		 int validateupdate=0;
		 String sqlupdate="SELECT gilat_severity,gilat_description FROM alarms_translator WHERE nbi_sys=? and o_severity = ? and o_probablecause=? ";
		 try(Connection cn = getConexion(null); PreparedStatement ps = cn.prepareStatement(sqlupdate);) {
			 ps.setString(1, model.getNbi_sys());
			 ps.setString(2,model.getSeverity());
			 ps.setString(3,model.getProbableCause());
			 //System.out.println(ps);
			 rs=ps.executeQuery();
			 while (rs.next()) {
				model.setSeverity(rs.getString(1));
				model.setEventDescription(rs.getString(2));
				}
			
		} catch (Exception e) {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
		}
		 
		 System.out.print("Modelo severity y description (new From Translator) "+ model.getSeverity() + " -- " +model.getEventDescription() );
	 }
	 
	 public int updateAlarms(Alarms model) {
		 int validateupdate=0;
		 //String sqlupdate="update alarms set event_date_end = ? where stamp >= (select t.stamp from (select ms.* from alarms ms where ms.nbi_sys = ? and ms.element_identifier=? and ms.event_date_end is null order by ms.id desc limit 1) t)";
		 String sqlupdate="update alarms set event_date_end = ? where nbi_sys = ? and element_identifier=? and event_date_end is null ";
		 try(Connection cn = getConexion(null); PreparedStatement ps = cn.prepareStatement(sqlupdate);) {
			 System.out.println("--> :Actualizacion tabla Alarms");
			 ps.setTimestamp(1,new Timestamp(model.getDateStart().getTime()));
			 //ps.setString(2, model.getAdditional_information());
			 ps.setString(2,model.getNbi_sys());
			 ps.setString(3, model.getElementIdentifier());
			 validateupdate=ps.executeUpdate();
			 
			
		} catch (Exception e) {
			e.printStackTrace();
			return validateupdate;
			
		}
		 return validateupdate;
	 }

	 public void insetAlarmsOtherServer(Alarms alarm) {
			System.out.println("Insertando Alarma Nokia  " + new Date());
			String insertTableSQL = "INSERT INTO alarms "
					+ "(instance, severity, event_date_start, event_date_end, fault_flag, "
					+ "event_detail, probable_cause, fault_function, alarm_duration, ip_origin, additional_information,"
					+ " event_description, element_identifier, element_ip, alarm_identifier, stamp,sistema,ciudad,nbi_sys)"
					+ " VALUES " + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			try (java.sql.Connection cn = getConexion("1"); PreparedStatement ps = cn.prepareStatement(insertTableSQL);) {

				Timestamp dEnd = (alarm.getDateEnd() != null) ? new Timestamp(alarm.getDateEnd().getTime()) : null;
				Timestamp d = (alarm.getDateStart() != null) ? new Timestamp(alarm.getDateStart().getTime()) : null;

				ps.setString(1, alarm.getInstance());
				ps.setString(2, alarm.getSeverity());
				ps.setTimestamp(3, d);
				ps.setTimestamp(4, dEnd);
				ps.setString(5, alarm.getFaultFlag());
				ps.setString(6, alarm.getEventDetail());
				ps.setString(7, alarm.getProbableCause());
				ps.setString(8, alarm.getFaultFunction());
				ps.setString(9, alarm.getAlarmDuration());
				ps.setString(10, alarm.getIpOrigin());
				ps.setString(11, alarm.getAdditionalInformation());
				ps.setString(12, alarm.getEventDescription());
				ps.setString(13, alarm.getElementIdentifier());
				ps.setString(14, alarm.getElementIp());
				ps.setString(15, alarm.getAlarmIdentifier());
				///////////// Elements/////////
				int platforms = 0;
				int locationId = 0;
				String descriptionSolutions = null;
				String descriptionPlatforms = null;
				String ubigeoLocations = null;
				String nameLocalities = null;

				try {
					Object[] elementsValues = getValue(new String[] { "platforms", "location_id" }, "elements",
							"identifier='" + alarm.getElementIdentifier() + "'");
					platforms = (int) elementsValues[0];
					locationId = (int) elementsValues[1];
					//System.out.println("-----Elements---->" + platforms + "-----" + locationId);
					//////// Platform/////////////

					int solutionIdPlatforms = 0;
					Object[] platformsValues = getValue(new String[] { "description", "solution_id" }, "platforms",
							"id=" + platforms);
					descriptionPlatforms = (String) platformsValues[0];
					solutionIdPlatforms = (int) platformsValues[1];
					// //System.out.println("----Platforms----->"+descriptionPlatforms
					// +"-----"+solutionIdPlatforms);
					///////// Solutions//////////////

					Object[] solutionsValues = getValue(new String[] { "description" }, "solutions",
							"id=" + solutionIdPlatforms);
					descriptionSolutions = (String) solutionsValues[0];
					// //System.out.println("----Solutions----->"+descriptionSolutions);
					/////// locations//////////
					int locatiesidLocations = 0;

					Object[] locationsValues = getValue(new String[] { "localitie_id", "ubigeo" }, "locations",
							"id=" + locationId);
					locatiesidLocations = (int) locationsValues[0];
					ubigeoLocations = (String) locationsValues[1];

					//System.out.println("----locations----->" + locatiesidLocations + "-----" + ubigeoLocations);
					////////////////////////// Localities

					Object[] localitiesValues = getValue(new String[] { "name" }, "localities",
							"id=" + locatiesidLocations);
					nameLocalities = (String) localitiesValues[0];

					//System.out.println("----localities----->" + nameLocalities);

				} catch (Exception e) {
					//System.out.println("Registro no encontrado  y guarndalo como null");
				}

				ps.setTimestamp(16, new Timestamp(System.currentTimeMillis()));
				ps.setString(17, "[" + descriptionPlatforms + "] - [" + descriptionSolutions + "]");
				ps.setString(18, "[" + ubigeoLocations + "] - [" + nameLocalities + "]");
				ps.setString(19, alarm.getNbi_sys());
				ps.addBatch();

				ps.execute();
				ps.close();
				cn.close();

			} catch (Exception e) {
				
				e.printStackTrace();
				logger.warn("Error SQL: " + e.getMessage());
			}

		}

}
