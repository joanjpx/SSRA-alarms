package com.asistp.main;

import com.asistp.Utils.Conexion;
import com.asistp.main.Bean;
import com.asistp.main.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;


public class ReadFile {
    private static final Logger logger = Logger.getLogger(ReadFile.class);
    private String path;
    private static int lastRead = 0;
    private String filename;
    private boolean closeReadtoRead;
    private SimpleDateFormat sdf;
    private String pathConfig;
    public ReadFile(String filename, String path, boolean closeReadtoRead,String pathConfig) {
        this.filename = filename;
        this.path = path;
        this.closeReadtoRead = closeReadtoRead;
        this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.pathConfig = pathConfig;
    }

    public void initRead() {
        FileReader fr = null;
        BufferedReader br = null;
        if (this.validateDate()) {
            String fileToRead = this.path + System.getProperty("file.separator") + "log-cambium-" + this.filename + ".log";
            System.out.println(fileToRead);
            try {
                String line;
                fr = new FileReader(fileToRead);
                br = new BufferedReader(fr);
                int x = 1;
                while ((line = br.readLine()) != null) {
                    Bean b;
                    lastRead = ReadFile.getLastReadFile(this.filename, this.path);
                    if (++x <= lastRead) continue;
                    ++lastRead;
                    String[] lineSeparate = line.split("#");
                    File f = this.createFile(lineSeparate);
                    if (f != null && (b = this.createBean(f)) != null) {
                        int s = this.save(b);
                        String tablename = this.createTableMensual();
                        if (tablename != null) {
                            this.insetAlarmsMensual(b, tablename);
                        }
                        if (b.getFault_flag().equals("Active")) {
                            this.saveAlarms2(b);
                        } else if (b.getFault_flag().equals("Cleared")) {
                            this.updateActive(b);
                            this.updateActiveMensual(b, tablename);
                        }
                        if (s == 1) {
                            System.out.println("Registro Guardado  :" + lastRead);
                        } else {
                            System.out.println("Registro No Guardado  :" + lastRead);
                        }
                    }
                    ReadFile.writefileLastRead(this.filename, this.path, lastRead);
                }
                fr.close();
                br.close();
                if (!this.closeReadtoRead) {
                    lastRead = 0;
                }
                return;
            }
            catch (Exception e) {
                System.out.println("Archivo no encontrado");
                return;
            }
        }
    }

    public File createFile(String[] lineSeparate) {
        File f = new File();
        Field[] fields = f.getClass().getDeclaredFields();
        for (int x = 0; x < fields.length; ++x) {
            try {
                fields[x].set(f, lineSeparate[x]);
                continue;
            }
            catch (Exception e) {
                int y = lastRead + 1;
                logger.error((Object)("Campos incompletos Revisar Archivo : " + this.filename + "--> Registro " + y));
                f = null;
                return f;
            }
        }
        return f;
    }

    public int save(Bean bean) {
        int i = 0;
        String sql = "INSERT INTO alarms (severity,event_id,event_date_start,event_date_end,event_description,probable_cause,element_identifier,element_ip,alarm_duration,alarm_identifier,instance,event_detail,additional_information,fault_flag,fault_function,ip_origin,status,ciudad,sistema,stamp,nbi_sys) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
        try {
            Conexion con = new Conexion(pathConfig);
            Connection c = con.getConexion();
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, bean.getSeverity());
            ps.setString(2, bean.getEvent_id());
            ps.setTimestamp(3, bean.getEvent_date_start());
            ps.setTimestamp(4, bean.getEvent_date_end());
            ps.setString(5, bean.getEvent_description());
            ps.setString(6, bean.getProbable_cause());
            ps.setString(7, bean.getElement_identifier());
            ps.setString(8, bean.getElement_ip());
            ps.setString(9, bean.getAlarm_duration());
            ps.setString(10, bean.getAlarm_identifier());
            ps.setString(11, bean.getInstance());
            ps.setString(12, bean.getEvent_detail());
            ps.setString(13, bean.getAdditional_information());
            ps.setString(14, bean.getFault_flag());
            ps.setString(15, bean.getFault_function());
            ps.setString(16, bean.getIp_origin());
            ps.setString(17, bean.getStatus());
            ps.setString(18, bean.getCiudad());
            ps.setString(19, bean.getSistema());
            ps.setTimestamp(20, bean.getStamp());
            ps.setString(21, bean.getNbi_sys());
            i = ps.executeUpdate();
            ps.close();
            c.close();
            con.cerrarConexion();
        }
        catch (Exception e) {
            logger.debug((Object)("Error al guardar en Tabla Alarms + " + e));
        }
        return i;
    }

    public int saveAlarms2(Bean bean) {
        int i = 0;
        String sql = "INSERT INTO alarms2 (severity,event_id,event_date_start,event_date_end,event_description,probable_cause,element_identifier,element_ip,alarm_duration,alarm_identifier,instance,event_detail,additional_information,fault_flag,fault_function,ip_origin,status,ciudad,sistema,stamp,nbi_sys) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
        try {
            Conexion con = new Conexion(pathConfig);
            Connection c = con.getConexion();
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, bean.getSeverity());
            ps.setString(2, bean.getEvent_id());
            ps.setTimestamp(3, bean.getEvent_date_start());
            ps.setTimestamp(4, bean.getEvent_date_end());
            ps.setString(5, bean.getEvent_description());
            ps.setString(6, bean.getProbable_cause());
            ps.setString(7, bean.getElement_identifier());
            ps.setString(8, bean.getElement_ip());
            ps.setString(9, bean.getAlarm_duration());
            ps.setString(10, bean.getAlarm_identifier());
            ps.setString(11, bean.getInstance());
            ps.setString(12, bean.getEvent_detail());
            ps.setString(13, bean.getAdditional_information());
            ps.setString(14, bean.getFault_flag());
            ps.setString(15, bean.getFault_function());
            ps.setString(16, bean.getIp_origin());
            ps.setString(17, bean.getStatus());
            ps.setString(18, bean.getCiudad());
            ps.setString(19, bean.getSistema());
            ps.setTimestamp(20, bean.getStamp());
            ps.setString(21, bean.getNbi_sys());
            i = ps.executeUpdate();
            ps.close();
            c.close();
            con.cerrarConexion();
        }
        catch (Exception e) {
            logger.error((Object)("Error en guardar en alarms2 " + e));
        }
        return i;
    }

    public Bean createBean(File f) {
        Bean b = new Bean();
        try {
            b.setSeverity("critical");
            int posinit = f.getColumn1().indexOf(":", 26);
            String Date2 = f.getColumn1().substring(posinit + 1, posinit + 21);
            try {
                this.sdf.parse(Date2);
                b.setEvent_date_start(new Timestamp(this.sdf.parse(Date2).getTime()));
            }
            catch (Exception e) {
                logger.error((Object)("Error en Convertir a Fecha : " + Date2 + " en Archivo : " + this.filename + "  En Registro  :" + (lastRead + 1)));
            }
            String event_description = f.getColumn10().substring(51, f.getColumn10().length() - 1);
            b.setEvent_description(event_description);
            String element_identifier = f.getColumn5().substring(f.getColumn5().indexOf("\"") + 1, f.getColumn5().length() - 1);
            b.setElement_identifier(element_identifier);
            b.setAdditional_information(f.toString());
            String fault_flag = f.getColumn11().substring(f.column11.length() - 2, f.column11.length()).trim();
            if (fault_flag.equals("1")) {
                b.setFault_flag("Active");
            } else if (fault_flag.equals("3")) {
                b.setFault_flag("Cleared");
            }
            b.setStamp(new Timestamp(new Date().getTime()));
            b.setNbi_sys("FTP/CAMBIUM");
        }
        catch (ArrayIndexOutOfBoundsException e) {
            b = null;
            logger.error((Object)("Revisar Longitud de las Columnas  En el Archivo : " + this.filename + "  Registros : " + lastRead + 1));
        }
        catch (Exception e) {
            b = null;
            logger.error((Object)"createBean()<---Error");
        }
        return b;
    }

    public boolean validateDate() {
        if (this.closeReadtoRead) {
            String today;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                today = sdf.format(new Date());
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            if (today.equals(this.filename)) {
                return true;
            }
            lastRead = 0;
            return false;
        }
        return true;
    }

    public void setModel_Alarms_Translator(Bean model) {
        System.out.print("Modelo severity y description " + model.getSeverity() + " -- " + model.getEvent_description());
        ResultSet rs = null;
        boolean validateupdate = false;
        String sqlupdate = "SELECT gilat_severity,gilat_description FROM alarms_translator WHERE nbi_sys=? and o_severity = ? and o_probablecause=? ";
        Conexion con = new Conexion(pathConfig);
        try {
            Connection c = con.getConexion();
            PreparedStatement ps = c.prepareStatement(sqlupdate);
            ps.setString(1, model.getNbi_sys());
            ps.setString(2, model.getSeverity());
            ps.setString(3, model.getProbable_cause());
            rs = ps.executeQuery();
            while (rs.next()) {
                model.setSeverity(rs.getString(1));
                model.setEvent_description(rs.getString(2));
            }
            ps.close();
            con.cerrarConexion();
        }
        catch (Exception e) {
            try {
                rs.close();
            }
            catch (Exception ps) {
                // empty catch block
            }
        }
        System.out.print("Modelo severity y description (new From Translator) " + model.getSeverity() + " -- " + model.getEvent_description());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String createTableMensual() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String SdateTitle = "zAlarms" + sdf.format(new Date());
        ResultSet rs = null;
        Conexion con = new Conexion(pathConfig);
        String sqlCreateTable = "CREATE TABLE zAlarms" + SdateTitle + " (id int primary key AUTO_INCREMENT, severity varchar(100), event_date_start timestamp, event_date_end timestamp, event_description varchar(255), element_identifier varchar(150), fault_flag varchar(20), stamp timestamp,nbi_sys varchar(15), solution varchar(100), locality varchar(100))";
        try {
            try (Connection cn = con.getConexion();){
                DatabaseMetaData dbm = cn.getMetaData();
                rs = dbm.getTables(null, null, "zAlarms" + SdateTitle, null);
                if (rs.next()) {
                    System.out.println("la Tabla zAlarms" + SdateTitle + " <---Existe");
                } else {
                    Statement st = cn.createStatement();
                    st.execute(sqlCreateTable);
                    st.close();
                }
                cn.close();
                rs.close();
            }
        }
        catch (Exception e) {
            logger.error((Object)"createTableMensual()<--Error");
            SdateTitle = null;
        }
        finally {
            try {
                rs.close();
            }
            catch (Exception e) {}
        }
        return SdateTitle;
    }

    public int updateActive(Bean model) {
        System.out.println("Entrando updateActive");
        Conexion con = new Conexion(pathConfig);
        int validateupdate = 0;
        String sqlupdate = "update alarms2 set event_date_end=?  where event_date_end is null and nbi_sys=? order by id desc limit 1 ";
        try {
            try (Connection cn = con.getConexion();
                 PreparedStatement ps = cn.prepareStatement(sqlupdate);){
                ps.setTimestamp(1, new Timestamp(model.getEvent_date_start().getTime()));
                ps.setString(2, model.getNbi_sys());
                validateupdate = ps.executeUpdate();
                cn.close();
                ps.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return validateupdate;
        }
        return validateupdate;
    }

    public int updateActiveMensual(Bean model, String tablename) {
        Conexion con = new Conexion(pathConfig);
        int validateupdate = 0;
        String sqlupdate = "update " + tablename + " set event_date_end=? where nbi_sys=? and event_date_end is null and element_identifier=?";
        try {
            try (Connection cn = con.getConexion();
                 PreparedStatement ps = cn.prepareStatement(sqlupdate);){
                ps.setTimestamp(1, new Timestamp(model.getEvent_date_start().getTime()));
                ps.setString(2, model.getNbi_sys());
                ps.setString(3, model.getElement_identifier());
                validateupdate = ps.executeUpdate();
                cn.close();
                ps.close();
            }
        }
        catch (Exception e) {
            logger.error((Object)("updateActiveMensual()  " + e));
            validateupdate = 0;
        }
        return validateupdate;
    }

    public void insetAlarmsMensual(Bean model, String tablaname) {
        Conexion con = new Conexion(pathConfig);
        String insertTableSQL = "INSERT INTO " + tablaname + " (severity, event_date_start, event_date_end,event_description, element_identifier, fault_flag, stamp,solution,locality,nbi_sys) VALUES (?,?,?,?,?,?,?,?,?,?)";
        String Elements = "select platforms,location_id from elements where identifier=?";
        String platform = "select description, solution_id  from platforms where id=?";
        String solutions = "select description from solutions where id=?";
        String locations = "select localitie_id,ubigeo from locations where id=?";
        String localities = "select name from localities where id=?";
        try {
            try (Connection cn = con.getConexion();
                 PreparedStatement ps = cn.prepareStatement(insertTableSQL);){
                int idlocation = 0;
                int idPlatform = 0;
                String platfomrsDescription = null;
                int platformsIdSolution = 0;
                String locationsUbigeo = null;
                String localitiesName = null;
                String solutionsDescription = null;
                Connection cn2 = null;
                PreparedStatement ps2 = null;
                try {
                    cn2 = con.getConexion();
                    ps2 = cn2.prepareStatement(Elements);
                    String mac = model.getElement_identifier();
                    mac = mac.substring(1, mac.length() - 1);
                    ps2.setString(1, model.getElement_identifier());
                    ResultSet rs = ps2.executeQuery();
                    while (rs.next()) {
                        idPlatform = rs.getInt(1);
                        idlocation = rs.getInt(2);
                    }
                    ps2 = cn2.prepareStatement(platform);
                    ps2.setInt(1, idPlatform);
                    rs = ps2.executeQuery();
                    while (rs.next()) {
                        platfomrsDescription = rs.getString(1);
                        platformsIdSolution = rs.getInt(2);
                    }
                    ps2 = cn2.prepareStatement(solutions);
                    ps2.setInt(1, platformsIdSolution);
                    rs = ps2.executeQuery();
                    while (rs.next()) {
                        solutionsDescription = rs.getString(1);
                    }
                    ps2 = cn2.prepareStatement(locations);
                    ps2.setInt(1, idlocation);
                    rs = ps2.executeQuery();
                    while (rs.next()) {
                        locationsUbigeo = rs.getString(2);
                        idlocation = rs.getInt(1);
                    }
                    ps2 = cn2.prepareStatement(localities);
                    ps2.setInt(1, idlocation);
                    rs = ps2.executeQuery();
                    while (rs.next()) {
                        localitiesName = rs.getString(1);
                    }
                    cn2.close();
                    ps2.close();
                }
                catch (Exception e) {
                    logger.error((Object)("Error al Buscar Sistema Ciudad " + e + " -->" + e.getMessage()));
                }
                Timestamp dEnd = model.getEvent_date_end() != null ? new Timestamp(model.getEvent_date_end().getTime()) : null;
                Timestamp d = model.getEvent_date_start() != null ? new Timestamp(model.getEvent_date_start().getTime()) : null;
                ps.setString(1, model.getSeverity());
                ps.setTimestamp(2, d);
                ps.setTimestamp(3, dEnd);
                ps.setString(4, model.getInstance());
                ps.setString(5, model.getElement_identifier());
                ps.setString(6, model.getFault_flag());
                ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
                ps.setString(8, "[" + platfomrsDescription + "] - [" + solutionsDescription + "]");
                ps.setString(9, "[" + locationsUbigeo + "] - [" + localitiesName + "]");
                ps.setString(10, model.getNbi_sys());
                int validateInsert = ps.executeUpdate();
                if (validateInsert > 0) {
                    System.out.println("Alarma Insertada (TABLA MENSUAL):  " + model.getNbi_sys() + "   " + new Date());
                } else {
                    System.out.println("Alarma NO Insertada (TABLA MENSUAL):  " + model.getNbi_sys() + "   " + new Date());
                }
            }
        }
        catch (Exception e) {
            logger.error((Object)"Error en insertar en Tabla Mensual");
        }
    }

    public static int getLastReadFile(String filename, String path) {
        java.io.File file = new java.io.File(path + java.io.File.separator + filename + ".last");
        int lastpost = 0;
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = "";
                while ((line = br.readLine()) != null) {
                    try {
                        lastpost = Integer.parseInt(line);
                    }
                    catch (Exception e) {
                        logger.error((Object)("getLastReadFile()->Error al tratar de Convertir Numero -> " + line + "  En Archivo : " + filename + ".last"));
                    }
                }
                br.close();
            }
            catch (Exception e) {
                logger.error((Object)"getLastReadFile()->Error al Tratar de leer Archivo");
            }
        }
        return lastpost;
    }

    public static void writefileLastRead(String filename, String path, int post) {
        try {
            FileWriter fw = new FileWriter(path + java.io.File.separator + filename + ".last");
            fw.write(String.valueOf(post));
            fw.close();
        }
        catch (Exception e) {
            logger.error((Object)("fileLastRead()->Error Al crear Archivo: " + filename + ".last " + e));
        }
    }
}