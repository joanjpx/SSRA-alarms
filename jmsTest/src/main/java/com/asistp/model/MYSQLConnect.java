/*
 * Decompiled with CFR 0.145.
 */
package com.asistp.model;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class MYSQLConnect {
    private Connection conexion = null;
    private String servidor = "10.255.42.123";
    private String database = "gilatsmrt";
    private String usuario = "fitel_userbd";
    private String password = "bdg1lat";
    public String pathFileConfig = "";

    public Connection getConexion(String conectionType) throws ClassNotFoundException, SQLException {
        this.init();
        if(conectionType!=null) {
        	this.initOtherServer();
        }
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://" + this.servidor + "/" + this.database;
        this.conexion = DriverManager.getConnection(url, this.usuario, this.password);
        return this.conexion;
    }

    public void cerrarConexion() throws SQLException {
        this.conexion.close();
        this.conexion = null;
    }

    public void cerrarConexion(Connection con, ResultSet rs, CallableStatement cs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
        if (cs != null) {
            cs.close();
        }
        if (con != null) {
            con.close();
        }
    }

    public void cerrarConexion(Connection con, CallableStatement cs) throws SQLException {
        if (cs != null) {
            cs.close();
        }
        if (con != null) {
            con.close();
        }
    }

    public void init() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(String.valueOf(this.pathFileConfig) + ".env"));
            this.servidor = prop.getProperty("DB_HOST");
            this.database = prop.getProperty("DB_DATABASE");
            this.usuario = prop.getProperty("DB_USERNAME");
            this.password = prop.getProperty("DB_PASSWORD");
            System.out.println("Parametros para la conexion a la base de datos ->>>> : " + this.servidor + "----" + this.database + "------" + this.usuario + "--------" + this.password);
        }
        catch (Exception e) {
        	e.printStackTrace();
            System.out.println("Error al Tratar de Obtener Datos del Archivo de Configuracion para la Base de Datos");
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
        	e.printStackTrace();
            System.out.println("Error al Tratar de Obtener Datos del Archivo de Configuracion para la Base de Datos");
            this.servidor = null;
            this.database = null;
            this.usuario = null;
            this.password = null;
        }
    }
}
