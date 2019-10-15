/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.dao;

import asistp.bean.BeanAlarms;

import java.beans.Statement;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
//import sun.text.normalizer.UBiDiProps;

/**
 *
 * @author Usuario2
 */
public class CoriantDAO extends Conexion {

	public void insetAlarms(BeanAlarms model, String ip, List<StringBuilder> lines) {
		// System.out.println(model);

		String insertTableSQL = "INSERT INTO alarms "
				+ "(instance, severity, event_date_start, event_date_end, fault_flag, "
				+ "event_detail, probable_cause, fault_function, alarm_duration, ip_origin, "
				+ "additional_information, event_description, element_identifier, status,stamp,sistema,ciudad,nbi_sys)"
				+ " VALUES " + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		String Elements = "select platforms,location_id from elements where identifier=?";
		String platform = "select description, solution_id  from platforms where id=?";
		String solutions = "select description from solutions where id=?";
		String locations = "select localitie_id,ubigeo from locations where id=?";
		String localities = "select name from localities where id=?";
		// String localities="select name from localities where id=?";
		try (Connection cn = getConexion(null); PreparedStatement ps = cn.prepareStatement(insertTableSQL);) {
			// System.out.println("----");
			int idlocation = 0;
			// int localities_id=0;
			int idPlatform = 0;
			String platfomrsDescription = null;
			int platformsIdSolution = 0;
			// String localities_name=null;
			String locationsUbigeo = null;
			String localitiesName = null;
			String solutionsDescription = null;
			Connection cn2 = null;
			PreparedStatement ps2 =null;
			ResultSet rs=null;
			try {

				////////////////////////// Elements/////////////////////////////////
				cn2 = getConexion(null);
				ps2 = cn2.prepareStatement(Elements);
				String mac = model.getElement_identifier();
				mac = mac.substring(1, mac.length() - 1);
				// System.out.println("MACCC :" + mac);
				ps2.setString(1, model.getElement_identifier());

				rs = ps2.executeQuery();

				while (rs.next()) {
					// localities_id=rs.getInt(1);
					idPlatform = rs.getInt(1);
					idlocation = rs.getInt(2);
				}
				// System.out.println("idPlatform (elements) : " + idPlatform);
				// System.out.println("idLocation (elements): " + idlocation);

				/////////////////////////// platform/////////////////////////////////
				// System.out.println("Segunda Busqueda : " + platform +" where platfomr = "+
				// idPlatform);
				ps2 = cn2.prepareStatement(platform);
				ps2.setInt(1, idPlatform);

				rs = ps2.executeQuery();
				while (rs.next()) {
					platfomrsDescription = rs.getString(1);
					platformsIdSolution = rs.getInt(2);
				}

				// System.out.println("descripction (platform)" + platfomrsDescription );
				// System.out.println("idsolution (platforn)" + platformsIdSolution );

				/////////////////////////// Solutions/////////////////////////////////
				// System.out.println("Tercera Busqueda : " + solutions + "id = " +
				/////////////////////////// platformsIdSolution);

				ps2 = cn2.prepareStatement(solutions);
				ps2.setInt(1, platformsIdSolution);

				rs = ps2.executeQuery();
				while (rs.next()) {
					solutionsDescription = rs.getString(1);
				}
				////////////////////////////////////////////////////////////////////////
				/////////////////////////// locations/////////////////////////////////
				// System.out.println("Cuarta Busqueda : " + locations + "id = " + idlocation);
				ps2 = cn2.prepareStatement(locations);
				ps2.setInt(1, idlocation);

				rs = ps2.executeQuery();
				while (rs.next()) {
					locationsUbigeo = rs.getString(2);
					idlocation = rs.getInt(1);
				}

				// System.out.println("locations ubigeo : " + locationsUbigeo);
				// System.out.println("localities id : " + idlocation);
				///////////////////////////////////////////////////////////////////////
				/////////////////////////// localities/////////////////////////////////
				// System.out.println("Quinta busqueda :" + localities +" where id="+idlocation
				// );
				ps2 = cn2.prepareStatement(localities);
				ps2.setInt(1, idlocation);

				rs = ps2.executeQuery();
				while (rs.next()) {
					localitiesName = rs.getString(1);
				}
				///////////////////////////////////////////////////////////////////////
				// System.out.println("Locaties_name : " + localitiesName);

			} catch (Exception e) {
				System.out.println("-------------Error de Busqueda");

			}finally{
				cn2.close();
				ps2.close();
				rs.close();
			}
			

			// 11/06/2018
			/*
			 * try (PreparedStatement select = cn.
			 * prepareStatement("SELECT id FROM alarms WHERE element_identifier=? and event_date_end is null order by id desc limit 1"
			 * )) { select.setString(1, model.getElement_identifier());
			 * 
			 * ResultSet rs = select.executeQuery();
			 * 
			 * if (rs.next()) { System.out.println("UPDATE: " + rs.getLong(1));
			 * System.out.println("IS new: " + model.isNew()); System.out.println("Start: "
			 * + model.getEvent_date_start()); System.err.println("End: " +
			 * model.getEvent_date_end()); System.out.println(model);
			 * System.out.println("==========================================="); //UPDATE
			 * Long id = rs.getLong(1); try (PreparedStatement update =
			 * cn.prepareStatement("UPDATE alarms SET event_date_end=? WHERE id=?")) {
			 * update.setLong(2, id); update.setDate(1,
			 * java.sql.Date.valueOf(model.getEvent_date_end().toInstant().atZone(ZoneId.of(
			 * "America/Lima")).toLocalDate())); update.executeUpdate(); } } else {
			 */
			/*
			 * System.out.println("INSERT"); System.out.println("IS new: " + model.isNew());
			 * System.out.println("Start: " + model.getEvent_date_start());
			 * System.err.println("End: " + model.getEvent_date_end());
			 * System.out.println("===========================================");
			 */
			Timestamp dEnd = (model.getEvent_date_end() != null) ? new Timestamp(model.getEvent_date_end().getTime())
					: null;
			Timestamp d = (model.getEvent_date_start() != null) ? new Timestamp(model.getEvent_date_start().getTime())
					: null;

			ps.setString(1, model.getInstance());
			ps.setString(2, model.getSeverity());
			ps.setTimestamp(3, d);
			ps.setTimestamp(4, dEnd);
			ps.setString(5, model.getFault_flag());
			ps.setString(6, model.getEvent_detail());
			ps.setString(7, null);
			ps.setString(8, model.getFault_function());
			ps.setString(9, model.getAlarm_duration());
			ps.setString(10, ip);
			ps.setString(11, lines.toString());
			ps.setString(12, model.getInstance());
			ps.setString(13, model.getElement_identifier());

			ps.setString(14, model.getStatus());
			// System.out.println("Valor para guardar : " + localities_name);
			// ps.setString(15,localities_name);
			ps.setTimestamp(15, new Timestamp(System.currentTimeMillis()));
			ps.setString(16, "[" + platfomrsDescription + "] - [" + solutionsDescription + "]");
			ps.setString(17, "[" + locationsUbigeo + "] - [" + localitiesName + "]");
			ps.setString(18, model.getNbi_sys());
			int validateInsert = ps.executeUpdate();
			if (validateInsert > 0) {
				System.out.println("Alarma Insertada :  " + model.getTypeAlarma() + "   " + new Date());
			} else {
				System.out.println("Alarma NO Insertada :  " + model.getTypeAlarma() + "   " + new Date());
			}

			/*
			 * } }
			 */

		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
			// Logger.getLogger("incident").warn(e.getMessage());
		}

	}

	public void insetAlarms2(BeanAlarms model, String ip) {
		// System.out.println(model);

		String insertTableSQL = "INSERT INTO alarms "
				+ "(instance, severity, event_date_start, event_date_end, fault_flag, "
				+ "event_detail, probable_cause, fault_function, alarm_duration, ip_origin, "
				+ "additional_information, event_description, element_identifier, status,stamp,sistema,ciudad,nbi_sys)"
				+ " VALUES " + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try (Connection cn = getConexion(null); PreparedStatement ps = cn.prepareStatement(insertTableSQL);) {

			ps.setString(1, model.getInstance());
			ps.setString(2, model.getSeverity());
			ps.setTimestamp(3, new Timestamp(model.getEvent_date_start().getTime()));
			ps.setTimestamp(4, new Timestamp(model.getEvent_date_end().getTime()));
			ps.setString(5, model.getFault_flag());
			ps.setString(6, model.getEvent_detail());
			ps.setString(7, model.getProbable_cause());
			ps.setString(8, model.getFault_function());
			ps.setString(9, model.getAlarm_duration());
			ps.setString(10, model.getDevice_ip());
			ps.setString(11, model.getAdditional_information());
			ps.setString(12, model.getEvent_description());
			ps.setString(13, model.getElement_identifier());
			ps.setString(14, null);
			ps.setTimestamp(15, model.getStamp());
			ps.setString(16, model.getSistema());
			ps.setString(17, null);
			ps.setString(18, model.getNbi_sys());

			int validateInsert = ps.executeUpdate();
			if (validateInsert > 0) {
				System.out.println("Alarma Insertada :  " + model.getTypeAlarma() + "   " + new Date());
			} else {
				System.out.println("Alarma NO Insertada :  " + model.getTypeAlarma() + "   " + new Date());
			}

			/*
			 * } }
			 */

		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
			// Logger.getLogger("incident").warn(e.getMessage());
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
	
	 public void insetAlarmsMensual(BeanAlarms model, String ip,List<StringBuilder> lines,String tablaname) {
	        //System.out.println(model);

	        String insertTableSQL = "INSERT INTO "+tablaname + " "
	                + "(severity, event_date_start, event_date_end,event_description, element_identifier, fault_flag, "
	                + "stamp,solution,locality,nbi_sys)"
	                + " VALUES "
	                + "(?,?,?,?,?,?,?,?,?,?)";

	        String Elements = "select platforms,location_id from elements where identifier=?";
	        String platform = "select description, solution_id  from platforms where id=?";
	        String solutions = "select description from solutions where id=?";
	        String locations = "select localitie_id,ubigeo from locations where id=?";
	        String localities = "select name from localities where id=?";
	        //String localities="select name from localities where id=?";
	        try (Connection cn = getConexion(null);
	                PreparedStatement ps = cn.prepareStatement(insertTableSQL);) {
	            //System.out.println("----");
	            int idlocation = 0;
	            //int localities_id=0;
	            int idPlatform = 0;
	            String platfomrsDescription = null;
	            int platformsIdSolution = 0;
	            //String localities_name=null;
	            String locationsUbigeo = null;
	            String localitiesName = null;
	            String solutionsDescription = null;
	            Connection cn2 = null;
	            PreparedStatement ps2=null;
	            ResultSet rs =null;
	            try {

	                //////////////////////////Elements/////////////////////////////////
	                cn2 = getConexion(null);
	                ps2 = cn2.prepareStatement(Elements);
	                String mac = model.getElement_identifier();
	                mac = mac.substring(1, mac.length() - 1);
	                // System.out.println("MACCC :" + mac);
	                ps2.setString(1, model.getElement_identifier());
	                
	                

	                rs = ps2.executeQuery();

	                while (rs.next()) {
	                    //localities_id=rs.getInt(1);           
	                    idPlatform = rs.getInt(1);
	                    idlocation = rs.getInt(2);
	                }
	               // System.out.println("idPlatform (elements) : " + idPlatform);
	              // System.out.println("idLocation  (elements): " + idlocation);
	                
	                    ///////////////////////////platform/////////////////////////////////
	               // System.out.println("Segunda Busqueda : " + platform +" where platfomr = "+ idPlatform);
	                    ps2 = cn2.prepareStatement(platform);
	                    ps2.setInt(1, idPlatform);

	                    rs = ps2.executeQuery();
	                    while (rs.next()) {
	                        platfomrsDescription = rs.getString(1);
	                        platformsIdSolution = rs.getInt(2);
	                    }
	                    
	             // System.out.println("descripction (platform)" + platfomrsDescription );
	             // System.out.println("idsolution (platforn)" + platformsIdSolution );
	                    
	                    
	                
	                   
	                    ///////////////////////////Solutions/////////////////////////////////    
	                    //System.out.println("Tercera Busqueda : " + solutions + "id = " + platformsIdSolution);
	                    
	                    ps2 = cn2.prepareStatement(solutions);
	                    ps2.setInt(1, platformsIdSolution);

	                    rs = ps2.executeQuery();
	                    while (rs.next()) {
	                        solutionsDescription = rs.getString(1);
	                    }
	                    ////////////////////////////////////////////////////////////////////////
	                    ///////////////////////////locations/////////////////////////////////   
	                  // System.out.println("Cuarta Busqueda : " + locations + "id = " + idlocation);
	                    ps2 = cn2.prepareStatement(locations);
	                    ps2.setInt(1, idlocation);
	                    
	                    rs = ps2.executeQuery();
	                    while (rs.next()) {
	                        locationsUbigeo = rs.getString(2);
	                        idlocation = rs.getInt(1);
	                    }
	                    
	                   // System.out.println("locations ubigeo : " + locationsUbigeo);
	                   // System.out.println("localities id : " + idlocation);
	                    ///////////////////////////////////////////////////////////////////////
	                    ///////////////////////////localities/////////////////////////////////    
	                   //System.out.println("Quinta busqueda  :" + localities +"  where id="+idlocation );
	                    ps2 = cn2.prepareStatement(localities);
	                    ps2.setInt(1, idlocation);

	                    rs = ps2.executeQuery();
	                    while (rs.next()) {
	                        localitiesName = rs.getString(1);
	                    }
	                    ///////////////////////////////////////////////////////////////////////
	                    //System.out.println("Locaties_name : " + localitiesName);          

	                
	          
	            } catch (Exception e) {
	                System.out.println("-------------Error de Busqueda");
	               
	                
	                
	            }finally {
					cn2.close();
					rs.close();
					ps2.close();
				}

	            //11/06/2018
	            /* try (PreparedStatement select = cn.prepareStatement("SELECT id FROM alarms WHERE element_identifier=? and event_date_end is null order by id desc limit 1")) {
	                select.setString(1, model.getElement_identifier());

	                ResultSet rs = select.executeQuery();

	                if (rs.next()) {
	                    System.out.println("UPDATE: " + rs.getLong(1));
	                    System.out.println("IS new: " + model.isNew());
	                    System.out.println("Start: " + model.getEvent_date_start());
	                    System.err.println("End: " + model.getEvent_date_end());
	                    System.out.println(model);
	                    System.out.println("===========================================");
	                    //UPDATE
	                    Long id = rs.getLong(1);
	                    try (PreparedStatement update = cn.prepareStatement("UPDATE alarms SET event_date_end=? WHERE id=?")) {
	                        update.setLong(2, id);
	                        update.setDate(1, java.sql.Date.valueOf(model.getEvent_date_end().toInstant().atZone(ZoneId.of("America/Lima")).toLocalDate()));
	                        update.executeUpdate();
	                    }
	                } else { */
	 /*System.out.println("INSERT");
	                    System.out.println("IS new: " + model.isNew());
	                    System.out.println("Start: " + model.getEvent_date_start());
	                    System.err.println("End: " + model.getEvent_date_end());
	                    System.out.println("===========================================");*/
	            Timestamp dEnd = (model.getEvent_date_end() != null) ? new Timestamp(model.getEvent_date_end().getTime()) : null;
	            Timestamp d = (model.getEvent_date_start() != null) ? new Timestamp(model.getEvent_date_start().getTime()) : null;

	            //ps.setString(1, model.getInstance());
	            ps.setString(1, model.getSeverity());
	            ps.setTimestamp(2, d);
	            ps.setTimestamp(3, dEnd);
	            //ps.setString(4, model.getInstance() +" " +model.getProbable_cause());
	            ps.setString(4, model.getInstance());
	            ps.setString(5, model.getElement_identifier());
	            ps.setString(6, model.getFault_flag());
	            ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
	            ps.setString(8, "["+platfomrsDescription+"] - [" + solutionsDescription+"]");
		        ps.setString(9, "["+locationsUbigeo + "] - [" + localitiesName+"]");
		        ps.setString(10,model.getNbi_sys());

	            //ps.setString(6, model.getEvent_detail());
	            //ps.setString(7, null);
	            //ps.setString(8, model.getFault_function());
	            //ps.setString(9, model.getAlarm_duration());
	            //ps.setString(10, ip);
	            //ps.setString(11, lines.toString());
	            
	            //

	            //ps.setString(14, model.getStatus());
	            // System.out.println("Valor para guardar : " + localities_name);
	            //    ps.setString(15,localities_name);
	           // 
	           // 
	            //
	            int validateInsert=ps.executeUpdate();
	            if(validateInsert>0){
	                System.out.println("Alarma Insertada (TABLA MENSUAL):  " + model.getTypeAlarma()+ "   " + new Date());
	            }else{
	            System.out.println("Alarma NO Insertada (TABLA MENSUAL):  " + model.getTypeAlarma()+ "   " + new Date());
	            }
	            
	            /*  }
	            }*/
	            
	        } catch (Exception e) {
	            System.out.println("Error Inesperado");
	            e.printStackTrace();
	            //Logger.getLogger("incident").warn(e.getMessage());
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
			 fw.write(value+System.getProperty("line.separator"));
			 fw.close();
			
		} catch (Exception e) {
			
		}
		 
	 }
	 
	 public void insetAlarmsOnlyCleared(BeanAlarms model, String ip, List<StringBuilder> lines) {
			// System.out.println(model);

			String insertTableSQL = "INSERT INTO alarms2 "
					+ "(instance, severity, event_date_start, event_date_end, fault_flag, "
					+ "event_detail, probable_cause, fault_function, alarm_duration, ip_origin, "
					+ "additional_information, event_description, element_identifier, status,stamp,sistema,ciudad,nbi_sys)"
					+ " VALUES " + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			String Elements = "select platforms,location_id from elements where identifier=?";
			String platform = "select description, solution_id  from platforms where id=?";
			String solutions = "select description from solutions where id=?";
			String locations = "select localitie_id,ubigeo from locations where id=?";
			String localities = "select name from localities where id=?";
			// String localities="select name from localities where id=?";
			try (Connection cn = getConexion(null); PreparedStatement ps = cn.prepareStatement(insertTableSQL);) {
				// System.out.println("----");
				int idlocation = 0;
				// int localities_id=0;
				int idPlatform = 0;
				String platfomrsDescription = null;
				int platformsIdSolution = 0;
				// String localities_name=null;
				String locationsUbigeo = null;
				String localitiesName = null;
				String solutionsDescription = null;
				Connection cn2 = null;
				try {

					////////////////////////// Elements/////////////////////////////////
					cn2 = getConexion(null);
					PreparedStatement ps2 = cn2.prepareStatement(Elements);
					String mac = model.getElement_identifier();
					mac = mac.substring(1, mac.length() - 1);
					// System.out.println("MACCC :" + mac);
					ps2.setString(1, model.getElement_identifier());

					ResultSet rs = ps2.executeQuery();

					while (rs.next()) {
						// localities_id=rs.getInt(1);
						idPlatform = rs.getInt(1);
						idlocation = rs.getInt(2);
					}
					// System.out.println("idPlatform (elements) : " + idPlatform);
					// System.out.println("idLocation (elements): " + idlocation);

					/////////////////////////// platform/////////////////////////////////
					// System.out.println("Segunda Busqueda : " + platform +" where platfomr = "+
					// idPlatform);
					ps2 = cn2.prepareStatement(platform);
					ps2.setInt(1, idPlatform);

					rs = ps2.executeQuery();
					while (rs.next()) {
						platfomrsDescription = rs.getString(1);
						platformsIdSolution = rs.getInt(2);
					}

					// System.out.println("descripction (platform)" + platfomrsDescription );
					// System.out.println("idsolution (platforn)" + platformsIdSolution );

					/////////////////////////// Solutions/////////////////////////////////
					// System.out.println("Tercera Busqueda : " + solutions + "id = " +
					/////////////////////////// platformsIdSolution);

					ps2 = cn2.prepareStatement(solutions);
					ps2.setInt(1, platformsIdSolution);

					rs = ps2.executeQuery();
					while (rs.next()) {
						solutionsDescription = rs.getString(1);
					}
					////////////////////////////////////////////////////////////////////////
					/////////////////////////// locations/////////////////////////////////
					// System.out.println("Cuarta Busqueda : " + locations + "id = " + idlocation);
					ps2 = cn2.prepareStatement(locations);
					ps2.setInt(1, idlocation);

					rs = ps2.executeQuery();
					while (rs.next()) {
						locationsUbigeo = rs.getString(2);
						idlocation = rs.getInt(1);
					}

					// System.out.println("locations ubigeo : " + locationsUbigeo);
					// System.out.println("localities id : " + idlocation);
					///////////////////////////////////////////////////////////////////////
					/////////////////////////// localities/////////////////////////////////
					// System.out.println("Quinta busqueda :" + localities +" where id="+idlocation
					// );
					ps2 = cn2.prepareStatement(localities);
					ps2.setInt(1, idlocation);

					rs = ps2.executeQuery();
					while (rs.next()) {
						localitiesName = rs.getString(1);
					}
					ps2.close();
					///////////////////////////////////////////////////////////////////////
					// System.out.println("Locaties_name : " + localitiesName);

				} catch (Exception e) {
					System.out.println("-------------Error de Busqueda");

				}finally {
					cn2.close();
				}

				// 11/06/2018
				/*
				 * try (PreparedStatement select = cn.
				 * prepareStatement("SELECT id FROM alarms WHERE element_identifier=? and event_date_end is null order by id desc limit 1"
				 * )) { select.setString(1, model.getElement_identifier());
				 * 
				 * ResultSet rs = select.executeQuery();
				 * 
				 * if (rs.next()) { System.out.println("UPDATE: " + rs.getLong(1));
				 * System.out.println("IS new: " + model.isNew()); System.out.println("Start: "
				 * + model.getEvent_date_start()); System.err.println("End: " +
				 * model.getEvent_date_end()); System.out.println(model);
				 * System.out.println("==========================================="); //UPDATE
				 * Long id = rs.getLong(1); try (PreparedStatement update =
				 * cn.prepareStatement("UPDATE alarms SET event_date_end=? WHERE id=?")) {
				 * update.setLong(2, id); update.setDate(1,
				 * java.sql.Date.valueOf(model.getEvent_date_end().toInstant().atZone(ZoneId.of(
				 * "America/Lima")).toLocalDate())); update.executeUpdate(); } } else {
				 */
				/*
				 * System.out.println("INSERT"); System.out.println("IS new: " + model.isNew());
				 * System.out.println("Start: " + model.getEvent_date_start());
				 * System.err.println("End: " + model.getEvent_date_end());
				 * System.out.println("===========================================");
				 */
				Timestamp dEnd = (model.getEvent_date_end() != null) ? new Timestamp(model.getEvent_date_end().getTime())
						: null;
				Timestamp d = (model.getEvent_date_start() != null) ? new Timestamp(model.getEvent_date_start().getTime())
						: null;

				ps.setString(1, model.getInstance());
				ps.setString(2, model.getSeverity());
				ps.setTimestamp(3, d);
				ps.setTimestamp(4, dEnd);
				ps.setString(5, model.getFault_flag());
				ps.setString(6, model.getEvent_detail());
				ps.setString(7, null);
				ps.setString(8, model.getFault_function());
				ps.setString(9, model.getAlarm_duration());
				ps.setString(10, ip);
				ps.setString(11, lines.toString());
				//ps.setString(12, model.getInstance() + " " + model.getProbable_cause());
				ps.setString(12, model.getInstance());
				ps.setString(13, model.getElement_identifier());

				ps.setString(14, model.getStatus());
				// System.out.println("Valor para guardar : " + localities_name);
				// ps.setString(15,localities_name);
				ps.setTimestamp(15, new Timestamp(System.currentTimeMillis()));
				ps.setString(16, "[" + platfomrsDescription + "] - [" + solutionsDescription + "]");
				ps.setString(17, "[" + locationsUbigeo + "] - [" + localitiesName + "]");
				ps.setString(18, model.getNbi_sys());
				int validateInsert = ps.executeUpdate();
				if (validateInsert > 0) {
					System.out.println("Alarma Insertada (Alarms2-->OnlyActive) :  " + model.getTypeAlarma() + "   " + new Date());
				} else {
					System.out.println("Alarma NO Insertada : (Alarms2-->OnlyActive) " + model.getTypeAlarma() + "   " + new Date());
				}

				/*
				 * } }
				 */

			} catch (Exception e) {
				System.out.println("Error");
				e.printStackTrace();
				// Logger.getLogger("incident").warn(e.getMessage());
			}

		}
	 
	 public int updateActive(BeanAlarms model) {
		 int validateupdate=0;
		 String sqlupdate="update alarms2 set event_date_end=?  where event_date_end is null and nbi_sys=? order by id desc limit 1 ";
		 try(Connection cn = getConexion(null); PreparedStatement ps = cn.prepareStatement(sqlupdate);) {
			 ps.setTimestamp(1,new Timestamp(model.getEvent_date_start().getTime()));
			 //ps.setString(2, model.getAdditional_information());
			 ps.setString(2,model.getNbi_sys());
			 validateupdate=ps.executeUpdate();
			 
			
		} catch (Exception e) {
			e.printStackTrace();
			return validateupdate;
			
		}
		 return validateupdate;
	 }
	 public int updateAlarms(BeanAlarms model) {
		 System.out.println("Ingresando MEtodo update Alarmas");
		 int validateupdate=0;
		 //String sqlupdate="update alarms set event_date_end = ? where stamp >= (select t.stamp from (select * from alarms ms where ms.nbi_sys = ? and ms.element_identifier=? and ms.event_date_end is null order by ms.id desc limit 1) t) and element_identifier=? ";
		 String sqlupdate="update alarms set event_date_end = ? where nbi_sys = ? and element_identifier=? and event_date_end is null ";
		 try(Connection cn = getConexion(null); PreparedStatement ps = cn.prepareStatement(sqlupdate);) {
			 System.out.println("--> :Actualizacion tabla Alarms");
			 ps.setTimestamp(1,new Timestamp(model.getEvent_date_start().getTime()));
			 //ps.setString(2, model.getAdditional_information());
			 ps.setString(2,model.getNbi_sys());
			 ps.setString(3, model.getElement_identifier());
			 validateupdate=ps.executeUpdate();
			 
			
		} catch (Exception e) {
			System.out.println("Error MEtodo update Alarmas");
			e.printStackTrace();
			return validateupdate;
			
		}
		 return validateupdate;
	 }
	 
	 public int updateActiveMensual(BeanAlarms model,String tablename) {
		 System.out.println(" Ingresando updateActiveMensual Metodo ");
		 int validateupdate=0;
		 String sqlupdate="update "+tablename+" set event_date_end=? where event_date_end is null and nbi_sys=? and element_identifier=?";
		 try(Connection cn = getConexion(null); PreparedStatement ps = cn.prepareStatement(sqlupdate);) {
			 ps.setTimestamp(1,new Timestamp(model.getEvent_date_start().getTime()));
			 ps.setString(2, model.getNbi_sys());
			 ps.setString(3,model.getElement_identifier());
			 //System.out.println(ps);
			validateupdate= ps.executeUpdate();
			
		} catch (Exception e) {
			System.out.println(" Error updateActiveMensual Metodo ");
			e.printStackTrace();
			validateupdate=0;
		}
		 
		 return validateupdate;
	 }
	 
	 public void setModel_Alarms_Translator(BeanAlarms model) {
		 //System.out.print("Modelo severity y description "+ model.getSeverity() + " -- " +model.getInstance() );
		 ResultSet rs=null;
		 int validateupdate=0;
		 String sqlupdate="SELECT gilat_severity,gilat_description FROM alarms_translator WHERE nbi_sys=? and o_severity = ? and o_probablecause=? ";
		 try(Connection cn = getConexion(null); PreparedStatement ps = cn.prepareStatement(sqlupdate);) {
			 ps.setString(1, model.getNbi_sys());
			 ps.setString(2,model.getSeverity());
			 ps.setString(3,model.getInstance());
			 //System.out.println(ps);
			 rs=ps.executeQuery();
			 while (rs.next()) {
				model.setSeverity(rs.getString(1));
				model.setInstance(rs.getString(2));
				}
			
		} catch (Exception e) {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
		}
		 
		// System.out.print("Modelo severity y description (new From Translator) "+ model.getSeverity() + " -- " +model.getInstance() );
	 }
	 public void insetAlarmsOtherServer(BeanAlarms model, String ip, List<StringBuilder> lines) {
			// System.out.println(model);

			String insertTableSQL = "INSERT INTO alarms "
					+ "(instance, severity, event_date_start, event_date_end, fault_flag, "
					+ "event_detail, probable_cause, fault_function, alarm_duration, ip_origin, "
					+ "additional_information, event_description, element_identifier, status,stamp,sistema,ciudad,nbi_sys)"
					+ " VALUES " + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			String Elements = "select platforms,location_id from elements where identifier=?";
			String platform = "select description, solution_id  from platforms where id=?";
			String solutions = "select description from solutions where id=?";
			String locations = "select localitie_id,ubigeo from locations where id=?";
			String localities = "select name from localities where id=?";
			// String localities="select name from localities where id=?";
			try (Connection cn = getConexion("1"); PreparedStatement ps = cn.prepareStatement(insertTableSQL);) {
				// System.out.println("----");
				int idlocation = 0;
				// int localities_id=0;
				int idPlatform = 0;
				String platfomrsDescription = null;
				int platformsIdSolution = 0;
				// String localities_name=null;
				String locationsUbigeo = null;
				String localitiesName = null;
				String solutionsDescription = null;
				Connection cn2 = null;
				PreparedStatement ps2 =null;
				ResultSet rs=null;
				try {

					////////////////////////// Elements/////////////////////////////////
					cn2 = getConexion(null);
					ps2 = cn2.prepareStatement(Elements);
					String mac = model.getElement_identifier();
					mac = mac.substring(1, mac.length() - 1);
					// System.out.println("MACCC :" + mac);
					ps2.setString(1, model.getElement_identifier());

					rs = ps2.executeQuery();

					while (rs.next()) {
						// localities_id=rs.getInt(1);
						idPlatform = rs.getInt(1);
						idlocation = rs.getInt(2);
					}
					// System.out.println("idPlatform (elements) : " + idPlatform);
					// System.out.println("idLocation (elements): " + idlocation);

					/////////////////////////// platform/////////////////////////////////
					// System.out.println("Segunda Busqueda : " + platform +" where platfomr = "+
					// idPlatform);
					ps2 = cn2.prepareStatement(platform);
					ps2.setInt(1, idPlatform);

					rs = ps2.executeQuery();
					while (rs.next()) {
						platfomrsDescription = rs.getString(1);
						platformsIdSolution = rs.getInt(2);
					}

					// System.out.println("descripction (platform)" + platfomrsDescription );
					// System.out.println("idsolution (platforn)" + platformsIdSolution );

					/////////////////////////// Solutions/////////////////////////////////
					// System.out.println("Tercera Busqueda : " + solutions + "id = " +
					/////////////////////////// platformsIdSolution);

					ps2 = cn2.prepareStatement(solutions);
					ps2.setInt(1, platformsIdSolution);

					rs = ps2.executeQuery();
					while (rs.next()) {
						solutionsDescription = rs.getString(1);
					}
					////////////////////////////////////////////////////////////////////////
					/////////////////////////// locations/////////////////////////////////
					// System.out.println("Cuarta Busqueda : " + locations + "id = " + idlocation);
					ps2 = cn2.prepareStatement(locations);
					ps2.setInt(1, idlocation);

					rs = ps2.executeQuery();
					while (rs.next()) {
						locationsUbigeo = rs.getString(2);
						idlocation = rs.getInt(1);
					}

					// System.out.println("locations ubigeo : " + locationsUbigeo);
					// System.out.println("localities id : " + idlocation);
					///////////////////////////////////////////////////////////////////////
					/////////////////////////// localities/////////////////////////////////
					// System.out.println("Quinta busqueda :" + localities +" where id="+idlocation
					// );
					ps2 = cn2.prepareStatement(localities);
					ps2.setInt(1, idlocation);

					rs = ps2.executeQuery();
					while (rs.next()) {
						localitiesName = rs.getString(1);
					}
					///////////////////////////////////////////////////////////////////////
					// System.out.println("Locaties_name : " + localitiesName);

				} catch (Exception e) {
					System.out.println("-------------Error de Busqueda");

				}finally{
					cn2.close();
					ps2.close();
					rs.close();
				}
				

				// 11/06/2018
				/*
				 * try (PreparedStatement select = cn.
				 * prepareStatement("SELECT id FROM alarms WHERE element_identifier=? and event_date_end is null order by id desc limit 1"
				 * )) { select.setString(1, model.getElement_identifier());
				 * 
				 * ResultSet rs = select.executeQuery();
				 * 
				 * if (rs.next()) { System.out.println("UPDATE: " + rs.getLong(1));
				 * System.out.println("IS new: " + model.isNew()); System.out.println("Start: "
				 * + model.getEvent_date_start()); System.err.println("End: " +
				 * model.getEvent_date_end()); System.out.println(model);
				 * System.out.println("==========================================="); //UPDATE
				 * Long id = rs.getLong(1); try (PreparedStatement update =
				 * cn.prepareStatement("UPDATE alarms SET event_date_end=? WHERE id=?")) {
				 * update.setLong(2, id); update.setDate(1,
				 * java.sql.Date.valueOf(model.getEvent_date_end().toInstant().atZone(ZoneId.of(
				 * "America/Lima")).toLocalDate())); update.executeUpdate(); } } else {
				 */
				/*
				 * System.out.println("INSERT"); System.out.println("IS new: " + model.isNew());
				 * System.out.println("Start: " + model.getEvent_date_start());
				 * System.err.println("End: " + model.getEvent_date_end());
				 * System.out.println("===========================================");
				 */
				Timestamp dEnd = (model.getEvent_date_end() != null) ? new Timestamp(model.getEvent_date_end().getTime())
						: null;
				Timestamp d = (model.getEvent_date_start() != null) ? new Timestamp(model.getEvent_date_start().getTime())
						: null;

				ps.setString(1, model.getInstance());
				ps.setString(2, model.getSeverity());
				ps.setTimestamp(3, d);
				ps.setTimestamp(4, dEnd);
				ps.setString(5, model.getFault_flag());
				ps.setString(6, model.getEvent_detail());
				ps.setString(7, null);
				ps.setString(8, model.getFault_function());
				ps.setString(9, model.getAlarm_duration());
				ps.setString(10, ip);
				ps.setString(11, lines.toString());
				ps.setString(12, model.getInstance());
				ps.setString(13, model.getElement_identifier());

				ps.setString(14, model.getStatus());
				// System.out.println("Valor para guardar : " + localities_name);
				// ps.setString(15,localities_name);
				ps.setTimestamp(15, new Timestamp(System.currentTimeMillis()));
				ps.setString(16, "[" + platfomrsDescription + "] - [" + solutionsDescription + "]");
				ps.setString(17, "[" + locationsUbigeo + "] - [" + localitiesName + "]");
				ps.setString(18, model.getNbi_sys());
				int validateInsert = ps.executeUpdate();
				if (validateInsert > 0) {
					System.out.println("Alarma Insertada :  " + model.getTypeAlarma() + "   " + new Date());
				} else {
					System.out.println("Alarma NO Insertada :  " + model.getTypeAlarma() + "   " + new Date());
				}

				/*
				 * } }
				 */

			} catch (Exception e) {
				System.out.println("Error");
				e.printStackTrace();
				// Logger.getLogger("incident").warn(e.getMessage());
			}

		}


}
