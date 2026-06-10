package edu.univ.erp.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInit {

    private static void runScript(String filePath, Connection conn){
        if(conn == null){
            return;
        }

        try{
            Statement statement = conn.createStatement();
            InputStream inputStream = DatabaseInit.class.getClassLoader().getResourceAsStream(filePath);

            if(inputStream == null){
                System.err.println("Unable to load file " + filePath);
                return;
            }

            String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            statement.execute(sql);

            System.out.println("Executed Successfully the Statement: " + sql);
        } catch (Exception e) {
            System.err.println("Error while trying to load DB connection");
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting Database Initialization");
        runScript("db/migrations/AuthSchema.sql", DBConnection.getGeneralConnection());
        runScript("db/migrations/ERPSchema.sql", DBConnection.getGeneralConnection());
        runScript("db/migrations/DBRestore.sql", DBConnection.getErpConnection());

        System.out.println("Finished Database Initialization");

    }
}




