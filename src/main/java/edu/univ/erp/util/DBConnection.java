package edu.univ.erp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;




public class DBConnection {
    // Loading the db.properties stored in db.properties file
    private static Properties loadProperties(){
        Properties props = new Properties();
        try {
            props.load(DBConnection.class.getResourceAsStream("/db.properties"));
            return props;
        }
        catch (Exception e){
            return null;
        }
    }

    public static Connection getAuthConnection() {
        try{
            Properties props = loadProperties();
            return DriverManager.getConnection(
                    props.getProperty("db.auth.url"),
                    props.getProperty("db.auth.user"),
                    props.getProperty("db.auth.password")
            );

        } catch (Exception e) {
            System.out.println("Auth DB Connection Failed" + e.getMessage());
            return null;
        }
    }

    public static Connection getErpConnection() {
        try{
            Properties props = loadProperties();
            return DriverManager.getConnection(
                    props.getProperty("db.erp.url"),
                    props.getProperty("db.erp.user"),
                    props.getProperty("db.erp.password")
            );

        } catch (Exception e) {
            System.out.println("ERP DB Connection Failed" + e.getMessage());
            return null;
        }
    }

    public static Connection getGeneralConnection() {
        try{
            Properties props = loadProperties();
            return DriverManager.getConnection(
                    props.getProperty("db.general.url"),
                    props.getProperty("db.general.user"),
                    props.getProperty("db.general.password")
            );

        } catch (Exception e) {
            System.out.println("ERP DB Connection Failed" + e.getMessage());
            return null;
        }
    }


    public static void main(String[] args){
        if(getAuthConnection() != null){
            System.out.println("Auth DB Connection Successful");
        }
        if(getErpConnection() != null){
            System.out.println("Erp DB Connection Successful");
        }
    }


}
