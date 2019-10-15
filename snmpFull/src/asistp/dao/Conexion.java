/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.dao;

import java.io.FileInputStream;
//import com.mysql.jdbc.Connection;
import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Usuario
 */
public class Conexion {

    private Connection conexion = null;
    private  String servidor = "10.255.42.123";//smrt 10.255.x.123  ssra 10.254.x.116
    private  String database = "gilatsmrt"; //     smrt                      ssra2;
    private  String usuario = "root";
    private  String password = "123456*abc";
    public  String pathFileConfig = "";
    //private String url = "";

    public Connection getConexion(String conectionType) throws ClassNotFoundException, SQLException {
    	this.init();
    	if(conectionType!=null) {
    		this.initOtherServer();
    	}
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://" + servidor + "/" + database;
            conexion = DriverManager.getConnection(url, usuario, password);
            return conexion;
        } catch (SQLException | ClassNotFoundException ex) {
            throw ex;
        }
    }

    public void cerrarConexion() throws SQLException {
        try {
            conexion.close();
            conexion = null;
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public void cerrarConexion(Connection con, ResultSet rs, CallableStatement cs)
            throws SQLException {
        try {
            if (rs != null) {
                rs.close();
            }
            if (cs != null) {
                cs.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public void cerrarConexion(Connection con, CallableStatement cs)
            throws SQLException {
        try {
            if (cs != null) {
                cs.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException ex) {
            throw ex;
        }
    }
    
    public void init() {
        final Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(String.valueOf(this.pathFileConfig) + ".env"));
            this.servidor = prop.getProperty("DB_HOST");
            this.database = prop.getProperty("DB_DATABASE");
            this.usuario = prop.getProperty("DB_USERNAME");
            this.password = prop.getProperty("DB_PASSWORD");
            System.out.println("Conexion Exitosa ->>>> : " + this.servidor + "----" + this.database + "------" + this.usuario + "--------" + this.password);
        }
        catch (Exception e) {
        	
            System.out.println("Error al Tratar de Obtener Datos del Archivo de Configuracion para la Base de Datos 1 " + pathFileConfig);
        }
    }
    
    public void initOtherServer() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(String.valueOf(this.pathFileConfig) + ".env"));
            this.servidor = prop.getProperty("DB_HOST_SERVER");
            this.database = prop.getProperty("DB_DATABASE_SERVER");
            this.usuario = prop.getProperty("DB_USERNAME_SERVER");
            this.password = prop.getProperty("DB_PASSWORD_SERVER");
            System.out.println("Parametros para la conexion a la base de datos ->>>> : " + this.servidor + "----" + this.database + "------" + this.usuario + "--------" + this.password);
        }
        catch (Exception e) {
        	
            System.out.println("Error al Tratar de Obtener Datos del Archivo de Configuracion para la Base de Datos" + pathFileConfig);
            this.servidor = null;
            this.database = null;
            this.usuario = null;
            this.password = null;
        }
    }
}
