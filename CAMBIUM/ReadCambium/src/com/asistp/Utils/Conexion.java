package com.asistp.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;

public class Conexion {

    private Connection conexion = null;
    private  String servidor = "localhost";
    private  String database = "gilatssra2";
    private  String usuario = "root";
    private  String password = "123456*abc";
    private  String pathConfig;

    public Conexion(String pathConfig) {
        this.pathConfig = pathConfig;
        init();
    }

    public Connection getConexion() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://"+this.servidor+"/"+this.database;
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
        Properties properties = new Properties();
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(new FileInputStream(this.pathConfig), "UTF-8");
            properties.load(in);
            this.servidor = properties.getProperty("DB_HOST");
            this.database = properties.getProperty("DB_DATABASE");
            this.usuario = properties.getProperty("DB_USERNAME");
            this.password = properties.getProperty("DB_PASSWORD");
            
        }catch(Exception e){
            
            System.out.println("Erro al Leer Archivo de configuracion Base de datos");
        }finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException ex) {
                }
            }
        }

    }
}
